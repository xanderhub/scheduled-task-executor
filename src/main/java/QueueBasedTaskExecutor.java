import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

/**
 * Scheduled executor based on {@link DelayQueue} which also {@link java.util.concurrent.BlockingQueue}
 * {@link Callable} items (tasks) are wrapped in {@link ScheduledTask} and placed into DelayQueue (backlog)
 * ScheduledTask class implements {@link java.util.concurrent.Delayed} interface. This provides ability to
 * order tasks by their execution times:
 * {@code getDelay} method calculates the diff (delay) in milliseconds between {@code LocalDateTime.now()}
 * and {@code executionTime} of ScheduledTask object. The head of the queue will be the task with the lowest delay.
 * <p>
 *     In case the number of items (tasks) in the queue is bigger than threshold the first task in the queue (the head)
 *     will be re-scheduled i.e. its delay will be set to zero and re-inserted into the queue.
 *     This operation performed by {@code prioritize()} on each enqueue and dequeue operation until
 *     number of tasks in the queue becomes smaller than threshold.
 * </p>
 */
public class QueueBasedTaskExecutor implements ScheduledTaskExecutor  {

    private final static Logger LOG = Logger.getLogger(QueueBasedTaskExecutor.class.getName());
    private DelayQueue<ScheduledTask> scheduledTasks;
    private int queueThreshold;

    /**
     * Creates a new {@code QueueBasedTaskExecutor} and starting task processing thread {@code startExecutor()}.
     */
    public QueueBasedTaskExecutor(int threshold) {
        this.scheduledTasks = new DelayQueue<>();
        this.queueThreshold = threshold;
        startExecutor();
    }

    /**
     * Creates a new {@code QueueBasedTaskExecutor} with default threshold if wasn't defined.
     */
    public QueueBasedTaskExecutor() {
        this(8);
    }

    /**
     * Schedules the task for predefined time i.e. adds task to the DelayQueue
     * Checks if threshold is reached and first tasks can be executed earlier
     *
     * @param executionTime time to execute Callable item
     * @param task Callable item to run
     * @return the result of {@code Callable} scheduledTask as FutureTask object
     */
    @Override
    public <T> FutureTask<T> schedule(LocalDateTime executionTime, Callable<T> task){
        FutureTask<T> futureTask = new FutureTask<>(task);
        scheduledTasks.add(new ScheduledTask<>(executionTime, futureTask));
        if(needToPrioritize()){
            prioritize();
        }
        return futureTask;
    }

    private ScheduledTask getNextTask() {
        return scheduledTasks.peek();
    }

    @Override
    public int waitingTasks() {
        return scheduledTasks.size();
    }

    @Override
    public void setThreshold(int value) {
        this.queueThreshold = value;
    }

    @Override
    public int getThreshold() {
        return queueThreshold;
    }

    /**
     * Starts processing tasks from the queue in a separate thread.
     * Checks if threshold is reached and first tasks can be executed earlier
     */
    private void startExecutor() {
        new Thread(() -> {
            try {
                while (Thread.currentThread().isAlive()) {
                    scheduledTasks.take().getTask().run();
                    if (needToPrioritize()){
                        prioritize();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @SuppressWarnings("ConstantConditions")
    private boolean needToPrioritize() {
        return waitingTasks() > getThreshold() && getNextTask().getDelayMillis() > 0L;
    }

    /**
     * Prioritizes the first task in the DelayQueue by re-inserting it with zero delay.
     * Method used internally by QueueBasedTaskExecutor in case threshold has been passed.
     */
    private synchronized void prioritize() {
        if (needToPrioritize()) {
            ScheduledTask nextTask = getNextTask();
            if(nextTask != null) {
                LOG.info("Prioritizing next task: " + nextTask);
                scheduledTasks.remove(nextTask);
                nextTask.setToRunNow();
                scheduledTasks.put(nextTask);
            }
        }
    }
}


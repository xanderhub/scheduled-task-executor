import jdk.nashorn.internal.codegen.CompilerConstants;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class QueueBasedTaskExecutor implements ScheduledTaskExecutor {
    private final static Logger LOG = Logger.getLogger(QueueBasedTaskExecutor.class.getName());
    private DelayQueue<ScheduledTask> scheduledTasks;
    private int queueThreshold;

    public QueueBasedTaskExecutor(int threshold) {
        this.scheduledTasks = new DelayQueue<>();
        this.queueThreshold = threshold;
        startExecutor();
    }

    public QueueBasedTaskExecutor() {
        this(8);
    }

    public int getQueueThreshold() {
        return queueThreshold;
    }

    public void setQueueThreshold(int queueThreshold) {
        this.queueThreshold = queueThreshold;
    }

    @Override
    public <T> FutureTask<T> schedule(LocalDateTime executionTime, Callable<T> task) {
        FutureTask<T> futureTask = new FutureTask<>(task);
        scheduledTasks.add(new ScheduledTask<>(executionTime, futureTask));
        if(needToPrioritize()){
            prioritize();
        }
        return futureTask;
    }

    @Override
    public int waitingTasks() {
        return scheduledTasks.size();
    }

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
        return scheduledTasks.size() > getQueueThreshold() && scheduledTasks.peek().getDelayMillis() > 0L;
    }

    private synchronized void prioritize() {
        if (needToPrioritize()) {
            ScheduledTask nextTask = scheduledTasks.peek();
            if(nextTask != null) {
                LOG.info("Prioritizing next task: " + nextTask);
                scheduledTasks.remove(nextTask);
                nextTask.setToRunNow();
                scheduledTasks.put(nextTask);
            }
        }
    }
}


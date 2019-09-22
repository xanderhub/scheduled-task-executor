import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Scheduled executor for {@link Callable} tasks.
 * Responsible to run these tasks in predefined execution time {@link LocalDateTime}.
 * If execution time is the same for multiple tasks they will be processed in the order they were created.
 * This executor designed to run waiting tasks earlier in case threshold of backlogged tasks is passed.
 * It also provides results for executed tasks by returning {@link FutureTask} for each scheduled task.
 *
 * @author Alexander Ivanov
 */
public interface ScheduledTaskExecutor {
    /**
     * Schedules the task for predefined time.
     *
     * @param executionTime time to execute Callable item
     * @param scheduledTask Callable item to run
     * @return the result of {@code Callable} scheduledTask
     */
    <T> FutureTask<T> schedule(LocalDateTime executionTime, Callable<T> scheduledTask);

    /**
     * @return number of waiting items (tasks) to be executed
     */
    int waitingTasks();

    /**
     * @param value sets threshold value for backlogged items.
     *              If crossed first items in backlog will be executed earlier.
     */
    void setThreshold(int value);

    /**
     * @return retrieves threshold value for backlogged items.
     *              If crossed first items in backlog will be executed earlier.
     */
    int getThreshold();
}

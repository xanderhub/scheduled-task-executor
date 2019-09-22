import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

/**
 * Used internally by implementations of {@link ScheduledTaskExecutor} which based on {@link DelayQueue}
 * Used as a wrapper of {@link Callable} items (tasks) and provides results by returning FutureTask<T> {@code getTask()}
 * Compared to each other by {@code compareTo()} method. By executionTime and then by creationTime.
 */
public class ScheduledTask<T> implements Delayed {
    private final LocalDateTime creationTime;
    private LocalDateTime executionTime;
    private FutureTask<T> task;


    ScheduledTask(LocalDateTime executionTime, FutureTask<T> task) {
        this.creationTime = LocalDateTime.now();
        this.executionTime = executionTime;
        this.task = task;
    }

    void setToRunNow() {
        executionTime = LocalDateTime.now();
    }

    long getDelayMillis() {
        return ChronoUnit.MILLIS.between(LocalDateTime.now(), executionTime);

    }

    FutureTask<T> getTask() {
        return this.task;
    }


    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(getDelayMillis(), TimeUnit.MILLISECONDS);
    }

    public int compareTo(Delayed o) {
        return (this.getDelayMillis() < ((ScheduledTask) o).getDelayMillis()) ? -1 :
                ((this.getDelayMillis() > ((ScheduledTask) o).getDelayMillis()) ? 1 :
                        this.creationTime.compareTo(((ScheduledTask) o).creationTime));
    }

    @Override
    public String toString() {
        return "[Creation time: " + creationTime + ", Execution time: " + executionTime + "]";
    }
}

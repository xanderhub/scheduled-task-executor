import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class ScheduledTask<T> implements Delayed {
    private final LocalDateTime creationTime;
    private LocalDateTime executionTime;
    private FutureTask<T> task;


    public ScheduledTask(LocalDateTime executionTime, FutureTask<T> task) {
        this.creationTime = LocalDateTime.now();
        this.executionTime = executionTime;
        this.task = task;
    }

    public void setToRunNow(){
        executionTime = LocalDateTime.now();
    }

//    private long getDelayMillis() {
//        return ChronoUnit.MILLIS.between(LocalDateTime.now(),
//                executionTime.minusNanos(TimeUnit.MILLISECONDS.toNanos(correction)));
//
//    }

    protected long getDelayMillis() {
        return ChronoUnit.MILLIS.between(LocalDateTime.now(), executionTime);

    }

    public FutureTask<T> getTask() {
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

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public interface ScheduledTaskExecutor {
    <T> FutureTask<T> schedule(LocalDateTime executionTime, Callable<T> scheduledTask);
    int waitingTasks();
}

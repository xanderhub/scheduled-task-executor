import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class CallableTask implements Callable<CallableTask.Result> {

    private String name;
    private int duration;

    public CallableTask(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    @Override
    public Result call() throws Exception {
        System.out.println("Executing: " + name);
        Result result = new Result(name, LocalDateTime.now());
        Thread.sleep(duration);
        return result;
    }

    public class Result {
        private String name;
        private LocalDateTime executionStartTime;

        public Result(String name, LocalDateTime executionStartTime) {
            this.name = name;
            this.executionStartTime = executionStartTime;
        }

        public String getName() {
            return name;
        }

        public LocalDateTime getExecutionStartTime() {
            return executionStartTime;
        }
    }
}

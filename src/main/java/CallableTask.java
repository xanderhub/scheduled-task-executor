import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class CallableTask implements Callable<String> {

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
    public String call() throws Exception {
        Thread.sleep(duration);
        System.out.println(name + " - executed at " + LocalDateTime.now());
        return "Task [" + name + "] completed";
    }
}

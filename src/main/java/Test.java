import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {
    private final static Logger LOG = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) throws Exception {

        ScheduledTaskExecutor executor = new QueueBasedTaskExecutor(3);

        FutureTask<String> task1 = executor.schedule(LocalDateTime.now().plusSeconds(5), new CallableTask("Vasya", 1000));
        FutureTask<String> task2 = executor.schedule(LocalDateTime.now().plusSeconds(7), new CallableTask("Petya", 1000));
        FutureTask<String> task3 = executor.schedule(LocalDateTime.now().plusSeconds(9), new CallableTask("Alex", 1000));
        FutureTask<String> task4 = executor.schedule(LocalDateTime.now().plusSeconds(12), new CallableTask("Fedor", 1000));
        FutureTask<String> task5 = executor.schedule(LocalDateTime.now().plusSeconds(2), new CallableTask("Valera", 1000));

        Thread.sleep(20000);
        //System.out.println(task5.isDone());
        System.out.println(executor.waitingTasks());



// …


//        executor.schedule(time, "TestTask2");
//        executor.schedule(time, "TestTask3");
//        executor.schedule(time, "TestTask4");
//        executor.schedule(time, "TestTask5");
    }

}

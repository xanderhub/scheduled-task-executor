import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ScheduledTaskExecutorTest {

    private ScheduledTaskExecutor executor = new QueueBasedTaskExecutor(10);
    private CallableTask firstTask = new CallableTask("First", 100);
    private CallableTask secondTask = new CallableTask("Second", 100);
    private CallableTask thirdTask = new CallableTask("Third", 100);
    private CallableTask fourthTask = new CallableTask("Fourth", 100);
    private CallableTask fifthTask = new CallableTask("Fifth", 100);

    @Test
    public void whenSchedulingTask_thenGettingResultFromThatTask() throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Assert.assertEquals("Equals: ", firstTask.getName(), executor.schedule(now.plusSeconds(1), firstTask).get().getName());
        Assert.assertEquals("Equals: ", secondTask.getName(), executor.schedule(now.plusSeconds(2), secondTask).get().getName());
        Assert.assertEquals("Equals: ", thirdTask.getName(), executor.schedule(now.plusSeconds(1), thirdTask).get().getName());

    }

    @Test
    public void whenSchedulingTasks_thenEachTaskExecutedAtScheduledTime() throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        FutureTask<CallableTask.Result> second = executor.schedule(now.plusSeconds(2), secondTask);
        FutureTask<CallableTask.Result> fifth = executor.schedule(now.plusSeconds(5), fifthTask);
        FutureTask<CallableTask.Result> third = executor.schedule(now.plusSeconds(3), thirdTask);
        FutureTask<CallableTask.Result> first = executor.schedule(now.plusSeconds(1), firstTask);
        FutureTask<CallableTask.Result> fourth = executor.schedule(now.plusSeconds(4), fourthTask);

        Assert.assertTrue(now.plus(999L, ChronoUnit.MILLIS).isBefore(first.get().getExecutionStartTime())
                            && now.plus(1005L, ChronoUnit.MILLIS).isAfter(first.get().getExecutionStartTime()));
        Assert.assertTrue(now.plus(1999L, ChronoUnit.MILLIS).isBefore(second.get().getExecutionStartTime())
                            && now.plus(2005L, ChronoUnit.MILLIS).isAfter(second.get().getExecutionStartTime()));
        Assert.assertTrue(now.plus(2999L, ChronoUnit.MILLIS).isBefore(third.get().getExecutionStartTime())
                            && now.plus(3005L, ChronoUnit.MILLIS).isAfter(third.get().getExecutionStartTime()));
        Assert.assertTrue(now.plus(3999L, ChronoUnit.MILLIS).isBefore(fourth.get().getExecutionStartTime())
                            && now.plus(4005L, ChronoUnit.MILLIS).isAfter(fourth.get().getExecutionStartTime()));
        Assert.assertTrue(now.plus(4999L, ChronoUnit.MILLIS).isBefore(fifth.get().getExecutionStartTime())
                            && now.plus(5005L, ChronoUnit.MILLIS).isAfter(fifth.get().getExecutionStartTime()));
    }

    @Test
    public void whenSchedulingTasksWithSameExecutionTime_thenTasksOrderedByCreationTime() throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        FutureTask<CallableTask.Result> first = executor.schedule(now.plusSeconds(1), firstTask);
        Thread.sleep(1);
        FutureTask<CallableTask.Result> second = executor.schedule(now.plusSeconds(1), secondTask);
        Thread.sleep(1);
        FutureTask<CallableTask.Result> third = executor.schedule(now.plusSeconds(1), thirdTask);
        Thread.sleep(1);
        FutureTask<CallableTask.Result> fourth = executor.schedule(now.plusSeconds(1), fourthTask);
        Thread.sleep(1);
        FutureTask<CallableTask.Result> fifth = executor.schedule(now.plusSeconds(1), fifthTask);

        Assert.assertTrue(first.get().getExecutionStartTime().isBefore(second.get().getExecutionStartTime()));
        Assert.assertTrue(second.get().getExecutionStartTime().isBefore(third.get().getExecutionStartTime()));
        Assert.assertTrue(third.get().getExecutionStartTime().isBefore(fourth.get().getExecutionStartTime()));
        Assert.assertTrue(fourth.get().getExecutionStartTime().isBefore(fifth.get().getExecutionStartTime()));
    }

    @Test
    public void whenThresholdPassed_thenNextTasksPrioritized() throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        executor.setThreshold(3);
        FutureTask<CallableTask.Result> first = executor.schedule(now.plusSeconds(2), firstTask);
        FutureTask<CallableTask.Result> second = executor.schedule(now.plusSeconds(3), secondTask);
        Thread.sleep(1);
        FutureTask<CallableTask.Result> third = executor.schedule(now.plusSeconds(3), thirdTask);
        Thread.sleep(1);
        FutureTask<CallableTask.Result> fourth = executor.schedule(now.plusSeconds(3), fourthTask);
        FutureTask<CallableTask.Result> fifth = executor.schedule(now.plusSeconds(4), fifthTask);

        Assert.assertTrue(now.plusSeconds(1).isAfter(first.get().getExecutionStartTime()));
        Assert.assertTrue(now.plusSeconds(1).isAfter(second.get().getExecutionStartTime()));
        Assert.assertTrue(now.plus(2999L, ChronoUnit.MILLIS).isBefore(third.get().getExecutionStartTime())
                            && third.get().getExecutionStartTime().isBefore(now.plus(3005L, ChronoUnit.MILLIS)));
        Assert.assertTrue(third.get().getExecutionStartTime().isBefore((fourth.get().getExecutionStartTime())));
        Assert.assertTrue(fourth.get().getExecutionStartTime().isBefore(fifth.get().getExecutionStartTime()));
    }

}

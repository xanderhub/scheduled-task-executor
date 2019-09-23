# scheduled-task-executor
*Test for Pixonic - task scheduler app*
<br />

## Problem

Having input events of type (__LocalDateTime__, __Callable__) the system should execute *Callable* tasks for each incomming event at
its defined *LocalDateTime* or as soon as possible if the system is overloaded and does not have time to do everything (has a backlog).
Tasks must be performed in order according to the value of LocalDateTime or in the order of event occurence for equal LocalDateTime.
Events can come in random order and from different threads.

## Solution
This project provides solution for the problem described above. It based on classical __producer-consumer__ pattern implementation.
Producer provides events of type (__LocalDateTime__, __Callable__) and consumer processes these events by executing *Callable* tasks.
Both producer and the consumer share a common buffer used as a queue. In our case, the solution aimed on the consumer side and buffer
implementation for incoming events (tasks).

## Implementation
In this Java project the buffer that stores icoming tasks implemented with `DelayQueue` which is also a `BlockingQueue`.
The *BlockingQueue* is highly used in multithreaded environments and producer-consumer solutions in Java. In this project it utilized
in `QueueBasedTaskExecutor` class which is an implementation of main API exposed by `ScheduledTaskExecutor` interface.
The reason for using the *DelayQueue* for this project is that an element from this queue can only be taken when its delay has expired.
Since *delay* in our case represented by the diff between scheduled execution time (LocalDateTime) and current time (in miliseconds) the task will be pulled out and executed just at its scheduled time. There is also no need to sort the tasks - *DelayQueue* does this work for us by sorting elements from lowest to highest delay values in a heap. *DelayQueue* guarantees that the head of the queue will be always the item with lowest delay. In our case it will be the task that should run next.
 

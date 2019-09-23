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

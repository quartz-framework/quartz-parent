package xyz.quartzframework.async;

import lombok.RequiredArgsConstructor;
import xyz.quartzframework.Inject;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.stereotype.ContextBootstrapper;
import xyz.quartzframework.tasks.ScheduledTaskExecutorService;
import xyz.quartzframework.tasks.TaskFactory;

@NoProxy
@RequiredArgsConstructor
@ContextBootstrapper
public class AsyncContextBootstrapper {

    @Inject
    public AsyncContextBootstrapper(TaskFactory taskFactory, ScheduledTaskExecutorService scheduledTaskExecutorService) {
        taskFactory.register("default-async-pool", scheduledTaskExecutorService);
    }
}
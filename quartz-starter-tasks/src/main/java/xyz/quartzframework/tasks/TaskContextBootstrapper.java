package xyz.quartzframework.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.config.Property;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@Slf4j
@NoProxy
@ContextBootstrapper
@RequiredArgsConstructor
public class TaskContextBootstrapper {

    @Provide
    @ActivateWhenBeanMissing(ScheduledTaskExecutorService.class)
    ScheduledTaskExecutorService scheduledTaskExecutorService(@Property("${quartz.default-task-pool.size:5}") int poolSize) {
        return new DefaultScheduledTaskExecutorService(poolSize);
    }

    @Provide
    @ActivateWhenBeanMissing(TaskFactory.class)
    TaskFactory taskFactory(ScheduledTaskExecutorService scheduledTaskExecutorService) {
        val factory = new DefaultTaskFactory();
        factory.register("default", scheduledTaskExecutorService);
        log.info("Initializing default task factory");
        return factory;
    }
}
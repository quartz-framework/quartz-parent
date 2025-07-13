package xyz.quartzframework.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenAnnotationPresent;
import xyz.quartzframework.stereotype.ContextBootstrapper;
import xyz.quartzframework.tasks.TaskFactory;

@Slf4j
@NoProxy
@RequiredArgsConstructor
@ContextBootstrapper
public class AsyncAspectContextBootstrapper {

    @Provide
    @ActivateWhenAnnotationPresent(EnableAsyncMethods.class)
    AsyncAspect asyncAspect(TaskFactory taskFactory) {
        log.info("Enabling @Async feature...");
        return new AsyncAspect(taskFactory);
    }
}
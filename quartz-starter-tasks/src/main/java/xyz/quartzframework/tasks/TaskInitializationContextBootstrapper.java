package xyz.quartzframework.tasks;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.definition.metadata.MethodMetadata;
import xyz.quartzframework.beans.factory.QuartzBeanFactory;
import xyz.quartzframework.beans.support.BeanInjector;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenAnnotationPresent;
import xyz.quartzframework.context.ContextLoads;
import xyz.quartzframework.context.QuartzContext;
import xyz.quartzframework.stereotype.ContextBootstrapper;

import java.time.ZoneId;

@Slf4j
@NoProxy
@RequiredArgsConstructor
@ContextBootstrapper
@ActivateWhenAnnotationPresent(EnableRepeatedTasks.class)
public class TaskInitializationContextBootstrapper {

    private final QuartzBeanFactory quartzBeanFactory;

    private final TaskFactory taskFactory;

    @PreDestroy
    public void onDestroy() {
        taskFactory.shutdownAll();
    }

    @ContextLoads
    public void onContextLoad() {
        val context = quartzBeanFactory.getBean(QuartzContext.class);
        val beanDefinitionRegistry = context.getBeanDefinitionRegistry();
        val beanFactory = context.getBeanFactory();
        beanDefinitionRegistry
                .getBeanDefinitions()
                .stream()
                .flatMap(definition -> definition.getMethods(m -> m.hasAnnotation(RepeatedTask.class)).stream())
                .map(MethodMetadata::getMethod)
                .forEach(taskMethod -> {
                    val annotation = taskMethod.getAnnotation(RepeatedTask.class);
                    if (annotation == null) return;
                    val executorName = annotation.executorName();
                    val initialDelay = annotation.initialDelay();
                    val fixedDelay = annotation.fixedDelay();
                    val timeUnit = annotation.timeUnit();
                    val cron = annotation.cron();
                    val zoneId = annotation.zoneId();
                    Runnable task = () -> BeanInjector.newInstance(beanFactory, taskMethod);
                    if (fixedDelay == -1) {
                        taskFactory.scheduleCron(executorName, task, cron, zoneId.equalsIgnoreCase("default") ? ZoneId.systemDefault() : ZoneId.of(zoneId));
                    } else {
                        taskFactory.scheduleAtFixedRate(executorName, task, initialDelay, fixedDelay, timeUnit);
                    }
                });
        val count = beanDefinitionRegistry
                .getBeanDefinitions()
                .stream()
                .mapToLong(definition -> definition.getMethods(m -> m.hasAnnotation(RepeatedTask.class)).size())
                .sum();
        log.info("Initialized {} repeated tasks", count);
    }
}
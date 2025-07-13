package xyz.quartzframework.bungee.listener;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.md_5.bungee.api.plugin.Listener;
import xyz.quartzframework.Inject;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.factory.QuartzBeanFactory;
import xyz.quartzframework.beans.support.BeanInjector;
import xyz.quartzframework.event.Listen;
import xyz.quartzframework.stereotype.ContextBootstrapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoProxy
@ContextBootstrapper
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class EventContextBootstrapper {

    private final QuartzBeanFactory quartzBeanFactory;

    private final BungeeListenerFactory listenerFactory;

    private final List<Listener> events;

    private final List<Listener> dynamicListeners = new ArrayList<>();

    @PreDestroy
    public void onDestroy() {
        dynamicListeners.forEach(listenerFactory::unregisterEvents);
        dynamicListeners.clear();
    }

    @PostConstruct
    public void postConstruct() {
        events.forEach(listener -> listenerFactory.registerEvents(BeanInjector.unwrapIfProxy(listener)));
        val registry = quartzBeanFactory.getRegistry();
        val registered = new ArrayList<>();
        registry
                .getBeanDefinitions()
                .stream()
                .filter(def -> !def.getMethods(m -> m.hasAnnotation(Listen.class)).isEmpty())
                .forEach(def -> {
                    def.getMethods(m -> m.hasAnnotation(Listen.class)).forEach(listenMethod -> {
                        if (listenMethod.getParameterCount() != 1) {
                            throw new IllegalStateException(String.format(
                                    "@Listen method '%s' in '%s' must have exactly one parameter",
                                    listenMethod.getName(), def.getName()
                            ));
                        }
                        if (!listenMethod.isVoid()) {
                            throw new IllegalStateException(String.format(
                                    "@Listen method '%s' in '%s' must return void",
                                    listenMethod.getName(), def.getName()
                            ));
                        }
                    });
                    val bean = quartzBeanFactory.getBean(def.getName(), def.getTypeMetadata());
                    val realBean = BeanInjector.unwrapIfProxy(bean);
                    listenerFactory.registerEvents(realBean);

                    if (!(bean instanceof Listener)) {
                        dynamicListeners.add(new Listener() {});
                    }
                    registered.add(def);
                });
        log.info("Registered {} listeners", events.size() + registered.size());
    }
}
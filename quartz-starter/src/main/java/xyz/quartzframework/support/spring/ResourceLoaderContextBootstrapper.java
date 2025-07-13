package xyz.quartzframework.support.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.stereotype.ContextBootstrapper;

import java.net.URLClassLoader;

@NoProxy
@ContextBootstrapper
@RequiredArgsConstructor
public class ResourceLoaderContextBootstrapper {

    @Provide
    @ActivateWhenBeanMissing(ResourceLoader.class)
    ResourceLoader resourceLoader(URLClassLoader loader) {
        return new DefaultResourceLoader(loader);
    }
}
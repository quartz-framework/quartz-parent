package xyz.quartzframework.spigot.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenAnnotationPresent;
import xyz.quartzframework.security.EnablePluginSecurity;
import xyz.quartzframework.spigot.session.SpigotSession;
import xyz.quartzframework.spigot.session.SpigotSessionService;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@Slf4j
@NoProxy
@RequiredArgsConstructor
@ContextBootstrapper
public class PluginSecurityAspectContextBootstrapper {

    @Provide
    @ActivateWhenAnnotationPresent(EnablePluginSecurity.class)
    SpigotSecurityAspect securityAspect(SpigotSession senderSession, SpigotSessionService sessionService) {
        log.info("Enabling plugin security...");
        return new SpigotSecurityAspect(senderSession, sessionService);
    }
}
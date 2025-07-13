package xyz.quartzframework.bungee.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenAnnotationPresent;
import xyz.quartzframework.bungee.session.BungeeSession;
import xyz.quartzframework.bungee.session.BungeeSessionService;
import xyz.quartzframework.security.EnablePluginSecurity;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@Slf4j
@NoProxy
@RequiredArgsConstructor
@ContextBootstrapper
public class PluginSecurityAspectContextBootstrapper {

    @Provide
    @ActivateWhenAnnotationPresent(EnablePluginSecurity.class)
    BungeeSecurityAspect securityAspect(BungeeSession senderSession, BungeeSessionService sessionService) {
        return new BungeeSecurityAspect(senderSession, sessionService);
    }
}
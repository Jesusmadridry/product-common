package com.common.persist.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Here is how to implement and inject in the security chain. It must be defined ReactiveAuditorProvider Bean to pass it as a parameter. Besides, observe the filter position is added after {@code AUTHENTICATION} order:
 * <pre>{@code
 * @Configuration
 * public class AnyConfiguration
 *
 *     .....
 *
 *     @Bean
 *     public CapturePrincipalAuthFilter capturePrincipalAuthFilter(ReactiveAuditorProvider reactiveAuditorProvider) {
 *         return new CapturePrincipalAuthFilter(reactiveAuditorProvider);
 *     }
 * }</pre>
 */

@Slf4j
@RequiredArgsConstructor
public class CapturePrincipalAuthFilter implements WebFilter{
    private final ReactiveAuditorProvider reactiveAuditorProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var user = reactiveAuditorProvider.getCurrentAuditor();
        log.trace("Initial user {}", user.orElseGet(() -> "none"));

        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .map(SecurityContext::getAuthentication)
                .map(authentication -> {
                    log.debug ("Registering user-Name: {}", authentication.getName());
                    reactiveAuditorProvider.registerAuthenticated(authentication);
                    return authentication;
                })
                .doOnSuccess(authentication -> log.debug ("Captured username: {}", authentication.getName()))
                .flatMap(x -> chain.filter(exchange));
    }


}

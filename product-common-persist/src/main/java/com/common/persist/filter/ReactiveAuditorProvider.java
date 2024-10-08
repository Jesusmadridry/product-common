package com.common.persist.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;

import java.util.Optional;

/**
*  This get Current Auditor will resolve the annotations @CreatedBy and @LastModifiedBy.
 *  Using the authenticated user that will be caught through the authenticationRef
*
*
* */
@Slf4j
public class ReactiveAuditorProvider implements AuditorAware<String> {
    private final InheritableThreadLocal<Authentication> authenticationRef = new InheritableThreadLocal<>();

    public void registerAuthenticated(Authentication authentication) {
        Optional.of(authentication)
                .filter(Authentication::isAuthenticated)
                .ifPresentOrElse(authenticationRef::set, authenticationRef::remove);
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        log.info("Get Current Auditor {}", authenticationRef.get());
        var userName = Optional.ofNullable(authenticationRef.get())
                .map(Authentication::getName)
                .orElse(null);
        return Optional.ofNullable(userName);
    }
}

package ru.xj2j.plan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNullApi;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.xj2j.plan.model.User;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<User> auditorProvider() {
        return new SpringSecurityAuditAware();
    }

    @Bean
    public AuditingEntityListener entityListener() {
        return new AuditingEntityListener();
    }

    public static class SpringSecurityAuditAware implements AuditorAware<User> {

        @Override
        public Optional<User> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            return Optional.of((User) authentication.getPrincipal());
        }
    }
}

package com.example.demo.configuration;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Marks this class as a configuration class for Spring, meaning it contains bean definitions.
 * "proxyBeanMethods = false" improves performance by disabling proxying of @Bean methods.
 * Configuration class for setting up observation (monitoring) aspects in the application.
 */
@Configuration(proxyBeanMethods = false)
public class ObserverConfig {

    /**
     * Registers an ObservedAspect bean, which enables the @Observed annotation support.
     * The @Observed annotation allows for automatic instrumentation of methods for observation.
     * The ObservationRegistry is required by ObservedAspect to manage observation events.
     *
     * @param observationRegistry
     * @return
     */
    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        // Defines a Spring bean of type ObservedAspect, using the ObservationRegistry as a dependency.
        // This bean enables Micrometer’s @Observed annotation, allowing methods annotated with @Observed
        // to be automatically instrumented for monitoring, capturing timing and other metrics.
        return new ObservedAspect(observationRegistry);
    }
}

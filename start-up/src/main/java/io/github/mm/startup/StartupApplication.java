package io.github.mm.startup;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.event.*;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ExternalClass {}

@Singleton
class LifecycleBean {

    private static final Logger log = LoggerFactory.getLogger(LifecycleBean.class);

    public LifecycleBean() {
        log.info("02 Constructor");
    }

    // @PostConstruct is required for BeanInitializedEventListener to fire.
    // Without it, Micronaut skips the initialization phase entirely.
    @PostConstruct
    void postConstruct() {
        log.info("04 @PostConstruct");
    }
}

@Singleton
class LifecycleBeanInitializedListener implements BeanInitializedEventListener<LifecycleBean> {

    private static final Logger log = LoggerFactory.getLogger(LifecycleBeanInitializedListener.class);

    @Override
    public LifecycleBean onInitialized(BeanInitializingEvent<LifecycleBean> event) {
        log.info("03 BeanInitializedEventListener (fires before @PostConstruct) - depends on @PostConstruct");
        return event.getBean();
    }
}

@Singleton
class LifecycleBeanCreatedListener implements BeanCreatedEventListener<LifecycleBean> {

    private static final Logger log = LoggerFactory.getLogger(LifecycleBeanCreatedListener.class);

    @Override
    public LifecycleBean onCreated(BeanCreatedEvent<LifecycleBean> event) {
        log.info("05 BeanCreatedEventListener (fires after @PostConstruct)");
        return event.getBean();
    }
}

@Singleton
class ContextStartupListener {

    private static final Logger log = LoggerFactory.getLogger(ContextStartupListener.class);

    @EventListener
    void onStartup(StartupEvent event) {
        log.info("07 StartupEvent (SmartInitializingSingleton / ContextRefreshedEvent analog)");
    }

    @EventListener
    void onServerStartup(ServerStartupEvent event) {
        log.info("08 ServerStartupEvent (ApplicationReadyEvent analog)");
    }
}

public class StartupApplication {

    private static final Logger log = LoggerFactory.getLogger(StartupApplication.class);

    public static void main(String[] args) {
        log.info("01 Application Context Starting");
        // eagerInitSingletons(true) forces all @Singleton beans to be constructed at
        // startup. By default, Micronaut singletons are LAZY — they're only created
        // on first injection/lookup. Without this flag, LifecycleBean may never be
        // instantiated, and this entire numbered sequence won't fire as shown.
        Micronaut.build(args).banner(false).eagerInitSingletons(true).start();
    }
}

@Factory
class ExternalClassFactory {

    private static final Logger log = LoggerFactory.getLogger(ExternalClassFactory.class);

    @Singleton
    ExternalClass externalClass() {
        log.info("06 @Bean factory method (for external/unmodifiable class), (order depends on classpath scanning)");
        return new ExternalClass();
    }
}

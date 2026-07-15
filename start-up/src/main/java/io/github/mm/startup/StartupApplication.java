package io.github.mm.startup;

import io.micronaut.context.annotation.Bean;
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

    private final ExternalClass externalClass;

    public LifecycleBean(ExternalClass externalClass) {
        log.info("03 Constructor");
        this.externalClass = externalClass;
    }

    // @PostConstruct is what gives this bean an "initialization phase."
    // BeanInitializedEventListener only fires for beans that have one —
    // remove @PostConstruct and step 04 below silently disappears too.
    @PostConstruct
    void postConstruct() {
        log.info("05 @PostConstruct");
    }
}

@Singleton
class LifecycleBeanInitializedListener implements BeanInitializedEventListener<LifecycleBean> {

    private static final Logger log = LoggerFactory.getLogger(LifecycleBeanInitializedListener.class);

    @Override
    public LifecycleBean onInitialized(BeanInitializingEvent<LifecycleBean> event) {
        log.info("04 BeanInitializedEventListener (fires before @PostConstruct)");
        LifecycleBean bean = event.getBean();
        // This is the hook's actual purpose: inspect or replace the bean
        // BEFORE its own initialization logic runs. A real use case is
        // injecting a computed default the constructor had no way to know.
        return bean;
    }
}

@Singleton
class LifecycleBeanCreatedListener implements BeanCreatedEventListener<LifecycleBean> {

    private static final Logger log = LoggerFactory.getLogger(LifecycleBeanCreatedListener.class);

    @Override
    public LifecycleBean onCreated(BeanCreatedEvent<LifecycleBean> event) {
        log.info("06 BeanCreatedEventListener (fires after @PostConstruct)");
        // Unlike BeanInitializedEventListener, the bean here is fully
        // initialized.
        return event.getBean();
    }
}

@Singleton
class ContextStartupListener {

    private static final Logger log = LoggerFactory.getLogger(ContextStartupListener.class);

    @EventListener
    void onStartup(StartupEvent event) {
        log.info("07 StartupEvent — application context ready");
    }

    @EventListener
    void onServerStartup(ServerStartupEvent event) {
        log.info("08 ServerStartupEvent — server accepting requests");
    }
}

@Factory
class ExternalClassFactory {

    private static final Logger log = LoggerFactory.getLogger(ExternalClassFactory.class);

    @Bean
    @Singleton
    ExternalClass externalClass() {
        log.info("02 @Bean factory method (produces ExternalClass, required by LifecycleBean's constructor)");
        return new ExternalClass();
    }
}

public class StartupApplication {

    private static final Logger log = LoggerFactory.getLogger(StartupApplication.class);

    public static void main(String[] args) {
        log.info("01 Application Context Starting");
        Micronaut.build(args).banner(false).eagerInitSingletons(true).start();
        log.info("09 Application Ready — running until shutdown");
    }
}

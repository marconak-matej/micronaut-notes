package io.github.mm.shutdown;

import io.micronaut.context.LifeCycle;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.event.BeanPreDestroyEvent;
import io.micronaut.context.event.BeanPreDestroyEventListener;
import io.micronaut.context.event.ShutdownEvent;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerShutdownEvent;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class ShutdownEventsBean {

    private static final Logger log = LoggerFactory.getLogger(ShutdownEventsBean.class);

    @EventListener
    void onServerShutdown(ServerShutdownEvent event) {
        log.info(
                "ServerShutdownEvent — {}:{}",
                event.getSource().getHost(),
                event.getSource().getPort());
    }

    @EventListener
    void onAppShutdown(ApplicationShutdownEvent event) {
        log.info("ApplicationShutdownEvent");
    }

    @EventListener
    void onShutdown(ShutdownEvent event) {
        log.info("ShutdownEvent");
    }
}

@Singleton
class PreDestroyBean {

    private static final Logger log = LoggerFactory.getLogger(PreDestroyBean.class);

    @PreDestroy
    void preDestroy() {
        log.info("@PreDestroy");
    }
}

@Singleton
class LifeCycleBean implements LifeCycle<LifeCycleBean> {

    private static final Logger log = LoggerFactory.getLogger(LifeCycleBean.class);
    private final AtomicBoolean running = new AtomicBoolean(true);

    @Override
    public LifeCycleBean start() {
        return this;
    }

    @Override
    public LifeCycleBean stop() {
        running.set(false);
        log.info("LifeCycle.stop()");
        return this;
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }
}

@Singleton
class PreDestroyBeanPreDestroyListener implements BeanPreDestroyEventListener<PreDestroyBean> {

    private static final Logger log = LoggerFactory.getLogger(PreDestroyBeanPreDestroyListener.class);

    @Override
    public PreDestroyBean onPreDestroy(BeanPreDestroyEvent<PreDestroyBean> event) {
        log.info("BeanPreDestroyEventListener — PreDestroyBean about to be destroyed");
        return event.getBean();
    }
}

class ExternalClass {
    private static final Logger log = LoggerFactory.getLogger(ExternalClass.class);

    public void shutdown() {
        log.info("@Bean(preDestroy) — ExternalClass.shutdown()");
    }
}

@Factory
class ExternalClassFactory {

    private static final Logger log = LoggerFactory.getLogger(ExternalClassFactory.class);

    @Bean(preDestroy = "shutdown")
    @Singleton
    ExternalClass externalClass() {
        return new ExternalClass();
    }
}

public class ShutdownApplication {

    private static final Logger log = LoggerFactory.getLogger(ShutdownApplication.class);

    public static void main(String[] args) {
        Runtime.getRuntime()
                .addShutdownHook(new Thread(
                        () -> log.info("Custom JVM shutdown hook (non-deterministic vs Micronaut's hook)"),
                        "custom-shutdown-hook"));

        Micronaut.build(args).banner(false).eagerInitSingletons(true).start();
    }
}

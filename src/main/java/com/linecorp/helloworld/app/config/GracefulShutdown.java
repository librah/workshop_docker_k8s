package com.linecorp.helloworld.app.config;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

@Slf4j
public class GracefulShutdown implements TomcatConnectorCustomizer,
        ApplicationListener<ContextClosedEvent> {

    private static final int TIMEOUT = 32;  // make it 32 seconds (2 seconds longer than pod shutdown wait time)

    private volatile Connector connector;

    @Override
    public void customize(Connector connector) {
        this.connector = connector;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            if (System.getenv("KUBERNETES_SERVICE_HOST") != null) {
                log.info("Wait {} seconds before proceeding to shutdown", TIMEOUT);
                Thread.sleep(TIMEOUT * 1000);
            }
        } catch (Exception ignore) {
            // do nothing
        }
        log.info("Pausing connector");
        this.connector.pause();
        log.info("Checking executor");
        Executor executor = this.connector.getProtocolHandler().getExecutor();
        if (executor instanceof ThreadPoolExecutor) {
            try {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                log.info("Gracefully shutting down thread pool executor, active: {}, task: {}", threadPoolExecutor.getActiveCount(), threadPoolExecutor.getTaskCount());
                threadPoolExecutor.shutdown();
                if (!threadPoolExecutor.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
                    log.info("Forcefully shut down, active: {}, task: {}", threadPoolExecutor.getActiveCount(), threadPoolExecutor.getTaskCount());
                    threadPoolExecutor.shutdownNow();
                } else {
                    log.info("Gracefully shut down done");
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Exiting onApplicationEvent");
    }

}

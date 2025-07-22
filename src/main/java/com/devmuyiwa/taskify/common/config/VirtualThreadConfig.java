package com.devmuyiwa.taskify.common.config;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class VirtualThreadConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatVirtualThreadExecutor(ThreadFactory virtualThreadFactory) {
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.getProtocolHandler().setExecutor(Executors.newThreadPerTaskExecutor(virtualThreadFactory));
        });
    }

    @Bean
    public ThreadFactory virtualThreadFactory() {
        return Thread.ofVirtual().name("virtual-http-", 0).factory();
    }

    @Bean
    public Executor virtualThreadExecutor(ThreadFactory virtualThreadFactory) {
        return Executors.newThreadPerTaskExecutor(virtualThreadFactory);
    }

    @Bean
    public WebMvcRegistrations webMvcRegistrations(Executor virtualThreadExecutor) {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
                RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
                adapter.setTaskExecutor(new ConcurrentTaskExecutor(virtualThreadExecutor));
                return adapter;
            }
        };
    }
}


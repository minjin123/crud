package com.site;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String url = "http://localhost:8080";
        System.out.println("Application started at: " + url);
    }
}
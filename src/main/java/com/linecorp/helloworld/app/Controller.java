package com.linecorp.helloworld.app;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Value("${employeeId}")
    private String employeeId;

    private String hostname;

    @PostConstruct
    private void init() {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "unknown host";
        }
    }

    @GetMapping("/hello")
    public String hello() {
        return String.format("You've hit %s (owned by %s)\n", hostname, employeeId);
    }
}

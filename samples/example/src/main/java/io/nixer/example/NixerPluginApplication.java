package io.nixer.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration;

@SpringBootApplication(exclude = JestAutoConfiguration.class)
public class NixerPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(NixerPluginApplication.class, args);
    }

}

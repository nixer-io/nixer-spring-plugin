package eu.xword.nixer.nixerplugin.example.light;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "eu.xword.nixer.nixerplugin")
public class NixerPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(NixerPluginApplication.class, args);
    }

}

package cn.nispring.rail12306;

import cn.nispring.rail12306.config.DataProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(DataProperties.class)
public class Rail12306Application {

    public static void main(String[] args) {
        SpringApplication.run(Rail12306Application.class, args);
    }

}

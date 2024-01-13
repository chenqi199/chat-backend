package com.taoyiyi.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableZipkinServer
@Slf4j
@SpringBootApplication(scanBasePackages = "com.taoyiyi")
public class ChatApplication  implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("boot run success");
    }
}

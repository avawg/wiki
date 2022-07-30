package org.wg.wiki;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableAsync
@EnableScheduling // 启动定时任务
@EnableWebSocket
@MapperScan("org.wg.wiki.mapper")
public class WikiApplication {
    public static void main(String[] args) {
        SpringApplication.run(WikiApplication.class, args);
    }
}

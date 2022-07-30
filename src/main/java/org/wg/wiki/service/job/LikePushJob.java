package org.wg.wiki.service.job;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wg.wiki.controller.WebSocketServer;

@Service
public class LikePushJob {
    @Autowired
    WebSocketServer socketServer;

    @Async
    // 推送点赞消息
    public void sendMessage(String message, String logId) {
        MDC.put("LOG_ID", logId);
        socketServer.sendMessage(message);
    }
}

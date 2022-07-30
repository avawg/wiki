package org.wg.wiki.service.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wg.wiki.mapper.EbookMapper;
import org.wg.wiki.utils.SnowFlake;

// 每天0点定时更新电子书统计数据
@Component
public class EbookJob {
    private static final Logger logger = LoggerFactory.getLogger(EbookJob.class);

    @Autowired
    private EbookMapper ebookMapper;

    @Autowired
    private SnowFlake snowFlake;

    @Scheduled(cron = "0 0 0 * * *")
    public void run() {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId())); // 增加日志流水号
        logger.info("更新电子书的文档数据开始");
        long start = System.currentTimeMillis();
        ebookMapper.updateStatData();
        long end = System.currentTimeMillis();
        logger.info("更新电子书的文档数据结束, 耗时: {}毫秒", end - start);
    }
}

package org.wg.wiki.service.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wg.wiki.mapper.EbookMapper;
import org.wg.wiki.mapper.EbookSnapshotMapper;
import org.wg.wiki.model.entity.EbookSnapshot;
import org.wg.wiki.utils.SnowFlake;

// 每隔一小时更新电子书快照数据
@Component
public class EbookSnapshotJob {
    private static final Logger logger = LoggerFactory.getLogger(DocJob.class);

    @Autowired
    private EbookSnapshotMapper ebookSnapshotMapper;

    @Autowired
    private SnowFlake snowFlake;

    @Scheduled(cron = "0 0 1 * * *")
    public void run() {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId())); // 增加日志流水号
        logger.info("更新电子书快照数据开始");
        long start = System.currentTimeMillis();
        ebookSnapshotMapper.generateSnapshot();
        long end = System.currentTimeMillis();
        logger.info("更新电子书快照数据结束, 耗时: {}毫秒", end - start);
    }
}

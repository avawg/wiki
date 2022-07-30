package org.wg.wiki.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wg.wiki.model.resp.Result;
import org.wg.wiki.service.EbookSnapshotService;

@RestController
@RequestMapping("/ebook-snapshot")
public class EbookSnapshotController {
    private final Logger logger = LoggerFactory.getLogger(EbookSnapshotController.class);

    @Autowired
    private EbookSnapshotService ebookSnapshotService;

    /**
     * 获取统计数据 昨天，当天 阅读数，点赞数，阅读增长，点赞增长
     */
    @GetMapping("/2day-statistic")
    public Result get2day() {
        Object list = ebookSnapshotService.get2day();
        return Result.success(list);
    }
}
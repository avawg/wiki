package org.wg.wiki.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wg.wiki.model.entity.Ebook;
import org.wg.wiki.model.req.EbookQueryReq;
import org.wg.wiki.model.req.EbookSaveReq;
import org.wg.wiki.model.resp.Result;
import org.wg.wiki.model.resp.Page;
import org.wg.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.wg.wiki.utils.CopyUtil.copy;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    private final Logger logger = LoggerFactory.getLogger(EbookController.class);

    @Autowired
    private EbookService ebookService;

    /**
     * 查询图书，支持分页和模糊查询
     */
    @GetMapping("/list")
    public Result list(@Valid EbookQueryReq req) {
        // 返回前端的值有时不会和数据库一一对应，例如user password
        Page<Ebook> page = ebookService.list(req);
        return Result.success(page);
    }

    /**
     * 保存图书 新增或更新
     */
    @PostMapping("/save")
    // RequestBody解析json post request请求参数
    public Result save(@RequestBody @Valid EbookSaveReq ebookReq) {
        Ebook ebook = copy(ebookReq, Ebook.class);
        ebookService.save(ebook);
        return Result.success();
    }

    /**
     * 删除图书
     */
    @DeleteMapping ("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        logger.info("id: {}", id);
        ebookService.delete(id);
        return Result.success();
    }
}

package org.wg.wiki.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wg.wiki.model.entity.Content;
import org.wg.wiki.model.entity.Doc;
import org.wg.wiki.model.req.DocQueryReq;
import org.wg.wiki.model.req.DocSaveReq;
import org.wg.wiki.model.resp.Page;
import org.wg.wiki.model.resp.Result;
import org.wg.wiki.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Arrays;
import java.util.List;

import static org.wg.wiki.utils.CopyUtil.copy;

@RestController
@RequestMapping("/doc")
public class DocController {

    private final Logger logger = LoggerFactory.getLogger(DocController.class);

    @Autowired
    private DocService docService;

    /**
     * 查询所有文档
     */
    @GetMapping("/all/{ebookId}")
    public Result all(@PathVariable Long ebookId) {
        List<Doc> list = docService.all(ebookId);
        return Result.success(list);
    }

    /**
     * 根据id查询文档内容
     */
    @GetMapping("/content/{id}")
    public Result getContent(@PathVariable Long id) {
        String content = docService.getContent(id);
        return Result.success(content);
    }

    /**
     * 保存文档 新增或更新
     */
    @PostMapping("/save")
    public Result save(@RequestBody @Valid DocSaveReq docReq) {
        Doc doc = copy(docReq, Doc.class);
        Content content = copy(docReq, Content.class);
        docService.save(doc, content);
        return Result.success();
    }

    /**
     * 删除文档
     */
    @PostMapping ("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        logger.info("ids: {}", ids);
        List<String> list = Arrays.asList(ids.split(","));
        docService.delete(list);
        return Result.success();
    }
}

package org.wg.wiki.controller;

import org.wg.wiki.model.entity.Doc;
import org.wg.wiki.model.req.DocQueryReq;
import org.wg.wiki.model.req.DocSaveReq;
import org.wg.wiki.model.resp.Page;
import org.wg.wiki.model.resp.Result;
import org.wg.wiki.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.wg.wiki.utils.CopyUtil.copy;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired
    private DocService docService;

    /**
     * 查询所有文档
     */
    @GetMapping("/all")
    public Result all() {
        List<Doc> list = docService.all();
        return Result.success(list);
    }

    /**
     * 查询文档，支持分页和模糊查询
     */
    @GetMapping("/list")
    public Result list(@Valid DocQueryReq req) {
        Page<Doc> page = docService.list(req);
        return Result.success(page);
    }

    /**
     * 保存文档 新增或更新
     */
    @PostMapping("/save")
    // RequestBody解析json post request请求参数
    public Result save(@RequestBody @Valid DocSaveReq docReq) {
        Doc doc = copy(docReq, Doc.class);
        docService.save(doc);
        return Result.success();
    }

    /**
     * 删除文档
     */
    @DeleteMapping ("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        System.out.println(id);
        docService.delete(id);
        return Result.success();
    }
}

package org.wg.wiki.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wg.wiki.model.entity.Category;
import org.wg.wiki.model.req.CategoryQueryReq;
import org.wg.wiki.model.req.CategorySaveReq;
import org.wg.wiki.model.resp.Page;
import org.wg.wiki.model.resp.Result;
import org.wg.wiki.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.wg.wiki.utils.CopyUtil.copy;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有分类
     */
    @GetMapping("/all")
    public Result all() {
        List<Category> list = categoryService.all();
        return Result.success(list);
    }

    /**
     * 查询分类，支持分页和模糊查询
     */
    @GetMapping("/list")
    public Result list(@Valid CategoryQueryReq req) {
        Page<Category> page = categoryService.list(req);
        return Result.success(page);
    }

    /**
     * 保存分类 新增或更新
     */
    @PostMapping("/save")
    // RequestBody解析json post request请求参数
    public Result save(@RequestBody @Valid CategorySaveReq categoryReq) {
        Category category = copy(categoryReq, Category.class);
        categoryService.save(category);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping ("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        logger.info("id: {}", id);
        categoryService.delete(id);
        return Result.success();
    }
}

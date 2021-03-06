package org.wg.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.wg.wiki.mapper.CategoryMapper;
import org.wg.wiki.model.entity.Category;
import org.wg.wiki.model.entity.CategoryExample;
import org.wg.wiki.model.req.CategoryQueryReq;
import org.wg.wiki.model.resp.Page;
import org.wg.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    SnowFlake snowFlake;

    /**
     * 查询所有数据
     */
    public List<Category> all() {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> list = categoryMapper.selectByExample(categoryExample);
        return list;
    }

    public Page<Category> list(CategoryQueryReq req) {
        CategoryExample categoryExample = new CategoryExample();
        // 分页查询，对第一条sql起作用
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> categorysList = categoryMapper.selectByExample(categoryExample);
        PageInfo<Category> pageInfo = new PageInfo<>(categorysList);

        Page<Category> page = new Page<>();
        page.setTotal(pageInfo.getTotal());
        page.setList(categorysList);
        return page;
    }

    /**
     * 保存分类
     */
    public void save(Category category) {
        if (ObjectUtils.isEmpty(category.getId())) {
            long id = snowFlake.nextId();
            category.setId(id); // 雪花算法生成id
            categoryMapper.insert(category); // 新增
        } else {
            categoryMapper.updateByPrimaryKey(category); // 更新
        }
    }

    /**
     * 删除分类
     */
    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }
}

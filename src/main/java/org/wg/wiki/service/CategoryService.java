package org.wg.wiki.service;

import org.wg.wiki.mapper.CategoryMapper;
import org.wg.wiki.model.entity.Category;
import org.wg.wiki.model.entity.CategoryExample;
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

    /**
     * 查询所有数据
     */
    public List<Category> all() {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> list = categoryMapper.selectByExample(categoryExample);
        return list;
    }

    /**
     * 保存分类
     */
    public void save(Category category) {
        if (ObjectUtils.isEmpty(category.getId())) {
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

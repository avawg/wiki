package org.wg.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.wg.wiki.mapper.EbookMapper;
import org.wg.wiki.model.entity.Ebook;
import org.wg.wiki.model.entity.EbookExample;
import org.wg.wiki.model.req.EbookQueryReq;
import org.wg.wiki.model.resp.Page;

import org.wg.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class EbookService {

    private static final Logger logger = LoggerFactory.getLogger(EbookService.class);

    @Autowired
    private EbookMapper ebookMapper;

    @Autowired
    SnowFlake snowFlake;

    public Page<Ebook> list(EbookQueryReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        // 动态sql，按书名模糊查询 名字不为空时按名字查找
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        // 分页查询，对第一条sql起作用
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ebook> ebooksList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebooksList);
        logger.info("总行数: {}", pageInfo.getTotal());
        logger.info("总页数: {}", pageInfo.getPages());

        Page<Ebook> page = new Page<>();
        page.setTotal(pageInfo.getTotal());
        page.setList(ebooksList);
        return page;
    }

    /**
     * 保存图书
     */
    public void save(Ebook ebook) {
        if (ObjectUtils.isEmpty(ebook.getId())) {
            long id = snowFlake.nextId();
            ebook.setId(id); // 雪花算法生成id
            ebook.setDocCount(0); // default 0 仍需手动设置
            ebook.setViewCount(0);
            ebook.setVoteCount(0);
            ebookMapper.insert(ebook); // 新增
        } else {
            ebookMapper.updateByPrimaryKey(ebook); // 更新
        }
    }

    /**
     * 删除图书
     */
    public void  delete(Long id) {
        ebookMapper.deleteByPrimaryKey(id);
    }
}

package org.wg.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.wg.wiki.mapper.DocMapper;
import org.wg.wiki.model.entity.Doc;
import org.wg.wiki.model.entity.DocExample;
import org.wg.wiki.model.entity.Doc;
import org.wg.wiki.model.entity.DocExample;
import org.wg.wiki.model.req.DocQueryReq;
import org.wg.wiki.model.resp.Page;
import org.wg.wiki.utils.SnowFlake;

import java.util.List;

@Service
public class DocService {

    private static final Logger logger = LoggerFactory.getLogger(DocService.class);

    @Autowired
    private DocMapper docMapper;

    @Autowired
    SnowFlake snowFlake;

    /**
     * 查询所有数据
     */
    public List<Doc> all() {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        List<Doc> list = docMapper.selectByExample(docExample);
        return list;
    }

    public Page<Doc> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docsList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docsList);
        Page<Doc> page = new Page<>();
        page.setTotal(pageInfo.getTotal());
        page.setList(docsList);
        return page;
    }

    /**
     * 保存文档
     */
    public void save(Doc doc) {
        if (ObjectUtils.isEmpty(doc.getId())) {
            long id = snowFlake.nextId();
            doc.setId(id); // 雪花算法生成id
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc); // 新增
        } else {
            docMapper.updateByPrimaryKey(doc); // 更新
        }
    }

    /**
     * 删除文档
     */
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }
}
package org.wg.wiki.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.wg.wiki.exception.BusinessException;
import org.wg.wiki.exception.BusinessExceptionCode;
import org.wg.wiki.mapper.ContentMapper;
import org.wg.wiki.mapper.DocMapper;
import org.wg.wiki.model.entity.Content;
import org.wg.wiki.model.entity.Doc;
import org.wg.wiki.model.entity.DocExample;
import org.wg.wiki.utils.RequestContext;

import java.util.List;

@Service
public class DocService {

    private static final Logger logger = LoggerFactory.getLogger(DocService.class);

    @Autowired
    private DocMapper docMapper;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询所有数据
     */
    public List<Doc> all(Long ebookId) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> list = docMapper.selectByExample(docExample);
        return list;
    }

    /**
     * 保存文档
     */
    public void save(Doc doc, Content content) {
        if (ObjectUtils.isEmpty(doc.getId())) {
            // 同时插入的删除保证自增主键一致
            docMapper.insertSelective(doc); // 新增
            contentMapper.insertSelective(content);
        } else {
            docMapper.updateByPrimaryKey(doc); // 更新
            int update = contentMapper.updateByPrimaryKeyWithBLOBs(content); // 包含大字段更新
            if (update == 0) {
                contentMapper.insertSelective(content);
            }
        }
    }

    /**
     * 删除文档
     */
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
        contentMapper.deleteByPrimaryKey(id);
    }

    /**
     * 删除文档
     */
    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    /**
     * 根据id查询文档内容
     */
    public String getContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else {
            docMapper.updateViewCount(id);
            return content.getContent();
        }
    }

    /**
     * 点赞
     */
    public void vote(Long id) {
        String remoteAddress = RequestContext.getRemoteAddress();
        if (redisTemplate.hasKey(remoteAddress)) {
            throw new BusinessException(BusinessExceptionCode.REPEAT_VOTE);
        }
        // 同一台主机24小时内不能在点赞
        redisTemplate.opsForValue().set(remoteAddress, 3600 * 24);
        docMapper.updateVoteCount(id);
    }
}

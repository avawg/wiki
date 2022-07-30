package org.wg.wiki.service;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.wg.wiki.exception.BusinessException;
import org.wg.wiki.exception.BusinessExceptionCode;
import org.wg.wiki.mapper.ContentMapper;
import org.wg.wiki.mapper.DocMapper;
import org.wg.wiki.model.entity.*;
import org.wg.wiki.service.job.LikePushJob;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    LikePushJob likePushJob;

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
    @Transactional
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
    @Transactional
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
        contentMapper.deleteByPrimaryKey(id);
    }

    /**
     * 删除文档
     */
    @Transactional
    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);

        ContentExample contentExample = new ContentExample();
        ContentExample.Criteria criteria1 = contentExample.createCriteria();
        criteria1.andIdIn(ids);
        contentMapper.deleteByExample(contentExample);
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
    public void vote(Long docId) {
        // 获取登录用户id
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader("token");
        User user = JSONObject.parseObject((String)redisTemplate.opsForValue().get(token), User.class);

        String userId = user.getId().toString();
        if (redisTemplate.opsForSet().isMember(userId, docId)) {
            throw new BusinessException(BusinessExceptionCode.REPEAT_VOTE);
        }
        // 同一用户24小时内不能在点赞
        redisTemplate.opsForSet().add(userId, docId, 3600 * 24);
        docMapper.updateVoteCount(docId);

        Doc doc = docMapper.selectByPrimaryKey(docId);
        String logId = MDC.get("LOG_ID");
        likePushJob.sendMessage("【" + doc.getName() + "】被点赞!", logId);
    }
}

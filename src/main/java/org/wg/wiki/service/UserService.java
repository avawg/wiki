package org.wg.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.wg.wiki.exception.BusinessException;
import org.wg.wiki.exception.BusinessExceptionCode;
import org.wg.wiki.mapper.UserMapper;
import org.wg.wiki.model.entity.User;
import org.wg.wiki.model.entity.UserExample;
import org.wg.wiki.model.req.UserQueryReq;
import org.wg.wiki.model.resp.Page;
import org.wg.wiki.utils.SnowFlake;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SnowFlake snowFlake;

    public Page<User> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> usersList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(usersList);
        Page<User> page = new Page<>();
        page.setTotal(pageInfo.getTotal());
        page.setList(usersList);
        return page;
    }

    /**
     * 保存用户
     */
    public void save(User user) {
        // 密码md5加密
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(md5Password);

        if (ObjectUtils.isEmpty(user.getId())) {

            User userDB = getByLoginName(user.getLoginName());
            if (ObjectUtils.isEmpty(userDB)) {
                user.setId(snowFlake.nextId()); // 雪花算法生成id
                userMapper.insert(user); // 新增
            } else {
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXISTS);
            }
        } else {
            user.setLoginName(null); // 不更新登录名
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    public User getByLoginName(String loginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        List<User> users = userMapper.selectByExample(userExample);
        if (!CollectionUtils.isEmpty(users)) {
            return users.get(0);
        }
        return null;
    }

    /**
     * 删除用户
     */
    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }
}

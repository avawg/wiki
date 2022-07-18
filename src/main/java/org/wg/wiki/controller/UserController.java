package org.wg.wiki.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wg.wiki.model.entity.User;
import org.wg.wiki.model.req.UserLoginReq;
import org.wg.wiki.model.req.UserQueryReq;
import org.wg.wiki.model.req.UserResetPasswordReq;
import org.wg.wiki.model.req.UserSaveReq;
import org.wg.wiki.model.resp.Page;
import org.wg.wiki.model.resp.Result;
import org.wg.wiki.model.resp.UserLoginResp;
import org.wg.wiki.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static org.wg.wiki.utils.CopyUtil.copy;

@RestController
@RequestMapping("/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 查询用户，支持分页和模糊查询
     */
    @GetMapping("/list")
    public Result list(@Valid UserQueryReq req) {
        Page<User> page = userService.list(req);
        return Result.success(page);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody @Valid UserLoginReq userLoginReq) {
        User user = copy(userLoginReq, User.class);
        UserLoginResp userLoginResp = userService.login(user);
        return Result.success(userLoginResp);
    }

    /**
     * 保存用户 新增或更新
     */
    @PostMapping("/save")
    public Result save(@RequestBody @Valid UserSaveReq userReq) {
        User user = copy(userReq, User.class);
        userService.save(user);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody @Valid UserResetPasswordReq req) {
        User user = copy(req, User.class);
        userService.resetPassword(user);
        return Result.success();
    }
}

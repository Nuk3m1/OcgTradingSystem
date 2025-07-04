package com.nuk3m1.ocgtradingsystem.service.impl;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuk3m1.ocgtradingsystem.common.ThrowUtils;
import com.nuk3m1.ocgtradingsystem.model.entity.UserFavorite;
import com.nuk3m1.ocgtradingsystem.model.entity.Users;
import com.nuk3m1.ocgtradingsystem.enums.ErrorCode;
import com.nuk3m1.ocgtradingsystem.exception.BusinessException;
import com.nuk3m1.ocgtradingsystem.model.request.UserRegisterRequest;
import com.nuk3m1.ocgtradingsystem.service.UsersService;
import com.nuk3m1.ocgtradingsystem.mapper.UsersMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author legion
* @description 针对表【users】的数据库操作Service实现
* @createDate 2025-06-27 22:03:48
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService {

    @Override
    public Users getUser() {
        Object loginUserId = StpUtil.getLoginIdDefaultNull();
        if (loginUserId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询  **可拓展缓存路线**
        Users currentUser = this.getById((String) loginUserId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;

    }

    @Override
    public Long UserRegister(UserRegisterRequest userRegisterRequest) {
        Users user = new Users();
        if(userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"UserRegisterRequest is null");
        }
        List<Users> users = baseMapper.selectList(Wrappers.<Users>lambdaQuery()
                .eq(Users::getAccount,userRegisterRequest.getAccount()));
        List<String> accounts = users.stream()
                .map(Users::getAccount)
                .collect(Collectors.toList());
        if(accounts.contains(userRegisterRequest.getAccount())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"该账户已存在");

        }


        BeanUtils.copyProperties(userRegisterRequest,user);
        validUser(user,true);
        // 插入
        boolean userId = baseMapper.insert(user) != 0;
        ThrowUtils.ThrowIf(!userId , ErrorCode.OPERATION_ERROR);
        return null;
    }

    @Override
    public Users userLogin(String userAccount, String userPassword) {
        if(StpUtil.isLogin()){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不允许多次登录");
        }
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG, "参数为空");
        }
        // 查询用户是否存在
        Users user = this.baseMapper.selectOne(Wrappers.<Users>lambdaQuery()
                .eq(Users::getAccount, userAccount)
                .eq(Users::getPassword, userPassword));
        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG, "用户不存在或密码错误");
        }
        if(!StpUtil.isDisable(user.getId())){

            StpUtil.login(user.getId());
        }else{throw new BusinessException(ErrorCode.OPERATION_ERROR,"登陆失败");}



        StpUtil.getSession().set("user_login", user);



        return user;
    }

    @Override
    public boolean userLogout() {
        if(StpUtil.isLogin()){
            StpUtil.logout();
        }else{
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"当前不存在登录用户");
        }

        return true;
    }


    public void validUser(Users user,boolean condition){
        if(user == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"Card can not be null");
        }
        String userAccount = user.getAccount();
        String userPassword = user.getPassword();

        if (condition) {
            ThrowUtils.ThrowIf(StringUtils.isAnyBlank(userAccount,userPassword), ErrorCode.PARAM_IS_WRONG,
                    "userAccount and userPassword cannot be blank");

        }
    }
}





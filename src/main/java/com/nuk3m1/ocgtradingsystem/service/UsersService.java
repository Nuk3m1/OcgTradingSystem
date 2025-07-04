package com.nuk3m1.ocgtradingsystem.service;

import com.nuk3m1.ocgtradingsystem.model.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nuk3m1.ocgtradingsystem.model.request.UserRegisterRequest;

/**
* @author legion
* @description 针对表【users】的数据库操作Service
* @createDate 2025-06-27 22:03:48
*/
public interface UsersService extends IService<Users> {
    Users getUser();

    Long UserRegister(UserRegisterRequest userRegisterRequest);

    Users userLogin(String userAccount, String userPassword);

    boolean userLogout();
}

package com.nuk3m1.ocgtradingsystem.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.nuk3m1.ocgtradingsystem.common.BaseResponse;
import com.nuk3m1.ocgtradingsystem.common.ResultUtils;
import com.nuk3m1.ocgtradingsystem.enums.ErrorCode;
import com.nuk3m1.ocgtradingsystem.exception.BusinessException;
import com.nuk3m1.ocgtradingsystem.model.entity.Users;
import com.nuk3m1.ocgtradingsystem.model.request.UserLoginRequest;
import com.nuk3m1.ocgtradingsystem.model.request.UserRegisterRequest;
import com.nuk3m1.ocgtradingsystem.service.CardsService;
import com.nuk3m1.ocgtradingsystem.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UsersService usersService;
    @Resource
    CardsService cardsService;


    @PostMapping("/register")
    public BaseResponse<Long> UserRegister(UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG);
        }
        if (StringUtils.isAnyBlank(userRegisterRequest.getAccount(),userRegisterRequest.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG, "Params can not be empty!");
        }
        Long condition = usersService.UserRegister(userRegisterRequest);
        return ResultUtils.success(condition);
    }

    @PostMapping("/login")
    public BaseResponse<Users> userLogin(UserLoginRequest userLoginRequest){
        if (userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG);
        }
        String UserAccount = userLoginRequest.getAccount();
        String UserPassword = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(UserAccount, UserPassword)) {
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG);
        }
        Users loginUser = usersService.userLogin(UserAccount, UserPassword);
        return ResultUtils.success(loginUser);
    }
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout() {
        boolean result = usersService.userLogout();
        return ResultUtils.success(result);
    }

}

package com.nuk3m1.ocgtradingsystem.controller;


import com.nuk3m1.ocgtradingsystem.common.BaseResponse;
import com.nuk3m1.ocgtradingsystem.common.ResultUtils;
import com.nuk3m1.ocgtradingsystem.model.entity.Cards;
import com.nuk3m1.ocgtradingsystem.model.entity.UserFavorite;
import com.nuk3m1.ocgtradingsystem.model.entity.Users;
import com.nuk3m1.ocgtradingsystem.service.UserFavoriteService;
import com.nuk3m1.ocgtradingsystem.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/userFavorite")
public class UserFavoriteController {
    @Resource
    private UsersService usersService;
    @Resource
    private UserFavoriteService userFavoriteService;

    @GetMapping("/get")
    public BaseResponse<List<Cards>> getUserFavorite(){

        Users currentUser = usersService.getUser();
        List<Cards> result = userFavoriteService.getUserFavorite(currentUser.getId());

        return ResultUtils.success(result);
    }


}

package com.nuk3m1.ocgtradingsystem.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserRegisterRequest {


    /**
     *
     */
    private String account;

    /**
     *
     */
    private String password;




}

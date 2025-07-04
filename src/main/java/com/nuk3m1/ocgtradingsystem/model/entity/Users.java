package com.nuk3m1.ocgtradingsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName users
 */
@TableName(value ="users")
@Data
public class Users {
    /**
     * 
     */
    @TableId(type= IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String account;

    /**
     * 
     */
    private String password;

    /**
     *
     */
    @TableField("type")
    private String type = "USER";

}
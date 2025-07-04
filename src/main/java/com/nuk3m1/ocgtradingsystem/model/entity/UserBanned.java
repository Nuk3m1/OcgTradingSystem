package com.nuk3m1.ocgtradingsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName user_banned
 */
@TableName(value ="user_banned")
@Data
public class UserBanned {
    /**
     *
     */
    @TableId(type= IdType.AUTO)
    private Long id;
    /**
     * 
     */
    private Long banUser;

    /**
     * 
     */
    private Integer status;
}
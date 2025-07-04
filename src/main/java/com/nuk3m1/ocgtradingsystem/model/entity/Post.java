package com.nuk3m1.ocgtradingsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName post
 */
@TableName(value ="post")
@Data
public class Post {
    /**
     * 
     */
    @TableId(type=IdType.AUTO)
    private Long id;

    /**
     * 收信方
     */
    private Long sellerId;

    /**
     * 写信方
     */
    private Long buyerId;

    /**
     * 
     */
    private String message;

    /**
     * 
     */
    private Date creatTime;
}
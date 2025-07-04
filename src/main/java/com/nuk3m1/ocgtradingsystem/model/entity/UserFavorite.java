package com.nuk3m1.ocgtradingsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName user_favorite
 */
@TableName(value ="user_favorite")
@Data
public class UserFavorite {
    /**
     *
     */
    @TableId(type= IdType.AUTO)
    private Long id;
    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long cardId;
}
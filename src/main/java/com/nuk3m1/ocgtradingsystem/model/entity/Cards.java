package com.nuk3m1.ocgtradingsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 
 * @TableName cards
 */
@TableName(value ="cards")
@Data
public class Cards {
    /**
     * 
     */
    @TableId(type= IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long sellerId;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String cardNumber;

    /**
     * 
     */
    private String cardGraph;

    /**
     * 
     */
    private String rarity;

    /**
     * 
     */
    private BigDecimal price;

    /**
     * 
     */
    private Long attention;

    /**
     * 
     */
    private Integer status;
}
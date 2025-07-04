package com.nuk3m1.ocgtradingsystem.model.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 不包含 卡片id 卖家id的实体类
 * 前者通过自增赋值
 * 后者通过读取当前登录账户id赋值
 */
@Data
public class CardsAddRequest {

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


}

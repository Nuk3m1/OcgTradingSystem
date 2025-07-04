package com.nuk3m1.ocgtradingsystem.model.request;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class PostSendRequest {
    /**
     *
     */
    private Long sellerId;



    /**
     *
     */
    private String message;


}

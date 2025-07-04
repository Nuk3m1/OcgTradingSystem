package com.nuk3m1.ocgtradingsystem.service;

import com.nuk3m1.ocgtradingsystem.model.entity.UserBanned;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nuk3m1.ocgtradingsystem.model.request.BannedFailRequest;
import com.nuk3m1.ocgtradingsystem.model.request.BannedPassRequest;
import com.nuk3m1.ocgtradingsystem.model.request.BannedSendRequest;

import java.util.List;

/**
* @author legion
* @description 针对表【user_banned】的数据库操作Service
* @createDate 2025-06-27 22:03:43
*/
public interface UserBannedService extends IService<UserBanned> {

    List<UserBanned> getUserBanned();

    Boolean passRequest(BannedPassRequest passBannedRequest);

    Boolean failRequest(BannedFailRequest failBannedRequest);

    Long sendRequest(BannedSendRequest bannedSendRequest);
}

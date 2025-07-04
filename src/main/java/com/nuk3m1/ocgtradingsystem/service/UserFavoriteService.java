package com.nuk3m1.ocgtradingsystem.service;

import com.nuk3m1.ocgtradingsystem.model.entity.Cards;
import com.nuk3m1.ocgtradingsystem.model.entity.UserFavorite;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nuk3m1.ocgtradingsystem.model.request.CardsCollectCancelRequest;

import java.util.List;

/**
* @author legion
* @description 针对表【user_favorite】的数据库操作Service
* @createDate 2025-06-27 22:03:46
*/
public interface UserFavoriteService extends IService<UserFavorite> {

    List<Cards> getUserFavorite(Long id);

    Long addCollectCards(Long cardId,Long userId);

    Boolean collectCancelCards(CardsCollectCancelRequest cardsCollectCancelRequest, Long id);
}

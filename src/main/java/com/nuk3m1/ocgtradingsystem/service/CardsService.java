package com.nuk3m1.ocgtradingsystem.service;

import com.nuk3m1.ocgtradingsystem.model.entity.Cards;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nuk3m1.ocgtradingsystem.model.entity.Users;
import com.nuk3m1.ocgtradingsystem.model.request.CardsAddRequest;
import com.nuk3m1.ocgtradingsystem.model.request.CardsBuyRequest;
import com.nuk3m1.ocgtradingsystem.model.request.CardsCollectRequest;
import com.nuk3m1.ocgtradingsystem.model.request.CardsDeleteRequest;

import java.util.List;

/**
* @author legion
* @description 针对表【cards】的数据库操作Service
* @createDate 2025-06-27 22:03:35
*/
public interface CardsService extends IService<Cards> {

    Long addCard(CardsAddRequest cardsAddRequest, Long id);

    Boolean deleteCard(CardsDeleteRequest id, Users currentUser);

    List<Cards> getAllCards();

    List<Cards> getTopCards();

    List<Cards> getOwnCards(Long id);

    Boolean buyCards(CardsBuyRequest cardsBuyRequest);

    Boolean collectCards(CardsCollectRequest cardsCollectRequest);
}

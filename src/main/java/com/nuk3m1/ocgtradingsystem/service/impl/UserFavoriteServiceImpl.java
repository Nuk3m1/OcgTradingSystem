package com.nuk3m1.ocgtradingsystem.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuk3m1.ocgtradingsystem.common.ThrowUtils;
import com.nuk3m1.ocgtradingsystem.enums.ErrorCode;
import com.nuk3m1.ocgtradingsystem.exception.BusinessException;
import com.nuk3m1.ocgtradingsystem.mapper.CardsMapper;
import com.nuk3m1.ocgtradingsystem.model.entity.Cards;
import com.nuk3m1.ocgtradingsystem.model.entity.UserFavorite;
import com.nuk3m1.ocgtradingsystem.model.request.CardsCollectCancelRequest;
import com.nuk3m1.ocgtradingsystem.service.CardsService;
import com.nuk3m1.ocgtradingsystem.service.UserFavoriteService;
import com.nuk3m1.ocgtradingsystem.mapper.UserFavoriteMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author legion
* @description 针对表【user_favorite】的数据库操作Service实现
* @createDate 2025-06-27 22:03:46
*/
@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite>
    implements UserFavoriteService {
    @Resource
    private CardsMapper cardsMapper;
    @Override
    public List<Cards> getUserFavorite(Long id) {
        if(id == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG);
        }
        List<UserFavorite> favorites = baseMapper.selectList(Wrappers.<UserFavorite>lambdaQuery()
                .eq(UserFavorite::getUserId,id));
        /**List<UserFavorite> favorites = baseMapper.selectList(lambdaQuery()
                .eq(UserFavorite::getUserId,id));
         */
        if (favorites.isEmpty()) {
            return new ArrayList<>(); // 如果没有记录，返回空列表
        }

        List<Long> cardIds = favorites.stream()
                .map(UserFavorite::getCardId)
                .collect(Collectors.toList());
        List<Cards> cards = cardsMapper.selectBatchIds(cardIds);
        List<Cards> filteredCards = cards.stream()
                .filter(card -> card.getStatus() == 1)
                .collect(Collectors.toList());
        return filteredCards;
    }

    @Override
    public Long addCollectCards(Long cardId,Long userId) {
        UserFavorite userFavorite = new UserFavorite();
        if( cardId == null || userId == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"未正确传递收藏目标的id");
        }
        //校验目标是否已收藏:
        List<UserFavorite> userFavorites = baseMapper.selectList(Wrappers.<UserFavorite>lambdaQuery()
                .eq(UserFavorite::getUserId,userId));
        List<Long> cardIds = userFavorites.stream()
                        .map(UserFavorite::getCardId)
                        .collect(Collectors.toList());
        if(cardIds.contains(cardId)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"你已经收藏过该卡牌");
        }
        userFavorite.setCardId(cardId);
        userFavorite.setUserId(userId);
        //插入
        boolean favorite = baseMapper.insert(userFavorite) != 0;
        ThrowUtils.ThrowIf(!favorite , ErrorCode.OPERATION_ERROR);
        return userFavorite.getId();
    }

    @Override
    public Boolean collectCancelCards(CardsCollectCancelRequest cardsCollectCancelRequest, Long id) {
        if(cardsCollectCancelRequest == null || id == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"未正确传递取消收藏的目标信息");
        }
        UserFavorite userFavorite = baseMapper.selectOne(Wrappers.<UserFavorite>lambdaQuery()
                .eq(UserFavorite::getCardId,cardsCollectCancelRequest.getCardId())
                .eq(UserFavorite::getUserId,id));
        if(userFavorite == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不存在待取消的收藏对象");
        }
        boolean result = baseMapper.deleteById(userFavorite.getId()) != 0;
        ThrowUtils.ThrowIf(!result,ErrorCode.OPERATION_ERROR);

        return true;
    }
}





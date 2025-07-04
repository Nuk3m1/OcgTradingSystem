package com.nuk3m1.ocgtradingsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuk3m1.ocgtradingsystem.model.entity.Cards;
import com.nuk3m1.ocgtradingsystem.model.entity.Users;
import com.nuk3m1.ocgtradingsystem.model.request.CardsAddRequest;
import com.nuk3m1.ocgtradingsystem.enums.ErrorCode;
import com.nuk3m1.ocgtradingsystem.exception.BusinessException;
import com.nuk3m1.ocgtradingsystem.model.request.CardsBuyRequest;
import com.nuk3m1.ocgtradingsystem.model.request.CardsCollectRequest;
import com.nuk3m1.ocgtradingsystem.model.request.CardsDeleteRequest;
import com.nuk3m1.ocgtradingsystem.service.CardsService;
import com.nuk3m1.ocgtradingsystem.mapper.CardsMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.nuk3m1.ocgtradingsystem.common.ThrowUtils;
/**
* @author legion
* @description 针对表【cards】的数据库操作Service实现
* @createDate 2025-06-27 22:03:35
*/
@Service
public class CardsServiceImpl extends ServiceImpl<CardsMapper, Cards>
    implements CardsService {


    @Override
    public Long addCard(CardsAddRequest cardsAddRequest, Long id) {
        Cards card = new Cards();
        if(cardsAddRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"cardsaddrequest is null");
        }
        card.setStatus(1);
        card.setAttention(0L);
        card.setSellerId(id);

        BeanUtils.copyProperties(cardsAddRequest,card);
        validCard(card,true);
        //插入
        boolean cardId = baseMapper.insert(card) != 0;
        ThrowUtils.ThrowIf(!cardId , ErrorCode.OPERATION_ERROR);
        return card.getId();
    }

    @Override
    public Boolean deleteCard(CardsDeleteRequest cardsDeleteRequest, Users currentUser) {
        if(cardsDeleteRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"target is null");
        }
        Cards card = baseMapper.selectOne(Wrappers.<Cards>lambdaQuery()
                .eq(Cards::getId,cardsDeleteRequest.getId()));
        //权限校验功能：满足以下条件中任意一个
        //1.当前用户为管理员
        //2.当前用户id与卡牌的sellerId相同
        if(!Objects.equals(currentUser.getId(), card.getSellerId())){
            if(!Objects.equals(currentUser.getType(), "ADMIN")){
                throw new BusinessException(ErrorCode.PERMISSION_ERROR,"您无权限执行此操作");
            }
        }

        card.setStatus(0);

        validCard(card,true);

        boolean result = baseMapper.updateById(card) != 0;
        ThrowUtils.ThrowIf(!result,ErrorCode.OPERATION_ERROR,"下架失败");
        return true;
    }

    @Override
    public List<Cards> getAllCards() {
        // 得到所有状态码为 1-上架  的卡牌
        /**
         * QueryWrapper<Cards> queryWrapper = new QueryWrapper<>();
         * queryWrapper.eq("status",1);
         * List<Cards> cards = baseMapper.selectList(queryWrapper);
         */
        List<Cards> cards = baseMapper.selectList(Wrappers.<Cards>lambdaQuery()
                .eq(Cards::getStatus,1));

        return cards;
    }

    @Override
    public List<Cards> getTopCards() {
        // 得到所有状态码为 1-上架 的卡牌
        // 将这些卡牌按照关注量降序排名 取10个
        QueryWrapper<Cards> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",1)
                .orderByDesc("attention")
                .last("LIMIT 10");
        List<Cards> TopCards = baseMapper.selectList(Wrappers.<Cards>lambdaQuery()
                .eq(Cards::getStatus,1)
                .orderByDesc(Cards::getAttention)
                .last("LIMIT 10"));
        return TopCards;
    }

    @Override
    public List<Cards> getOwnCards(Long id) {
        //得到所有sellerId和传入的id相匹配的卡牌
        List<Cards> cards = baseMapper.selectList(Wrappers.<Cards>lambdaQuery()
                .eq(Cards::getSellerId,id)
                .eq(Cards::getStatus,1));
        return cards;
    }

    @Override
    public Boolean buyCards(CardsBuyRequest cardsBuyRequest) {
        if(cardsBuyRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG);
        }
        Cards card = baseMapper.selectOne(Wrappers.<Cards>lambdaQuery()
                .eq(Cards::getId,cardsBuyRequest.getId()));
        card.setStatus(2);

        validCard(card,true);

        boolean result = baseMapper.updateById(card) != 0;
        ThrowUtils.ThrowIf(!result,ErrorCode.OPERATION_ERROR,"购买失败");
        return true;
    }

    @Override
    public Boolean collectCards(CardsCollectRequest cardsCollectRequest) {
        if(cardsCollectRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG);
        }
        Cards card = baseMapper.selectById(cardsCollectRequest.getId());
        Long attention = card.getAttention() + 1;
        card.setAttention(attention);
        validCard(card,true);
        baseMapper.updateById(card);
        /**
        Cards card = new Cards();
        Long attention = cardsCollectRequest.getAttention() + 1;
        BeanUtils.copyProperties(cardsCollectRequest,card);
        card.setAttention(attention);
        validCard(card,true);
        baseMapper.updateById(card);*/
        return true;
    }

    private void validCard(Cards card , boolean condition){
        if(card == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"Card can not be null");
        }
        String name = card.getName();
        String cardNumber = card.getCardNumber();
        String cardGraph = card.getCardGraph();
        String rarity = card.getRarity();
        BigDecimal price = card.getPrice();
        Integer status = card.getStatus();

        if (condition) {
            ThrowUtils.ThrowIf(StringUtils.isAnyBlank(name, cardNumber, cardGraph), ErrorCode.PARAM_IS_WRONG, "Name, cardNumber, and cardGraph cannot be blank");
            if (rarity == null ) {
                throw new BusinessException(ErrorCode.PARAM_IS_WRONG, "rarity of card cannot be null");
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(ErrorCode.PARAM_IS_WRONG, "price cannot be null");
            }
            if(status != 0 && status != 1 && status != 2 && status!= 3 ){
                throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"card status is out of range");
            }
        }

    }
}





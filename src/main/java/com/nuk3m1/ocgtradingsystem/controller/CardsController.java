package com.nuk3m1.ocgtradingsystem.controller;

import com.nuk3m1.ocgtradingsystem.common.BaseResponse;
import com.nuk3m1.ocgtradingsystem.common.ResultUtils;
import com.nuk3m1.ocgtradingsystem.model.entity.Cards;
import com.nuk3m1.ocgtradingsystem.model.request.*;
import com.nuk3m1.ocgtradingsystem.model.entity.Users;
import com.nuk3m1.ocgtradingsystem.enums.ErrorCode;
import com.nuk3m1.ocgtradingsystem.exception.BusinessException;
import com.nuk3m1.ocgtradingsystem.service.CardsService;
import com.nuk3m1.ocgtradingsystem.service.UserFavoriteService;
import com.nuk3m1.ocgtradingsystem.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.xml.transform.Result;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/cards")
public class CardsController {
    @Resource
    private CardsService cardsService;
    @Resource
    private UsersService usersService;
    @Resource
    private UserFavoriteService userFavoriteService;


    /**
     * 添加卡牌
     */
    @PostMapping("/add")
    public BaseResponse<Long> addCards(@RequestBody CardsAddRequest cardsAddRequest){
        if(cardsAddRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"add request is null");
        }
        Users currentUser = usersService.getUser();
        Long cardId = cardsService.addCard(cardsAddRequest , currentUser.getId());
        return ResultUtils.success(cardId);
    }

    /**
     * 管理员:下架卡牌
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteCards(@RequestBody CardsDeleteRequest deleteRequest){
        if(deleteRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"target card is null");
        }
        Users currentUser = usersService.getUser();

        Boolean result = cardsService.deleteCard(deleteRequest,currentUser);
        return ResultUtils.success(result);
    }

    /**
     * 获得所有卡牌
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<List<Cards>> getAllCards(){
        List<Cards> cards = cardsService.getAllCards();
        return ResultUtils.success(cards);
    }

    /**
     * 获得热门卡牌
     * @return
     */
    @GetMapping("/get/top")
    public BaseResponse<List<Cards>> getTopCards(){
        List<Cards> cards = cardsService.getTopCards();
        return ResultUtils.success(cards);
    }
    /**
     * 获得 自己上架的卡牌
     * @return
     */
    @GetMapping("/get/own")
    public BaseResponse<List<Cards>> getOwnCards(){
        Users currentUser = usersService.getUser();
        List<Cards> cards = cardsService.getOwnCards(currentUser.getId());
        return ResultUtils.success(cards);
    }
    /**
     * 点击 购买 按钮
     * 1.将对应的“上架”状态的卡牌 修改为 “2-已售出”
     */
    @PostMapping("/buy")
    public BaseResponse<Boolean> buyCards(@RequestBody CardsBuyRequest cardsBuyRequest){
        if(cardsBuyRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"target request is wrong");
        }
        Boolean result = cardsService.buyCards(cardsBuyRequest);
        return ResultUtils.success(result);

    }
    /**
     * 点击 感兴趣 按钮
     * 1.增加对应卡牌的关注量
     * 2.向收藏表添加信息
     * @return
     */
    @PostMapping("/collect/add")
    public BaseResponse<Boolean> collectCards(@RequestBody CardsCollectRequest cardsCollectRequest){
        Users user = usersService.getUser();
        if(cardsCollectRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"target request is null");
        }
        Boolean result = cardsService.collectCards(cardsCollectRequest);
        Long collectId = userFavoriteService.addCollectCards(cardsCollectRequest.getId(),user.getId());
        return ResultUtils.success(result);
    }
    /**
     * 取消收藏
     * 1. 减去收藏表相应的信息
     */
    @PostMapping("/collect/delete")
    public BaseResponse<Boolean> collectCancelCards(@RequestBody CardsCollectCancelRequest cardsCollectCancelRequest){
        Users user = usersService.getUser();
        if(cardsCollectCancelRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"target request is null");
        }



        Boolean result = userFavoriteService.collectCancelCards(cardsCollectCancelRequest,user.getId());
        return ResultUtils.success(result);
    }

}

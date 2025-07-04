package com.nuk3m1.ocgtradingsystem.controller;

import com.nuk3m1.ocgtradingsystem.common.BaseResponse;
import com.nuk3m1.ocgtradingsystem.common.ResultUtils;
import com.nuk3m1.ocgtradingsystem.enums.ErrorCode;
import com.nuk3m1.ocgtradingsystem.exception.BusinessException;
import com.nuk3m1.ocgtradingsystem.model.entity.UserBanned;
import com.nuk3m1.ocgtradingsystem.model.entity.Users;
import com.nuk3m1.ocgtradingsystem.model.request.BannedFailRequest;
import com.nuk3m1.ocgtradingsystem.model.request.BannedPassRequest;
import com.nuk3m1.ocgtradingsystem.model.request.BannedSendRequest;
import com.nuk3m1.ocgtradingsystem.service.UserBannedService;
import com.nuk3m1.ocgtradingsystem.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/banned")
public class UserBannedController {
    @Resource
    private UsersService usersService;
    @Resource
    private UserBannedService userBannedService;


    /**
     * 返回所有状态码为0的举报记录(状态-待审核)
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<List<UserBanned>> getUserBanned(){
        Users currentUser = usersService.getUser();
        if(Objects.equals(currentUser.getType(), "USER")){
            throw new BusinessException(ErrorCode.PERMISSION_ERROR);
        }
        List<UserBanned> userBannedList = userBannedService.getUserBanned();
        return ResultUtils.success(userBannedList);
    }

    /**
     * Admin权限
     * 对待审核的举报进行 通过 操作
     * @return
     */
    @PostMapping("/pass")
    public BaseResponse<Boolean> passRequest(BannedPassRequest passBannedRequest){
        if(passBannedRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"target request is null");
        }
        Users currentUser = usersService.getUser();
        if(Objects.equals(currentUser.getType(), "USER")){
            throw new BusinessException(ErrorCode.PERMISSION_ERROR,"operation is not allowed");
        }
        Boolean result = userBannedService.passRequest(passBannedRequest);
        return ResultUtils.success(result);
    }

    /**
     * Admin权限
     * 对待审核的举报进行 驳回 操作
     * @return
     */
    @PostMapping("/fail")
    public BaseResponse<Boolean> failRequest(BannedFailRequest failBannedRequest){
        if(failBannedRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"target request is null");
        }
        Users currentUser = usersService.getUser();
        if(Objects.equals(currentUser.getType(),"USER")){
            throw new BusinessException(ErrorCode.PERMISSION_ERROR,"operation is not allowed");
        }
        Boolean result = userBannedService.failRequest(failBannedRequest);
        return ResultUtils.success(result);
    }

    /**
     * 全体用户
     * 发送举报请求  设置初始状态码
     * @return
     */
    @PostMapping("/send")
    public BaseResponse<Long> sendRequest(BannedSendRequest bannedSendRequest){
        if(bannedSendRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG);
        }
        Long result = userBannedService.sendRequest(bannedSendRequest);
        return ResultUtils.success(result);

    }
}

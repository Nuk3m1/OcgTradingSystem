package com.nuk3m1.ocgtradingsystem.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuk3m1.ocgtradingsystem.common.ThrowUtils;
import com.nuk3m1.ocgtradingsystem.enums.ErrorCode;
import com.nuk3m1.ocgtradingsystem.exception.BusinessException;
import com.nuk3m1.ocgtradingsystem.mapper.UsersMapper;
import com.nuk3m1.ocgtradingsystem.model.entity.UserBanned;
import com.nuk3m1.ocgtradingsystem.model.entity.Users;
import com.nuk3m1.ocgtradingsystem.model.request.BannedFailRequest;
import com.nuk3m1.ocgtradingsystem.model.request.BannedPassRequest;
import com.nuk3m1.ocgtradingsystem.model.request.BannedSendRequest;
import com.nuk3m1.ocgtradingsystem.service.UserBannedService;
import com.nuk3m1.ocgtradingsystem.mapper.UserBannedMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author legion
* @description 针对表【user_banned】的数据库操作Service实现
* @createDate 2025-06-27 22:03:43
*/
@Service
public class UserBannedServiceImpl extends ServiceImpl<UserBannedMapper, UserBanned>
    implements UserBannedService {
    @Resource
    private UsersMapper usersMapper;


    @Override
    public List<UserBanned> getUserBanned() {
        List<UserBanned> userBannedList = baseMapper.selectList(Wrappers.<UserBanned>lambdaQuery()
                .eq(UserBanned::getStatus,0));
        // List<UserBanned> userBannedList = baseMapper.selectList(lambdaQuery().eq(UserBanned::getStatus,0));
        return userBannedList;

    }

    @Override
    public Boolean passRequest(BannedPassRequest passBannedRequest) {
        if(passBannedRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG);
        }
        UserBanned userBanned = new UserBanned();
        userBanned.setStatus(1);
        BeanUtils.copyProperties(passBannedRequest,userBanned);
        validUserBanned(userBanned,true);
        Users user = usersMapper.selectOne(
                Wrappers.<Users>lambdaQuery()
                .eq(Users::getId,userBanned.getBanUser()));
        // 踢人下线 封禁账户
        StpUtil.kickout(user.getId());
        StpUtil.disable(user.getId(),-1);

        baseMapper.updateById(userBanned);

        return true;
    }

    @Override
    public Boolean failRequest(BannedFailRequest failBannedRequest) {
        if(failBannedRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG);
        }
        UserBanned userBanned = new UserBanned();
        userBanned.setStatus(2);
        BeanUtils.copyProperties(failBannedRequest,userBanned);
        validUserBanned(userBanned,true);

        baseMapper.updateById(userBanned);
        return true;
    }

    @Override
    public Long sendRequest(BannedSendRequest bannedSendRequest) {
        if(bannedSendRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG);
        }
        // 得到所有的id
        List<Users> users = usersMapper.selectList(null);
        List<Long> BanUserIds = users.stream()
                .map(Users::getId)
                .collect(Collectors.toList());
        // 被举报的用户不存在
        if(!BanUserIds.contains(bannedSendRequest.getBanUser())){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"您举报的用户不存在");
        }
        UserBanned userBanned = new UserBanned();
        userBanned.setStatus(0);
        BeanUtils.copyProperties(bannedSendRequest,userBanned);
        boolean result = baseMapper.insert(userBanned) != 0;
        ThrowUtils.ThrowIf(!result,ErrorCode.OPERATION_ERROR);
        return userBanned.getId();
    }

    private void validUserBanned(UserBanned userBanned,boolean condition){
        if(userBanned == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"UserBanned can not be null");
        }
        Long userbannedId = userBanned.getBanUser();
        Integer status = userBanned.getStatus();

        if (condition) {
            if (userbannedId == null ) {
                throw new BusinessException(ErrorCode.PARAM_IS_WRONG, "params cannot be null");
            }
            if (status != 1 && status != 2 && status != 3) {
                throw new BusinessException(ErrorCode.PARAM_IS_WRONG, "status of banned_list is out of range");
            }
        }
    }
}





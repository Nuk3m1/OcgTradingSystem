package com.nuk3m1.ocgtradingsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuk3m1.ocgtradingsystem.common.ThrowUtils;
import com.nuk3m1.ocgtradingsystem.enums.ErrorCode;
import com.nuk3m1.ocgtradingsystem.exception.BusinessException;
import com.nuk3m1.ocgtradingsystem.model.entity.Post;
import com.nuk3m1.ocgtradingsystem.model.request.PostSendRequest;
import com.nuk3m1.ocgtradingsystem.service.PostService;
import com.nuk3m1.ocgtradingsystem.mapper.PostMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
* @author legion
* @description 针对表【post】的数据库操作Service实现
* @createDate 2025-06-27 22:03:40
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

    @Override
    public Long sendPost(PostSendRequest postSendRequest, Long id) {
        // 自增postId  自取当前用户id作为buyerId
        Post post = new Post();
        if(postSendRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"sendPost request is null");
        }
        if(id == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"target id is null");
        }
        post.setBuyerId(id);
        BeanUtils.copyProperties(postSendRequest,post);
        validPost(post,true);
        //插入
        boolean result = baseMapper.insert(post) !=0 ;
        ThrowUtils.ThrowIf(!result,ErrorCode.OPERATION_ERROR);

        return post.getId();
    }

    @Override
    public List<Post> displayPost(Long id) {
        List<Post> posts = baseMapper.selectList(Wrappers.<Post>lambdaQuery()
                .eq(Post::getSellerId,id));
        return posts;
    }


    private void validPost(Post post,boolean condition){
        if(post == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"Post can not be null");
        }
        Long sellerId = post.getSellerId();
        Long buyerId = post.getBuyerId();
        String message = post.getMessage();

        if (condition) {
            ThrowUtils.ThrowIf(StringUtils.isAnyBlank( message), ErrorCode.PARAM_IS_WRONG, "Message of Post cannot be blank");
            if (sellerId == null ) {
                throw new BusinessException(ErrorCode.PARAM_IS_WRONG, "receiverId cannot be null");
            }
            if (buyerId == null) {
                throw new BusinessException(ErrorCode.PARAM_IS_WRONG, "senderId cannot be null");
            }
        }
    }
}





package com.nuk3m1.ocgtradingsystem.controller;


import com.nuk3m1.ocgtradingsystem.common.BaseResponse;
import com.nuk3m1.ocgtradingsystem.common.ResultUtils;
import com.nuk3m1.ocgtradingsystem.enums.ErrorCode;
import com.nuk3m1.ocgtradingsystem.exception.BusinessException;
import com.nuk3m1.ocgtradingsystem.model.entity.Post;
import com.nuk3m1.ocgtradingsystem.model.entity.Users;
import com.nuk3m1.ocgtradingsystem.model.request.PostSendRequest;
import com.nuk3m1.ocgtradingsystem.service.CardsService;
import com.nuk3m1.ocgtradingsystem.service.PostService;
import com.nuk3m1.ocgtradingsystem.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.xml.transform.Result;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {
    @Resource
    private CardsService cardsService;
    @Resource
    private UsersService usersService;
    @Resource
    private PostService postService;
    /**
     * 发送留言
     */
    @PostMapping("/send")
    public BaseResponse<Long> sendPost(@RequestBody PostSendRequest postSendRequest){
        if(postSendRequest == null){
            throw new BusinessException(ErrorCode.PARAM_IS_WRONG,"send post request is null");
        }
        Users currentUser = usersService.getUser();
        Long PostId = postService.sendPost(postSendRequest,currentUser.getId());
        return ResultUtils.success(PostId);
    }

    /**
     * 显示所有收信方为自己的留言
     * @return List<post>
     */
    @GetMapping("/get")
    public BaseResponse<List<Post>> displayPost(){
        Users currentUser = usersService.getUser();
        List<Post> posts = postService.displayPost(currentUser.getId());
        return ResultUtils.success(posts);

    }


}

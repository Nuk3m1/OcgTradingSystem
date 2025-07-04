package com.nuk3m1.ocgtradingsystem.service;

import com.nuk3m1.ocgtradingsystem.model.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nuk3m1.ocgtradingsystem.model.request.PostSendRequest;

import java.util.List;

/**
* @author legion
* @description 针对表【post】的数据库操作Service
* @createDate 2025-06-27 22:03:40
*/
public interface PostService extends IService<Post> {

    Long sendPost(PostSendRequest postSendRequest, Long id);

    List<Post> displayPost(Long id);
}

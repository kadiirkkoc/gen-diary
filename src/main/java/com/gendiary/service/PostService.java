package com.gendiary.service;

import com.gendiary.dtos.PostDto;
import com.gendiary.dtos.TagDto;
import com.gendiary.model.Post;
import com.gendiary.model.Tag;

import java.util.List;

public interface PostService {

    List<PostDto> getAllPost();
    PostDto getPostById(Long postId);
    String createPost(PostDto postDto);
    String updatePost(Long id,PostDto postDto);
    String deletePost(Long id);


}

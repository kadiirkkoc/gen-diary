package com.gendiary.service;

import com.gendiary.dtos.PostDto;
import java.io.IOException;
import java.util.List;

public interface PostService {

    List<PostDto> getAllPost();
    PostDto getPostById(Long postId);
    String  createPost(PostDto postDto) throws IOException;
    String updatePost(Long id,PostDto postDto);
    String deletePost(Long id);


}

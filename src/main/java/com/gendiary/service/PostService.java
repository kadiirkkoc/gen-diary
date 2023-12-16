package com.gendiary.service;

import com.gendiary.dtos.PostDto;
import com.gendiary.dtos.TagDto;
import com.gendiary.model.Post;
import com.gendiary.model.Tag;

import java.io.IOException;
import java.util.List;

public interface PostService {

    List<PostDto> getAllPost();
    PostDto getPostById(Long postId);
    String createPost(PostDto postDto) throws IOException;
    String updatePost(Long id,PostDto postDto);
    String deletePost(Long id);
    String uploadImage(String imgUrl) throws IOException;
    String getRenderedImage(String aiServiceImgUrl) throws IOException;
    String saveDecodedImageToFile(byte[] decodedImage) throws IOException;


}

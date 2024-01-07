package com.gendiary.controller;

import com.gendiary.dtos.CommentDto;
import com.gendiary.dtos.PostDto;
import com.gendiary.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(){
        return new ResponseEntity<>(postService.getAllPost(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostWithId(@PathVariable(name = "id")Long id){
        return new ResponseEntity<>(postService.getPostById(id),HttpStatus.OK);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<String> createPost(@ModelAttribute PostDto postDto ) throws IOException {
        return new ResponseEntity<>(postService.createPost(postDto),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable Long id, @RequestBody PostDto postDto){
        return new ResponseEntity<>(postService.updatePost(id,postDto),HttpStatus.OK);
    }
}

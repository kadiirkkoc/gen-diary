package com.gendiary.controller;

import com.gendiary.dtos.CommentDto;
import com.gendiary.dtos.PostDto;
import com.gendiary.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<PostDto>> getAllComments(){
        return new ResponseEntity<>(postService.getAllPost(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getCommentWithId(@PathVariable(name = "id")Long id){
        return new ResponseEntity<>(postService.getPostById(id),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createComment(@RequestBody PostDto postDto) throws IOException {
        return new ResponseEntity<>(postService.createPost(postDto),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateComment(@PathVariable Long id, @RequestBody PostDto postDto){
        return new ResponseEntity<>(postService.updatePost(id,postDto),HttpStatus.OK);
    }
}

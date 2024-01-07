package com.gendiary.controller;

import com.gendiary.dtos.CommentDto;
import com.gendiary.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments(){
        return new ResponseEntity<>(commentService.getAllComment(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentWithId(@PathVariable(name = "id")Long id){
        return new ResponseEntity<>(commentService.getCommentById(id),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createComment(@RequestBody CommentDto commentDto){
        return new ResponseEntity<>(commentService.createComment(commentDto),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateComment(@PathVariable Long id, @RequestBody CommentDto commentDto){
        return new ResponseEntity<>(commentService.updateComment(id,commentDto),HttpStatus.OK);
    }
}

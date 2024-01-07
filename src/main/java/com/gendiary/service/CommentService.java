package com.gendiary.service;

import com.gendiary.dtos.CommentDto;
import com.gendiary.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    List<CommentDto> getAllComment();
    CommentDto getCommentById(Long id);
    String createComment(CommentDto commentDto);
    String updateComment(Long id, CommentDto commentDto);
    String deleteComment(Long id);
    Comment getCommentByUuid(Long id);

    //Comment likeComment(Long id);

    //Comment unlikeComment(Long uuid);
}

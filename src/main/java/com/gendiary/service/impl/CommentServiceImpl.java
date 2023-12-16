package com.gendiary.service.impl;

import com.gendiary.dtos.CommentDto;
import com.gendiary.loggers.MainLogger;
import com.gendiary.loggers.messages.CommentMessage;
import com.gendiary.loggers.messages.UserMessage;
import com.gendiary.model.Comment;
import com.gendiary.model.User;
import com.gendiary.repository.CommentRepository;
import com.gendiary.repository.PostRepository;
import com.gendiary.service.CommentService;
import com.gendiary.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MainLogger logger = new MainLogger(PostServiceImpl.class);
    private final UserService userService;
    private final PostRepository postRepository;

    public CommentServiceImpl(CommentRepository commentRepository, UserService userService, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postRepository = postRepository;
    }

    @Override
    public List<CommentDto> getAllComment() {
        return commentRepository.findAll().stream()
                .map(comment -> CommentDto.builder()
                        .commenterUUID(comment.getCommenterUUID())
                        .commentContent(comment.getCommentContent())
                        .publishedDate(comment.getPublishedDate())
                        .countOfLike(comment.getCountOfLike())
                        .postUUID(comment.getPost().getUuid())
                        .likeList(comment.getLikeList()) //redesign
                        .build()).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.map(com -> CommentDto.builder()
                        .commenterUUID(com.getCommenterUUID())
                        .commentContent(com.getCommentContent())
                        .publishedDate(com.getPublishedDate())
                        .countOfLike(com.getCountOfLike())
                        .postUUID(com.getPost().getUuid())
                        .likeList(com.getLikeList()) //redesign
                        .build())
                .orElseGet(()->{
                            logger.log(CommentMessage.NOT_FOUND + id , HttpStatus.BAD_REQUEST);
                            return null;
                        });
    }

    @Override
    public String createComment(CommentDto commentDto) {
        Comment dbComment = Comment.builder()
                .commenterUUID(commentDto.getCommenterUUID())
                .commentContent(commentDto.getCommentContent())
                .publishedDate(commentDto.getPublishedDate())
                .countOfLike(commentDto.getCountOfLike())
                .post(postRepository.findByUuid(commentDto.getPostUUID()))
                .likeList(commentDto.getLikeList()) //redesign
                .build();
        commentRepository.save(dbComment);
        return CommentMessage.CREATE + dbComment.getId();
    }

    @Override
    public String updateComment(Long id, CommentDto commentDto) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty()){
            logger.log(UserMessage.NOT_FOUND + id , HttpStatus.BAD_REQUEST);
        }
        comment.get().setCommentContent(commentDto.getCommentContent());
        //count of like
        commentRepository.save(comment.get());
        return CommentMessage.UPDATE + comment.get().getId();
    }

    @Override
    public String deleteComment(Long id) {
        commentRepository.deleteById(id);
        return CommentMessage.DELETE + id;
    }

    public Comment getCommentByUuid(Long uuid){
        return commentRepository.findByUuid(String.valueOf(uuid));
    }

    @Override
    public Comment likeComment(Long uuid) {
        User authUser = userService.getAuthenticatedUser();
        Comment targetComment = getCommentByUuid(uuid);
        if (!targetComment.getLikeList().contains(authUser)) {
            targetComment.setCountOfLike(targetComment.getCountOfLike()+1);
            targetComment.getLikeList().add(authUser);
            targetComment.setDateLastModified((Timestamp) new Date());
            Comment updatedComment = commentRepository.save(targetComment);
            return updatedComment;
        } else {
            logger.log(CommentMessage.INVALID_OPERATION + uuid,HttpStatus.BAD_REQUEST);
            return null;
        }
    }

    @Override
    public Comment unlikeComment(Long uuid) {
        User authUser = userService.getAuthenticatedUser();
        Comment targetComment = getCommentByUuid(uuid);
        if (targetComment.getLikeList().contains(authUser)) {
            targetComment.setCountOfLike(targetComment.getCountOfLike()-1);
            targetComment.getLikeList().remove(authUser);
            targetComment.setDateLastModified((Timestamp) new Date());
            Comment updatedComment = commentRepository.save(targetComment);

            return updatedComment;
        } else {
            logger.log(CommentMessage.INVALID_OPERATION + uuid,HttpStatus.BAD_REQUEST);
            return null;
        }
    }
}

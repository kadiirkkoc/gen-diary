package com.gendiary.service.impl;

import com.gendiary.dtos.PostDto;
import com.gendiary.loggers.MainLogger;
import com.gendiary.loggers.messages.PostMessage;
import com.gendiary.loggers.messages.UserMessage;
import com.gendiary.model.Post;
import com.gendiary.model.User;
import com.gendiary.repository.PostRepository;
import com.gendiary.repository.UserRepository;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PostServiceImpl implements com.gendiary.service.PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final MainLogger logger = new MainLogger(PostServiceImpl.class);

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PostDto> getAllPost() {
        return postRepository.findAll().stream()
                .map(post -> PostDto.builder()
                        .content(post.getContent())
                        .renderedImageUrl(post.getRenderedImageUrl())
                        .likeCount(post.getLikeCount())
                        .commentCount(post.getCommentCount())
                        .dateCreated(post.getDateCreated())
                        .postOwnerUUID(post.getUser().getUuid())
                        .postComments(post.getPostComments())
                        .postTags(post.getPostTags())
                        .build()).collect(Collectors.toList());

    }

    @Override
    public PostDto getPostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        return post.map(x -> PostDto.builder()
                    .content(x.getContent())
                    .renderedImageUrl(x.getRenderedImageUrl())
                    .likeCount(x.getLikeCount())
                    .commentCount(x.getCommentCount())
                    .dateCreated(x.getDateCreated())
                    .postOwnerUUID(x.getUser().getUuid())
                    .postComments(x.getPostComments())
                    .postTags(x.getPostTags())
                    .build())
                .orElseGet(() -> {
                    logger.log(PostMessage.NOT_FOUND + postId, HttpStatus.BAD_REQUEST);
                    return null;
                });

    }

    @Override
    public String createPost(PostDto postDto) {
        Optional<User> user = userRepository
                .findByUuid(postDto.getPostOwnerUUID());
        if (user.isPresent()){
            postRepository.save(Post.builder()
                    .uuid(UUID.randomUUID().toString())
                            .content(postDto.getContent())
                            .uploadedImageUrl(postDto.getUploadedImageUrl())
                            .renderedImageUrl(postDto.getRenderedImageUrl())
                            .likeCount(0)
                            .commentCount(0)
                            .dateCreated(postDto.getDateCreated())
                            .postComments(null)
                            .postTags(null)
                    .build());
        }
        else {
            logger.log(PostMessage.NOT_FOUND_WITH_UUID + postDto
                    .getPostOwnerUUID(), HttpStatus.BAD_REQUEST);
        }
        return PostMessage.CREATE;
    }

    @Override
    public String updatePost(Long id,PostDto postDto) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()){
            logger.log(PostMessage.NOT_FOUND_WITH_UUID + id, HttpStatus.BAD_REQUEST);
        }
        post.get().setContent(postDto.getContent());
        post.get().setLikeCount(postDto.getLikeCount());
        post.get().setCommentCount(postDto.getCommentCount());
        postRepository.save(post.get());
        return PostMessage.UPDATE + post.get().getId();
    }

    @Override
    public String deletePost(Long id) {
        postRepository.deleteById(id);
        return PostMessage.DELETE + id;
    }
}

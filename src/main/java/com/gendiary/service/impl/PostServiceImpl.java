package com.gendiary.service.impl;

import com.gendiary.dtos.PostDto;
import com.gendiary.loggers.MainLogger;
import com.gendiary.loggers.messages.PostMessage;
import com.gendiary.model.Post;
import com.gendiary.model.User;
import com.gendiary.repository.PostRepository;
import com.gendiary.repository.UserRepository;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        return null;
    }

    @Override
    public PostDto getPostById(Long postId) {
        return null;
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
        return null;
    }

    @Override
    public String deletePost(Long id) {
        return null;
    }
}

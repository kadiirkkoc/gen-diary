package com.gendiary.service.impl;

import com.gendiary.dtos.PostDto;
import com.gendiary.loggers.MainLogger;
import com.gendiary.loggers.messages.PostMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import io.micrometer.common.util.StringUtils;
import org.apache.commons.io.IOUtils;
import java.util.Base64;
import com.gendiary.model.Post;
import com.gendiary.model.User;
import com.gendiary.repository.PostRepository;
import com.gendiary.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;


public class PostServiceImpl implements com.gendiary.service.PostService {

    @Value("${ai-service.process-url}")
    private String aiServiceProcessUrl;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final MainLogger logger = new MainLogger(PostServiceImpl.class);




    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.aiServiceProcessUrl = aiServiceProcessUrl;
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
    public String createPost(PostDto postDto) throws IOException {
        Optional<User> user = userRepository
                .findByUuid(postDto.getPostOwnerUUID());
        if (user.isPresent()) {
            postRepository.save(Post.builder()
                    .uuid(UUID.randomUUID().toString())
                    .content(postDto.getContent())
                    .uploadedImageUrl(postDto.getUploadedImageUrl())
                    .renderedImageUrl(getRenderedImage(aiServiceProcessUrl))
                    .likeCount(0)
                    .commentCount(0)
                    .dateCreated(postDto.getDateCreated())
                    .postComments(null)
                    .postTags(null)
                    .build());
        } else {
            logger.log(PostMessage.NOT_FOUND_WITH_UUID + postDto
                    .getPostOwnerUUID(), HttpStatus.BAD_REQUEST);
        }
        return PostMessage.CREATE;
    }

    @Override
    public String updatePost(Long id, PostDto postDto) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
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

    public String uploadImage(String imgUrl) throws IOException {
        URL url = new URL(imgUrl);

        try (InputStream in = url.openStream()) {
            byte[] imageData = IOUtils.toByteArray(in); // You can use a library like Apache Commons IO for this
            String encodedImage = Base64.getEncoder().encodeToString(imageData);
            String processedResult = restTemplate.postForObject(aiServiceProcessUrl, encodedImage, String.class);
            return processedResult;
        } catch (IOException e) {
            // Handle errors, e.g., invalid URL, network issues, or AI service request failures
            e.printStackTrace();
            return null;
        }
    }

    public String getRenderedImage(String aiServiceImgUrl) throws IOException {
        if (StringUtils.isEmpty(aiServiceImgUrl)) {
            // Handle invalid input, e.g., return an error response or throw an exception
            throw new IllegalArgumentException("aiServiceImgUrl is empty or null");
        }

        try {
            byte[] decodedImage = Base64.getDecoder().decode(aiServiceImgUrl);
            String decodedImagePath = saveDecodedImageToFile(decodedImage);
            return decodedImagePath;
        } catch (IllegalArgumentException e) {
            // Handle invalid Base64 data, e.g., return an error response or throw an exception
            throw new IllegalArgumentException("Invalid Base64 data", e);
        } catch (IOException e) {
            // Handle file I/O errors
            e.printStackTrace();
            throw e;
        }
    }

    public String saveDecodedImageToFile(byte[] decodedImage) throws IOException {
        Path tempFile = Files.createTempFile("decoded_image", ".jpg");
        Files.write(tempFile, decodedImage);
        return tempFile.toString();

    }
}




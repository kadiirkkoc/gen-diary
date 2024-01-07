package com.gendiary.service.impl;

import com.gendiary.dtos.PostDto;
import com.gendiary.loggers.MainLogger;
import com.gendiary.loggers.messages.PostMessage;

import java.io.*;

import com.gendiary.service.UserService;
import com.gendiary.utils.FileNamingUtil;
import com.gendiary.utils.FileUploadUtil;
import com.google.gson.JsonElement;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import java.sql.Timestamp;
import java.util.*;

import java.net.URL;

import com.gendiary.model.Post;
import com.gendiary.model.User;
import com.gendiary.repository.PostRepository;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostServiceImpl implements com.gendiary.service.PostService {

    @Value("${ai-service.process-url}")
    private String aiServiceProcessUrl;
    private final PostRepository postRepository;
    private final UserService userService;
    private final UserCredentialsServiceImpl userCredentialsService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final MainLogger logger = new MainLogger(PostServiceImpl.class);
    private final Environment environment;
    private final FileNamingUtil fileNamingUtil;
    private final FileUploadUtil fileUploadUtil;

    public PostServiceImpl(PostRepository postRepository, UserService userService, UserCredentialsServiceImpl userCredentialsService, Environment environment, FileNamingUtil fileNamingUtil, FileUploadUtil fileUploadUtil) {
        this.aiServiceProcessUrl = aiServiceProcessUrl;
        this.postRepository = postRepository;
        this.userService = userService;
        this.userCredentialsService = userCredentialsService;
        this.environment = environment;
        this.fileNamingUtil = fileNamingUtil;
        this.fileUploadUtil = fileUploadUtil;
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
                        .build())
                .collect(Collectors.toList());
    }
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

        User authUser = userService.getAuthenticatedUser();
        if (authUser == null) {
            System.out.println("user null");
        }

        Post newPost = new Post();
        newPost.setContent(postDto.getContent());
        newPost.setUser(authUser);
        newPost.setLikeCount(0);
        postDto.setCountry(authUser.getCountry());
        newPost.setPostOwnerUuid(authUser.getUuid());
        newPost.setPostOwnerUsername(authUser.getUsername());
        if (authUser.getCountry() == null) {
            throw new IllegalStateException("Authenticated user does not have a country set.");
        }
        newPost.setPostComments(null);
        if (postDto.getImage() != null) {
            String processedImageUrl = processImageWithAIService(postDto);
            newPost.setRenderedImageUrl(processedImageUrl);
        } else {
            newPost.setRenderedImageUrl(null);
        }

        newPost.setDateCreated(new Timestamp(System.currentTimeMillis()));
        postRepository.save(newPost);
        return logger.log(PostMessage.CREATE + newPost.getUuid(), HttpStatus.OK);
    }



    private String processImageWithAIService(PostDto postDto) throws IOException {
        byte[] imageBytes = postDto.getImage().getBytes();
        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

        HttpPost httpPost = new HttpPost(aiServiceProcessUrl);
        JsonObject jsonPayload = new JsonObject();
        jsonPayload.addProperty("image", encodedImage);
        jsonPayload.addProperty("country", postDto.getCountry());

        StringEntity entity = new StringEntity(jsonPayload.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost)) {

            int statusCode = response.getStatusLine().getStatusCode();
            String responseString = EntityUtils.toString(response.getEntity());

            if (statusCode != 200) {
                logger.error("AI service returned a non-OK status code: " + statusCode);
                logger.error("Error response from the AI service: " + responseString);
                throw new RuntimeException("AI service returned a non-OK status code: " + statusCode + " - " + responseString);
            }

            JsonObject jsonResponse = new JsonParser().parse(responseString).getAsJsonObject();
            if (jsonResponse.has("image_data")) {
                String encodedProcessedImage = jsonResponse.get("image_data").getAsString();
                byte[] decodedBytes = Base64.getDecoder().decode(encodedProcessedImage);
                return saveDecodedImage(decodedBytes);
            } else {
                throw new RuntimeException("The response from the AI service does not contain 'image_data'.");
            }
        } catch (IOException e) {
            logger.error("An IOException occurred when processing the image with the AI service: ");
            throw e;
        }
    }


    private String saveDecodedImage(byte[] imageBytes) throws IOException {
        String uploadDir = environment.getProperty("upload.rendered.images");
        String imageName = "rendered_" + UUID.randomUUID() + ".jpg";
        String imagePath = uploadDir + File.separator + imageName;

        File outputFile = new File(imagePath);
        outputFile.getParentFile().mkdirs(); // Create the directory structure if it doesn't exist

        try (OutputStream out = new FileOutputStream(imagePath)) {
            out.write(imageBytes);
        }
        return environment.getProperty("app.root.backend") + File.separator + uploadDir + File.separator + imageName;
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

}




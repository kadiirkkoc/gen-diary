package com.gendiary.dtos;

import com.gendiary.model.Comment;
import com.gendiary.model.Tag;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private String content;
    private MultipartFile image;
    private MultipartFile renderedImage;
    private Integer likeCount;
    private Integer commentCount;
    private String likerUUIDS;
    private Timestamp dateCreated;
    private String postOwnerUUID;
    private List<Comment> postComments;
    private List<Tag> postTags;
    private String authUserEmail;
    private String country;

}



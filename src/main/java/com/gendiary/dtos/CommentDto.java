package com.gendiary.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gendiary.model.Post;
import com.gendiary.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private String commenterUUID;
    private String commentContent;
    private Timestamp publishedDate;
    private Long countOfLike;
    private String postUUID;
    private List<User> likeList;
}

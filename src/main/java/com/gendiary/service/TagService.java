package com.gendiary.service;

import com.gendiary.dtos.TagDto;
import com.gendiary.dtos.UserDto;
import com.gendiary.model.Tag;
import com.gendiary.model.User;

import java.util.List;

public interface TagService {

    List<TagDto> getAllTag();
    TagDto getTagById(Long tagId);
    String createTag(TagDto tagDto);
    String updateTag(TagDto tagDto);
    String deleteTag(Long id);
}

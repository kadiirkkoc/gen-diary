package com.gendiary.service.impl;

import com.gendiary.dtos.TagDto;
import com.gendiary.model.Tag;
import com.gendiary.repository.TagRepository;
import com.gendiary.service.TagService;

import java.util.List;

public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<TagDto> getAllTag() {
        return null;
    }

    @Override
    public TagDto getTagById(Long tagId) {
        return null;
    }

    @Override
    public String createTag(TagDto tagDto) {
        return null;
    }

    @Override
    public String updateTag(TagDto tagDto) {
        return null;
    }

    @Override
    public String deleteTag(Long id) {
        return null;
    }
}

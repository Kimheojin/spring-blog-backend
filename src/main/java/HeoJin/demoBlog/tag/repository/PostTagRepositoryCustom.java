package HeoJin.demoBlog.tag.repository;

import HeoJin.demoBlog.tag.dto.response.TagResponseDto;

import java.util.List;

public interface PostTagRepositoryCustom {

    List<TagResponseDto> getCountWithTagId();
}

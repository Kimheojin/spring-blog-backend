package HeoJin.demoBlog.tag.repository;

import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.tag.dto.response.TagResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostTagRepositoryCustom {

    List<TagResponseDto> getCountWithTagId();

    Page<Post> findPublishedPostWithTag(Long tagId, Pageable pageable);
}

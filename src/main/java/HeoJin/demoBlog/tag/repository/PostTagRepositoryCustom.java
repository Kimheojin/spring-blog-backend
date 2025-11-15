package HeoJin.demoBlog.tag.repository;

import HeoJin.demoBlog.post.dto.response.TagResponse;
import HeoJin.demoBlog.tag.dto.data.PostIdWithTagDto;
import HeoJin.demoBlog.tag.dto.response.PostTagResponseDto;
import HeoJin.demoBlog.tag.dto.response.TagResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PostTagRepositoryCustom {

    List<TagResponseDto> getCountWithTagId();

    Page<PostTagResponseDto> findPublishedPostWithTag(Long tagId, Pageable pageable);

    List<PostIdWithTagDto> getTagListWithPostIdList(List<Long> postIds);

    List<TagResponse> getTagListWithPostId(Long postId);

    Map<Long, List<String>> findAllTagListWithPostPublishedId();
}

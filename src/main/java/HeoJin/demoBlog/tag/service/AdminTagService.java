package HeoJin.demoBlog.tag.service;


import HeoJin.demoBlog.global.exception.CustomNotFound;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.tag.dto.request.ListAddTagRequestDto;
import HeoJin.demoBlog.tag.dto.request.ListDeleteTagRequest;
import HeoJin.demoBlog.tag.dto.response.ListTagResponse;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminTagService {

    private final TagManager tagManager;
    private final PostTagRepository postTagRepository;
    private final PostRepository postRepository;



    @Transactional
    public ListTagResponse addTagPost(ListAddTagRequestDto listAddTagRequestDto) {
        // 해당 post 값
        Long postId = listAddTagRequestDto.postId();
        // 검증
        if (!postRepository.existsById(postId)) {
            throw new CustomNotFound("해당 post가 존재하지 않습니다.");
        }

        listAddTagRequestDto.DtoList().forEach(
                addTagDtoRequest
                        -> tagManager.addTagPost(addTagDtoRequest.getTagName(), postId)
        );

        return new ListTagResponse(postTagRepository.getTagListWithPostId(postId));
    }

    // 태그 삭제 메소드
    @Transactional
    public ListTagResponse deleteTag(ListDeleteTagRequest listDeleteTagRequest) {

        long postId = listDeleteTagRequest.postId();
        // 검증
        if(!postRepository.existsById(postId)) {
            throw new CustomNotFound("해당 post 가 존재하지 않습니다,");
        }

        listDeleteTagRequest.DtoList().forEach(
                deleteTagDtoRequest
                        -> tagManager.deleteTagPost(deleteTagDtoRequest.getTagName(), postId)
        );

        return new ListTagResponse(postTagRepository.getTagListWithPostId(postId));

    }
}

package HeoJin.demoBlog.tag.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ListAddTagRequestDto(
        List<AddTagDtoRequest> DtoList,
        @NotNull(message = "postId 값이 비어있습니다.")
        Long postId
) {
}

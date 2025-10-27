package HeoJin.demoBlog.tag.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ListDeleteTagRequest(
        List<DeleteTagDtoRequest> DtoList,
        @NotNull(message = "postId 값이 비어있습니다.")
        Long postId
) {
}

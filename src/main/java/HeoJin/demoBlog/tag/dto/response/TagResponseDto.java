package HeoJin.demoBlog.tag.dto.response;

import HeoJin.demoBlog.tag.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagResponseDto {
    private String tagName;
    private Long tagId;

    public static TagResponseDto fromEntity(Tag tag) {
        return TagResponseDto.builder()
                .tagName(tag.getTagName())
                .tagId(tag.getId())
                .build();
    }
}

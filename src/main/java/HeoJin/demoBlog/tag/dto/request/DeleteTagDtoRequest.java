package HeoJin.demoBlog.tag.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class DeleteTagDtoRequest {
    private String tagName;
}

package HeoJin.demoBlog.seo.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class TriggerResponseDto {

    private int updateCount;
    private int insertCount;
    private int deleteCount;
}

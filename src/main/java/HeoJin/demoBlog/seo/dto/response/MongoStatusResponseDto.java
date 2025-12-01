package HeoJin.demoBlog.seo.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MongoStatusResponseDto {
    // 생성된 데이터 수
    private long seoDataCount;
}

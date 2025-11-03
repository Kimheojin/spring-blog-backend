package HeoJin.demoBlog.post.dto.request;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledPostRequest {

    @NotBlank(message = "제목을 입력해 주세요")
    @Size(max = 100, message = "제목은 100자를 넘지 못합니다.")
    private String title;
    @NotBlank(message = "내용을 입력해 주세요")
    private String content;
    @NotBlank(message = "카테고리 명을 입력해 주세요")
    private String categoryName;
    
    @Future(message = "예약발행 날짜는 현재 시간 이후로 설정해 주세요")
    @NotNull(message = "예약발행 날짜를 다시 설정해 주세요")
    private LocalDateTime regDate;

    private List<TagRequest> tagList;


}

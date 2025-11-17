package HeoJin.demoBlog.member.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateDto {
    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
}

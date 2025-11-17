package HeoJin.demoBlog.member.controller;

import HeoJin.demoBlog.member.dto.request.PasswordUpdateDto;
import HeoJin.demoBlog.member.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PatchMapping("/auth/password")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> passwordUpdate(
            @RequestBody @Valid PasswordUpdateDto passwordUpdateDto
    ) {
        accountService.updatePassword(passwordUpdateDto);
        return ResponseEntity.ok("비밀번호 변경이 완료되었습니다.");
    }
}

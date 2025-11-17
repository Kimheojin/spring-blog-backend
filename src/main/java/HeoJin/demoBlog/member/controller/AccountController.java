package HeoJin.demoBlog.member.controller;

import HeoJin.demoBlog.member.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PatchMapping("/auth/password")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> passwordUpate() {
        return ResponseEntity.ok("hello");
    }
}

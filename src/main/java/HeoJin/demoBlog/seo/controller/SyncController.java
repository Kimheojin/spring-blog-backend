package HeoJin.demoBlog.seo.controller;


import HeoJin.demoBlog.seo.dto.response.TriggerResponseDto;
import HeoJin.demoBlog.seo.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    // 사용자가 직접 싱크 맞추는
    @PostMapping("/seo/mongo-sync")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TriggerResponseDto> triggerSync() {
        TriggerResponseDto triggerResponseDto = syncService.triggerSync();

        return ResponseEntity.ok(triggerResponseDto);
    }


}

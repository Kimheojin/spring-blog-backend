package HeoJin.demoBlog.global.scheduler.seo;


import HeoJin.demoBlog.seo.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test & !performance") // 부하 테스트 시
public class MongoScheduler {

    private final SyncService syncService;
    // KST 기준
    // mongo + mysql 동기화 관련
    @Scheduled(cron = "0 0 4 * * ?")// 매일 새벽 4시 동기화
    public void runTaskAt4AM(){
        syncService.triggerSync();
    }
}

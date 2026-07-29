package HeoJin.demoBlog.global.scheduler.seo;


import HeoJin.demoBlog.seo.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoScheduler {

    private static final String SYNC_MONGO_SEARCH_INDEX_CRON = "0 0 4 * * ?";

    private final SyncService syncService;

    // KST 기준
    @Scheduled(cron = SYNC_MONGO_SEARCH_INDEX_CRON)
    public void syncMongoSearchIndex(){
        syncService.triggerSync();
    }
}

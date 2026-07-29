package HeoJin.demoBlog.global.scheduler.seo;

import HeoJin.demoBlog.seo.service.SyncService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MongoSchedulerTest {

    @InjectMocks
    private MongoScheduler mongoScheduler;

    @Mock
    private SyncService syncService;

    @Test
    @DisplayName("Mongo 검색 인덱스 동기화 실행")
    void test1() {
        // when
        mongoScheduler.syncMongoSearchIndex();

        // then
        verify(syncService).triggerSync();
    }
}

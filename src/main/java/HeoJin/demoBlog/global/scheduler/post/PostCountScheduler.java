package HeoJin.demoBlog.global.scheduler.post;


import HeoJin.demoBlog.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostCountScheduler {

    private final CategoryRepository categoryRepository;

    // postCount 정합성 스케쥴 메서드
    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional
    public void runTask6AM() {

        // syncPostCounts -> published where 처리 되어 잇음
        categoryRepository.findAll().forEach(category -> {
            categoryRepository.syncPostCounts(category.getId());
        });
    }
}

package HeoJin.demoBlog.global.scheduler.post;


import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PostScheduler {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    // 매일 새벽 5시
    // 예약 발행 관련
    @Scheduled(cron = "0 0 5 * * ?")
    @Transactional
    public void runTask5AM(){
        // oom 발생 가능성 농후
        List<Post> byStatus = postRepository.findByStatus(PostStatus.SCHEDULED);
        LocalDateTime now = LocalDateTime.now();

        // 전체 category 업데이트 관련 set
        Set<Long> categoryIdsToSync = new HashSet<>();

        byStatus.forEach(post -> {
            if(post.getRegDate().isBefore(now)){
                // 변경 감지
                post.changeStatus(PostStatus.PUBLISHED);
                post.changeRegDate(now);
                categoryIdsToSync.add(post.getCategory().getId());
            }
        });
        // category Id 인자로 받아서 정합성 및 업데이트
        categoryIdsToSync.forEach(categoryRepository::syncPostCounts);
    }

}

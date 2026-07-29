package HeoJin.demoBlog.global.scheduler.post;


import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PostScheduler {

    private static final String PUBLISH_SCHEDULED_POSTS_CRON = "0 0 5 * * ?";

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final Clock clock;

    // 예약 발행 관련
    @Scheduled(cron = PUBLISH_SCHEDULED_POSTS_CRON)
    @Transactional
    public void publishScheduledPosts(){
        LocalDateTime now = LocalDateTime.now(clock);
        List<Post> postsToPublish = postRepository.findByStatusAndRegDateBefore(PostStatus.SCHEDULED, now);

        Set<Long> categoryIdsToSync = new HashSet<>();

        postsToPublish.forEach(post -> {
            post.changeStatus(PostStatus.PUBLISHED);
            post.changeRegDate(now);
            categoryIdsToSync.add(post.getCategory().getId());
        });

        categoryIdsToSync.forEach(categoryRepository::syncPostCounts);
    }

}

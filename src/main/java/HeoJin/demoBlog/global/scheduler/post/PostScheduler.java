package HeoJin.demoBlog.global.scheduler.post;


import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostScheduler {

    private final PostRepository postRepository;

    // 매일 새벽 5시
    @Scheduled(cron = "0 0 5 * * ?")
    public void runTask5AM(){
        List<Post> byStatus = postRepository.findByStatus(PostStatus.SCHEDULED);

        byStatus.forEach(post -> {
            if(post.getRegDate().isBefore(LocalDateTime.now())){
                post.changeStatus(PostStatus.PUBLISHED);
                post.changeRegDate(LocalDateTime.now());
            }
        });
    }

}

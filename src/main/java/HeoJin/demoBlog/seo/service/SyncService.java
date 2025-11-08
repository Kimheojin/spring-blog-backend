package HeoJin.demoBlog.seo.service;


import HeoJin.demoBlog.global.exception.CustomNotFound;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.seo.dto.response.TriggerResponseDto;
import HeoJin.demoBlog.seo.entity.PostMongo;
import HeoJin.demoBlog.seo.repository.PostMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final PostMongoRepository postMongoRepository;
    private final PostRepository postRepository;
    public TriggerResponseDto triggerSync() {


        List<Post> allPost = postRepository.findAll();
        if (allPost.isEmpty()) {
            throw new CustomNotFound("post 가 존재하지 않습니다.");
        }
        Map<Long, PostMongo> postMysqlMap = new HashMap<>();
        for (Post post : allPost) {
            PostMongo postMongo = PostMongo.builder()
                    .postId(post.getId())
                    .content(post.getContent())
                    .title(post.getTitle())
                    .syncedDate(LocalDateTime.now())
                    .build();
            postMysqlMap.put(postMongo.getPostId(), postMongo);

        }
        TriggerResponseDto triggerResponseDto = compareData(postMysqlMap);
        return triggerResponseDto;




    }
    
    private TriggerResponseDto compareData(Map<Long, PostMongo> postMysqlMap){
        Map<Long, PostMongo> postMongoMap = new HashMap<>();
        postMongoRepository.getAll().forEach(p -> postMongoMap.put(p.getPostId(), p));

        List<PostMongo> postsToUpdate = new ArrayList<>();
        List<PostMongo> postsToInsert = new ArrayList<>();


        for (PostMongo postFromMysql : postMysqlMap.values()) {
            PostMongo postFromMongo = postMongoMap.get(postFromMysql.getPostId());

            if (postFromMongo != null) {
                postsToUpdate.add(postFromMongo.update(postFromMysql));
            } else {
                postsToInsert.add(postFromMysql);
            }
        }

        // Mongo 에는 있지만, Mysql 에는 없는 데이터
        List<PostMongo> postsToDelete = postMongoMap.values().stream()
                .filter(mongoPost -> !postMysqlMap.containsKey(mongoPost.getPostId()))
                .toList();

        if (!postsToInsert.isEmpty()) {
            postMongoRepository.insertAll(postsToInsert);
        }
        if (!postsToUpdate.isEmpty()) {
            postMongoRepository.updateAll(postsToUpdate);
        }
        if (!postsToDelete.isEmpty()) {
            postMongoRepository.deleteAll(postsToDelete);
        }

        return TriggerResponseDto.builder()
                .insertCount(postsToInsert.size())
                .updateCount(postsToUpdate.size())
                .deleteCount(postsToDelete.size())
                .build();

    }
}

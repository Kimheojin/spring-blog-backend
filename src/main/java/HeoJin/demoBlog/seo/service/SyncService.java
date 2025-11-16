package HeoJin.demoBlog.seo.service;


import HeoJin.demoBlog.global.exception.CustomNotFound;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.seo.dto.data.PostForMongoDto;
import HeoJin.demoBlog.seo.dto.response.TriggerResponseDto;
import HeoJin.demoBlog.seo.entity.PostMongo;
import HeoJin.demoBlog.seo.repository.PostMongoRepository;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
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
    private final PostTagRepository postTagRepository;
    public TriggerResponseDto triggerSync() {
        // 기존 post 리스트
        List<PostForMongoDto> allPost
                = postRepository.findPostsForMongo();
        Map<Long, List<String>> allTagListWithPostPublishedId
                = postTagRepository.findAllTagListWithPostPublishedId();
        if (allPost.isEmpty()) {
            throw new CustomNotFound("post 가 존재하지 않습니다.");
        }

        Map<Long, PostMongo> postMysqlMap = new HashMap<>();
        for (PostForMongoDto postForMongoDto : allPost) {
            PostMongo postMongo = PostMongo.builder()
                    .postId(postForMongoDto.getPostId())
                    .plainContent(SyncManager.toPlainText(postForMongoDto.getContent()))
                    .title(postForMongoDto.getTitle())
                    .syncedDate(LocalDateTime.now())
                    .contentHash(SyncManager.makeHashCodeToContent(postForMongoDto.getContent())) // 원본 데이터 해시로 넣기
                    .tagList(allTagListWithPostPublishedId.get(postForMongoDto.getPostId()))
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

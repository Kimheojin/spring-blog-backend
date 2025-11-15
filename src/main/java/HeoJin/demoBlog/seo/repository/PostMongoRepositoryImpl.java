package HeoJin.demoBlog.seo.repository;


import HeoJin.demoBlog.seo.dto.response.ListPostSearchResponseDto;
import HeoJin.demoBlog.seo.entity.PostMongo;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class PostMongoRepositoryImpl implements PostMongoRepository{

    private final MongoTemplate mongoTemplate;

    @Value("${mongo.collectionName}")
    private String collectionName;


    @Override
    public List<PostMongo> getAll() {

        List<PostMongo> all = mongoTemplate.findAll(PostMongo.class, collectionName);

        return all;
    }

    @Override
    public void insertAll(List<PostMongo> postMongoList) {
        // insert 동작
        mongoTemplate.insert(postMongoList, collectionName);

    }

    @Override
    public void updateAll(List<PostMongo> postMongoList) {
        postMongoList.forEach(
                postMongo -> mongoTemplate.save(postMongo)
        );

    }

    @Override
    public ListPostSearchResponseDto getUnifiedSearch(String term) {

        Aggregation postCount = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                        {
                            "$searchMeta" : {
                                "index": "post_search_kr",
                                "text": {
                                    "query": "%s",
                                    "path": ???
                                },
                                "count": {
                                    "type": "total"
                                }
                            }
                        }
                        """.formatted(term)))
        );

        AggregationResults<Document> countResults = mongoTemplate.aggregate(
                postCount,
                collectionName,
                Document.class
        );

        int totalCount = 0;
        if(!countResults.getMappedResults().isEmpty()) {
            Document countDoc = countResults.getMappedResults().get(0);
            totalCount = countDoc.get("count", Document.class)
                    .getLong("total")
                    .intValue();
        }

        // 페이징 처리 해야하나
        // 검색은 검색만 해야 하는 거

        return null;
    }

    @Override
    public void deleteAll(List<PostMongo> postMongoList) {
        postMongoList.forEach(
                postMongo ->  mongoTemplate.remove(postMongo)
        );
    }


}

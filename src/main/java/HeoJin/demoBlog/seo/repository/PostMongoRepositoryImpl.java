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
import org.springframework.data.mongodb.core.query.Query;
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
        // 총 개수 조회

        Aggregation postCount = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                        {
                            "$searchMeta" : {
                                "index": "title_plainContent_kr",
                                "text": {
                                    "query": "%s",
                                    "path": ["title", "plainContent"],
                                    "fuzzy": {
                                        "maxEdits": 1
                                    }
                                },
                                "count": {
                                    "type": "total"
                                }
                              }
                        
                            }
                        }
                        """.formatted(term))) // String term -> 검색어
        );

        AggregationResults<Document> countResults = mongoTemplate.aggregate(
                postCount,
                collectionName,
                Document.class
        );

        int totalCount = 0;
        if (!countResults.getMappedResults().isEmpty()) {
            Document countDoc = countResults.getMappedResults().get(0);
            totalCount = countDoc.get("count", Document.class)
                    .getLong("total")
                    .intValue();
        }
        // 실제 검색 결과
        Aggregation postSearch = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                        {
                            "$search" : {
                                "index": "title_plainContent_kr",
                                "text": {
                                    "query": "%s",
                                    "path": ["title", "plainContent"],
                                    "fuzzy": {
                                        "maxEdits": 1
                                    }
                                }
                            }
                        }
                        """.formatted(term))),
                Aggregation.stage(Document.parse("""
                        {
                            "$project": {
                                "_id": 0,
                                "postID" : 1,
                                "title" : 1,
                                "plainContent": 1,
                                "score": { "$meta": "searchScore" }
                            }
                        }
                        """)),
                Aggregation.stage(Document.parse("""
                        {
                            "$sort": { "score": -1 }
                       
                        }
                        """)),
                Aggregation.limit(10)
                );
        AggregationResults<Document> searchResults = mongoTemplate.aggregate(
                postSearch,
                collectionName,
                Document.class
                );
        List<Document> documents = searchResults.getMappedResults();

        return null;
    }





    @Override
    public void deleteAll(List<PostMongo> postMongoList) {
        postMongoList.forEach(
                postMongo ->  mongoTemplate.remove(postMongo)
        );
    }

    @Override
    public Long getDataCount() {
        Query query = new Query();
        long count = mongoTemplate.count(query, PostMongo.class);
        return count;
    }


}

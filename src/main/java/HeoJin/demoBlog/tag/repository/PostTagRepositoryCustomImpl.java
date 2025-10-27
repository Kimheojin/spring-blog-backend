package HeoJin.demoBlog.tag.repository;

import HeoJin.demoBlog.tag.dto.response.TagResponseDto;
import HeoJin.demoBlog.tag.entity.QPostTag;
import HeoJin.demoBlog.tag.entity.QTag;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostTagRepositoryCustomImpl implements PostTagRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TagResponseDto> getCountWithTagId() {
        QPostTag qPostTag = QPostTag.postTag;
        QTag qTag = QTag.tag;
        return jpaQueryFactory
                .select(Projections.constructor(TagResponseDto.class,
                        qTag.tagName,
                        qTag.id,
                        qPostTag.count()))
                .from(qPostTag)
                .join(qTag).on(qPostTag.tagId.eq(qTag.id))
                .groupBy(qTag.tagName, qTag.id)
                .fetch();
    }
}

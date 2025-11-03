package HeoJin.demoBlog.tag.repository;

import HeoJin.demoBlog.tag.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long>, PostTagRepositoryCustom {

    boolean existsByTagId(Long tagId);

    void deleteByPostIdAndTagId(Long postId, Long tagId);

    void deleteAllByPostId(Long postId);

    List<PostTag> findAllByPostId(Long postId);
}

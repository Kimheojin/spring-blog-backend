package HeoJin.demoBlog.configuration.InitRepository;

import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.entity.Role;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.tag.entity.PostTag;
import HeoJin.demoBlog.tag.entity.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TestInitRepository {

    private final EntityManager em;

    public Optional<Member> findByEmail(String email) {
        return em.createQuery("select m from Member m join fetch m.role where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public Optional<Role> findByRoleName(String roleName) {
        return em.createQuery("select r from Role r where r.roleName = :roleName", Role.class)
                .setParameter("roleName", roleName)
                .getResultStream()
                .findFirst();
    }

    public Optional<Category> findByCategoryName(String CategoryNAme){
        return em.createQuery("select c from Category c where c.categoryName = :categoryName", Category.class)
                .setParameter("categoryName", CategoryNAme)
                .getResultStream()
                .findFirst();
    }

    public List<Post> findAllPost(){
        return em.createQuery("select p from Post p", Post.class)
                .getResultList();
    }

    public Tag findOrCreateTag(String tagName) {
        Optional<Tag> optionalTag = em.createQuery("select t from Tag t where t.tagName = :tagName", Tag.class)
                .setParameter("tagName", tagName)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultStream()
                .findFirst();

        return optionalTag.orElseGet(() -> {
            Tag newTag = Tag.builder()
                    .tagName(tagName)
                    .build();
            em.persist(newTag);
            em.flush();
            return newTag;
        });
    }

    public void createPostTagLink(Long postId, Long tagId) {
        Optional<PostTag> existingPostTag = em.createQuery("select pt from PostTag pt where pt.postId = :postId and pt.tagId = :tagId", PostTag.class)
                .setParameter("postId", postId)
                .setParameter("tagId", tagId)
                .getResultStream()
                .findFirst();

        if (existingPostTag.isEmpty()) {
            PostTag postTag = PostTag.builder()
                    .postId(postId)
                    .tagId(tagId)
                    .build();
            em.persist(postTag);
        }
    }
}

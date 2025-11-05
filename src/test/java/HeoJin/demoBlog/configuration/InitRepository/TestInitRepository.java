package HeoJin.demoBlog.configuration.InitRepository;

import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.entity.Role;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TestInitRepository {

    private final EntityManager em;

    // name 쿼리 사용
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

}

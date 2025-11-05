package HeoJin.demoBlog.configuration.InitRepository;

import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.entity.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestInitRepository {
    // name 쿼리 사용

    @Query("select m from Member m join fetch m.role where m.email = :email")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("select r from Role r where r.roleName = :roleName")
    Optional<Role> findByRoleName(@Param("roleName") String roleName);

}

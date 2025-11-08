package HeoJin.demoBlog.member.repository;


import HeoJin.demoBlog.configuration.InitRepository.TestInitRepository;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.configuration.dataJpaTest.SaveDataJpaTest;
import HeoJin.demoBlog.global.config.QuerydslConfig;
import HeoJin.demoBlog.member.entity.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@DataJpaTest
@Import({QuerydslConfig.class, DataInitComponent.class, TestInitRepository.class, BCryptPasswordEncoder.class})
public class MemberRepositoryTest extends SaveDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DataInitComponent dataInitComponent;

    @BeforeEach
    void setUp(){
        EntityManager em = entityManager.getEntityManager();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        initializeTestData(em, encoder);
    }

    @Test
    @DisplayName("findByEmail -> 정상 작동 테스트")
    void test1() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        String testMemberEmail = testMember.getEmail();
        entityManager.persistAndFlush(testMember);
        entityManager.clear();

        // when
        Optional<Member> findMember = memberRepository.findByEmail(testMemberEmail);
        // then
        Assertions.assertTrue(findMember.isPresent());
        Assertions.assertEquals(testMemberEmail, findMember.get().getEmail());
        Assertions.assertEquals(testMember.getMemberName(), findMember.get().getMemberName());

        Assertions.assertEquals(testMember.getId(), findMember.get().getId());
    }

    @Test
    @DisplayName("findByEmail -> 빈 optional 정상 반환 하는지")
    void test2() {
        // given

        // when
        Optional<Member> findMember = memberRepository.findByEmail("ronaldo@naver.com");
        // then
        Assertions.assertFalse(findMember.isPresent());
        Assertions.assertTrue(findMember.isEmpty());
    }

    @Test
    @DisplayName("findByEmail -> null 이메일로 조회 시 empty 확인")
    void test3() {
        // given
        String nullEmail = null;
        // when
        Optional<Member> findMember = memberRepository.findByEmail(nullEmail);
        // then
        Assertions.assertTrue(findMember.isEmpty());

    }


}

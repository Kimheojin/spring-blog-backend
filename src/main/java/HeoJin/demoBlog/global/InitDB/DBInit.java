package HeoJin.demoBlog.global.InitDB;

import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.entity.Role;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.member.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
@Profile({"!test", "!performance"})
public class DBInit implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        initRoleAndUser();

    }

    // postConstruct -> commandLineRunner
    @Transactional
    protected void initRoleAndUser(){
        final String INIT_EMAIL = "hurjin1109@naver.com";
        final String INIT_ROLE = "ADMIN";

        Optional<Member> existedMember = memberRepository.findByEmail(INIT_EMAIL);

        if(existedMember.isPresent()){
            log.info("초기 사용자가 이미 존재합니다.{}", existedMember.get().getEmail() );
            return;
        }

        Role adminRole = roleRepository.findByRoleName(INIT_ROLE)
                .orElseGet(() -> {
                    log.info("ADMIN 역할을 생성합니다.");
                    return roleRepository.save(Role.builder()
                            .roleName(INIT_ROLE)
                            .build());
                });


        Member member = Member.builder()
                .email(INIT_EMAIL)
                .password(passwordEncoder.encode("1234"))
                .memberName("허진")
                .role(adminRole)
                .build();


        memberRepository.save(member);


        log.info("초기 사용자 데이터가 생성되었습니다: {}", INIT_EMAIL);
    }



}
package HeoJin.demoBlog.configuration.mockUser;

import HeoJin.demoBlog.configuration.InitRepository.TestInitRepository;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.entity.Role;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DemoMockSecurityContext implements WithSecurityContextFactory<WithMockCustomUser> {

    private final BCryptPasswordEncoder passwordEncoder;
    private final TestInitRepository testInitRepository;
    private final EntityManager em;

    @Value("${test.user.email}")
    private String defaultEmail;
    @Value("${test.user.password}")
    private String defaultPassword;
    @Value("${test.user.name}")
    private String defaultMemberName;
    @Value("${test.user.role}")
    private String defaultRole;


    @Override
    @Transactional
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        String email = annotation.email().isEmpty() ? defaultEmail : annotation.email();
        String password = annotation.password().isEmpty() ? defaultPassword : annotation.password();
        String memberName = annotation.memberName().isEmpty() ? defaultMemberName : annotation.memberName();
        String role = annotation.roles().length == 0 || annotation.roles()[0].isEmpty() ? defaultRole : annotation.roles()[0];


        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Member testMember;

        Optional<Member> byEmail = testInitRepository.findByEmail(email);
        if (byEmail.isEmpty()) {
            testMember = createMember(email, password, memberName, role);
        }else {
            testMember = byEmail.get();
        }


        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(testMember.getRole().getRoleName()));


        Authentication auth = new UsernamePasswordAuthenticationToken(testMember.getId(), null, authorities);

        context.setAuthentication(auth);

        return context;
    }


    @Transactional(readOnly = false)
    protected Member createMember(String email, String password, String memberName, String role){
        // DB에 저장 후 반환

        // role
        Role mockRole;

        Optional<Role> byRoleName = testInitRepository.findByRoleName(role);
        if (byRoleName.isEmpty()) {
            Role newRole = Role.builder()
                    .roleName(role).build();
            em.persist(newRole);
            mockRole = newRole;
        }else {
            mockRole = byRoleName.get();
        }
        // member
        Member member = Member.builder()
                .memberName(memberName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(mockRole).build();

        em.persist(member);

        return member;
    }
}

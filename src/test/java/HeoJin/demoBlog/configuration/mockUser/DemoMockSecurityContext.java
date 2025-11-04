package HeoJin.demoBlog.configuration.mockUser;

import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.entity.Role;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.member.repository.RoleRepository;
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
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class DemoMockSecurityContext implements WithSecurityContextFactory<WithMockCustomUser> {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${test.user.email}")
    private String defaultEmail;
    @Value("${test.user.password}")
    private String defaultPassword;
    @Value("${test.user.name}")
    private String defaultMemberName;
    @Value("${test.user.role}")
    private String defaultRole;


    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        String email = annotation.email().isEmpty() ? defaultEmail : annotation.email();
        String password = annotation.password().isEmpty() ? defaultPassword : annotation.password();
        String memberName = annotation.memberName().isEmpty() ? defaultMemberName : annotation.memberName();
        String[] roles = annotation.roles().length == 0 || annotation.roles()[0].isEmpty() ? new String[]{defaultRole} : annotation.roles();


        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Member testMember = memberRepository.findByEmail(email)
                .orElseGet(() -> createMember(email, password, memberName, roles));

        List<SimpleGrantedAuthority> authorities = testMember.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        Authentication auth = new UsernamePasswordAuthenticationToken(testMember.getId(), null, authorities);

        context.setAuthentication(auth);

        return context;
    }


    @Transactional(readOnly = false)
    protected Member createMember(String email, String password, String memberName, String[] roles){
        // DB에 저장 후 반환

        // role
        Role mockRole = roleRepository.findByRoleName(roles[0])
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .roleName(roles[0]).build();
                    return roleRepository.save(newRole);
                });

        // member
        Member member = Member.builder()
                .memberName(memberName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(mockRole).build();

        memberRepository.save(member);

        return member;
    }
}

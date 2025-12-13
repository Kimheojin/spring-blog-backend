package HeoJin.demoBlog.member.service;


import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.global.util.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 회원이 존재하지 않습니다: " + email));

        return new CustomUserDetail(member);
    }


}

package HeoJin.demoBlog.member.service;


import HeoJin.demoBlog.global.exception.refactor.NotFoundException;
import HeoJin.demoBlog.member.dto.request.PasswordUpdateDto;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void updatePassword(PasswordUpdateDto passwordUpdateDto) {
        // 비밀번호 업데이트
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // memberId
        Optional<Member> targetMember = memberRepository.findById(Long.parseLong(auth.getName()));
        if(targetMember.isEmpty()){
            throw new NotFoundException("해당 회원이 DB에 존재하지 않습니다.");
        }
        Member member = targetMember.get();
        member.updatePassword(passwordEncoder.encode(passwordUpdateDto.getNewPassword()));

    }
}

package com.example.thejavatest.Member;

import com.example.thejavatest.domain.Member;
import com.example.thejavatest.domain.Study;

import java.util.Optional;

public interface MemberService {

    Optional<Member> findById(Long memberId);

    void validate(Long memberId);

    void notify(Study newStudy);

    void notify(Member member);

}
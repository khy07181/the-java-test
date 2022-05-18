package com.example.thejavatest.Study;

import com.example.thejavatest.Member.MemberService;
import com.example.thejavatest.domain.Member;
import com.example.thejavatest.domain.Study;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    MemberService memberService;

    @Mock
    StudyRepository studyRepository;

    @Test
    void createNewStudy() {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("khy07181@gmail.com");

        when(memberService.findById(any())).thenReturn(Optional.of(member));

        // Stubbing
        assertEquals(member.getEmail(), memberService.findById(1L).get().getEmail());
        assertEquals(member.getEmail(), memberService.findById(2L).get().getEmail());


        // Throw exception
        doThrow(new IllegalArgumentException()).when(memberService).validate(1L);

        assertThrows(IllegalArgumentException.class, () -> memberService.validate(1L));


        // Stubbing consecutive calls
        when(memberService.findById(any()))
                .thenReturn(Optional.of(member))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.empty());

        assertEquals(member.getEmail(), memberService.findById(1L).get().getEmail());
        assertThrows(RuntimeException.class, () -> memberService.findById(2L));
        assertEquals(Optional.empty(), memberService.findById(3L));
    }

}
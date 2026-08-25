package com.portfolio.mysite.service;

import com.portfolio.mysite.entity.Guestbook;
import com.portfolio.mysite.repository.GuestbookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GuestbookServiceTest {

    @Mock
    private GuestbookRepository guestbookRepository;

    private GuestbookService guestbookService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        guestbookService = new GuestbookService(guestbookRepository, passwordEncoder);
    }

    @Test
    void saveMessageEncodesPasswordBeforePersistingGuestbookEntry() {
        Guestbook guestbook = new Guestbook();
        guestbook.setName("Hyein");
        guestbook.setPassword("1234");
        guestbook.setMessage("방명록 저장 확인 테스트입니다.");

        guestbookService.saveMessage(guestbook);

        assertThat(guestbook.getPassword()).isNotEqualTo("1234");
        assertThat(passwordEncoder.matches("1234", guestbook.getPassword())).isTrue();
        verify(guestbookRepository).save(guestbook);
    }
}

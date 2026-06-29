package com.portfolio.mysite.service;

import com.portfolio.mysite.entity.Guestbook;
import com.portfolio.mysite.repository.GuestbookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GuestbookServiceTest {

    @Mock
    private GuestbookRepository guestbookRepository;

    @InjectMocks
    private GuestbookService guestbookService;

    @Test
    void saveMessagePersistsGuestbookEntry() {
        Guestbook guestbook = new Guestbook();
        guestbook.setName("Hyein");
        guestbook.setPassword("1234");
        guestbook.setMessage("방명록 저장 확인 테스트입니다.");

        guestbookService.saveMessage(guestbook);

        verify(guestbookRepository).save(guestbook);
    }
}

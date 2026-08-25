package com.portfolio.mysite.service;

import com.portfolio.mysite.entity.Guestbook;
import com.portfolio.mysite.repository.GuestbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.lang.NonNull;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuestbookService {

    private final GuestbookRepository guestbookRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Guestbook> getAllMessages() {
        return guestbookRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

   @Transactional
        public void saveMessage(@NonNull Guestbook guestbook)  {
        guestbook.setPassword(passwordEncoder.encode(guestbook.getPassword()));
        guestbookRepository.save(guestbook);
    }

    @Transactional
    public boolean deleteMessage(@NonNull Long id, String password) {
        return guestbookRepository.findById(id)
                .filter(guest -> passwordEncoder.matches(password, guest.getPassword()))
                .map(guest -> {
                    guestbookRepository.delete(guest);
                    return true;
                }).orElse(false);
    }
}

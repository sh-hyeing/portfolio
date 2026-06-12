package com.portfolio.mysite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.portfolio.mysite.entity.Guestbook;
import com.portfolio.mysite.service.GuestbookService;
import com.portfolio.mysite.service.GuestbookSpamGuard;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/guest")
@RequiredArgsConstructor
public class GuestbookController {
    private final GuestbookService guestbookService;
    private final GuestbookSpamGuard guestbookSpamGuard;

    @GetMapping
    public String guestbookPage(Model model) {
        model.addAttribute("guestList", guestbookService.getAllMessages());
        model.addAttribute("guestForm", new Guestbook());
        return "index";
    }

    @PostMapping("/add")
    public String addMessage(@Valid @ModelAttribute("guestForm") Guestbook guestbook,
                             BindingResult result,
                             @RequestParam(value = "website", required = false) String website,
                             @RequestParam(value = "submittedAt", required = false) String submittedAt,
                             HttpServletRequest request,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("guestList", guestbookService.getAllMessages());
            return "index"; 
        }

        var spamCheck = guestbookSpamGuard.check(
                getClientKey(request),
                website,
                submittedAt,
                guestbook.getName(),
                guestbook.getMessage()
        );

        if (!spamCheck.allowed()) {
            redirectAttributes.addFlashAttribute("errorMessage", "\uBC29\uBA85\uB85D \uB4F1\uB85D\uC774 \uC81C\uD55C\uB418\uC5C8\uC2B5\uB2C8\uB2E4. \uC7A0\uC2DC \uD6C4 \uB2E4\uC2DC \uC2DC\uB3C4\uD574 \uC8FC\uC138\uC694.");
            return "redirect:/#guest";
        }

        guestbookService.saveMessage(guestbook);
        return "redirect:/#guest"; 
    }

    @PostMapping("/delete/{id}")
    public String deleteMessage(@PathVariable Long id, 
                                @RequestParam("password") String password, 
                                RedirectAttributes redirectAttributes) {
        boolean success = guestbookService.deleteMessage(id, password);
        
        if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage", "\uBE44\uBC00\uBC88\uD638\uAC00 \uC77C\uCE58\uD558\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4.");
        }
        
        return "redirect:/#guest"; 
    }

    private String getClientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

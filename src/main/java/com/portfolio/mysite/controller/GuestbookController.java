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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/guest")
@RequiredArgsConstructor
public class GuestbookController {
    private final GuestbookService guestbookService;

    @GetMapping
    public String guestbookPage(Model model) {
        model.addAttribute("guestList", guestbookService.getAllMessages());
        model.addAttribute("guestForm", new Guestbook());
        return "guestbook";
    }

   @PostMapping("/add")
    public String addMessage(@Valid @ModelAttribute("guestForm") Guestbook guestbook, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("guestList", guestbookService.getAllMessages());
            return "index"; 
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
            redirectAttributes.addFlashAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
        }
        
        return "redirect:/#guest"; 
    }
}
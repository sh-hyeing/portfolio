package com.portfolio.mysite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.portfolio.mysite.entity.Guestbook;
import com.portfolio.mysite.service.GuestbookService;

import lombok.RequiredArgsConstructor;
import java.util.List; 

import org.springframework.ui.Model; 

@Controller
@RequiredArgsConstructor
public class MainController {
    private final GuestbookService guestbookService; 

    @GetMapping("/")
    public String index(Model model) {
        List<Guestbook> guestList = guestbookService.getAllMessages();
        model.addAttribute("guestList", guestList);
        
        model.addAttribute("guestForm", new Guestbook());
        
        return "index"; 
    }

    @GetMapping("/about")
    public String about() {
        return "about"; 
    }

    @GetMapping("/work")
    public String work() {
        return "work"; 
    }

    @GetMapping("/work/{projectName}")
    public String getWorkDetail(@PathVariable String projectName) {
      return projectName;
    }

}


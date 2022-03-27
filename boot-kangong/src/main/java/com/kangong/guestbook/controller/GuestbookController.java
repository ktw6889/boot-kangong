package com.kangong.guestbook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kangong.guestbook.dto.GuestbookDTO;
import com.kangong.guestbook.dto.PageRequestDTO;
import com.kangong.guestbook.service.GuestbookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/guestbook")
@Log4j2
@RequiredArgsConstructor //자동 주입을 위한 Annotation
public class GuestbookController {

    private final GuestbookService service; //final로 선언

	/*
	 * @GetMapping("/") public String index() { return "redirect:/guestbook/list"; }
	 */

    @GetMapping("/list")
    public String list(PageRequestDTO pageRequestDTO, Model model){
        log.info("list............." + pageRequestDTO);
        model.addAttribute("result", service.getList(pageRequestDTO));
        return "thymeleaf/guestbook/list";
    }

    @GetMapping("/register")
    public String register(){
        log.info("regiser get...");
        return "thymeleaf/guestbook/register";
    }

    @PostMapping("/register")
    public String registerPost(GuestbookDTO dto, RedirectAttributes redirectAttributes){
        log.info("dto..." + dto);
        //새로 추가된 엔티티의 번호
        Long gno = service.register(dto);
        redirectAttributes.addFlashAttribute("msg", gno);
        
        return "redirect:/guestbook/list";
    }

    @GetMapping({"/read"})
    public String read(long gno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model ){

        log.info("gno: " + gno);

        GuestbookDTO dto = service.read(gno);

        model.addAttribute("dto", dto);
        return "thymeleaf/guestbook/read";
    }
    
    @GetMapping({"/modify"})
    public String modify(long gno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model ){

        log.info("gno: " + gno);

        GuestbookDTO dto = service.read(gno);

        model.addAttribute("dto", dto);
        return "thymeleaf/guestbook/modify";
    }

    @PostMapping("/remove")
    public String remove(long gno, RedirectAttributes redirectAttributes){


        log.info("gno: " + gno);

        service.remove(gno);

        redirectAttributes.addFlashAttribute("msg", gno);

        return "redirect:/guestbook/list";

    }

    @PostMapping("/modify")
    public String modify(GuestbookDTO dto,
                         @ModelAttribute("requestDTO") PageRequestDTO requestDTO,
                         RedirectAttributes redirectAttributes){


        log.info("post modify.........................................");
        log.info("dto: " + dto);

        service.modify(dto);

        redirectAttributes.addAttribute("page",requestDTO.getPage());
        redirectAttributes.addAttribute("type",requestDTO.getType());
        redirectAttributes.addAttribute("keyword",requestDTO.getKeyword());
        redirectAttributes.addAttribute("gno",dto.getGno());


        return "redirect:/guestbook/read";

    }




}

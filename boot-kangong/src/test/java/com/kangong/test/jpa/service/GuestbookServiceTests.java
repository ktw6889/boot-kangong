package com.kangong.test.jpa.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kangong.guestbook.dto.GuestbookDTO;
import com.kangong.guestbook.dto.PageRequestDTO;
import com.kangong.guestbook.dto.PageResultDTO;
import com.kangong.guestbook.entity.Guestbook;
import com.kangong.guestbook.service.GuestbookService;

@SpringBootTest
public class GuestbookServiceTests {

    @Autowired
    private GuestbookService service;

   
    public void testRegister() {

        GuestbookDTO guestbookDTO = GuestbookDTO.builder()
                .title("Sample Title...")
                .content("Sample Content...")
                .writer("user0")
                .build();

        System.out.println(service.register(guestbookDTO));

    }

    public void testStream() {
    	ArrayList<Guestbook> guestbookList = new ArrayList<Guestbook>();
    	IntStream.rangeClosed(1,30).forEach(i -> {

            Guestbook guestbook = Guestbook.builder()
            		.gno(Long.parseLong(i+""))
                    .title("Title...." + i)
                    .content("Content..." +i)
                    .writer("user" + (i % 10))
                    .build();
            
            guestbookList.add(guestbook);
        });
    	
    	ArrayList<String> list = new ArrayList<>(Arrays.asList("Apple","Banana","Melon","Grape","Strawberry"));
    	
    	System.out.println("list1: "+list.stream().map(s->s.toUpperCase()).collect(Collectors.toList()));
    	System.out.println("list2: "+list.stream().map(String::toLowerCase).collect(Collectors.toList()));    	
    	System.out.println("guestbookList: "+ guestbookList.stream().filter(guestbook-> guestbook.getGno() < 10).collect(Collectors.toList()));    	
    	System.out.println("init: "+guestbookList);
    
    	//ArrayList guestbookList2 = guestbookList.stream().map(guestbook -> guestbook ).collect(Collectors.toList());    	 
    }
    
    @Test
    public void testList(){

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();

        PageResultDTO<GuestbookDTO, Guestbook> resultDTO = service.getList(pageRequestDTO);

        System.out.println("PREV: "+resultDTO.isPrev());
        System.out.println("NEXT: "+resultDTO.isNext());
        System.out.println("TOTAL: " + resultDTO.getTotalPage());

        System.out.println("-------------------------------------");
        for (GuestbookDTO guestbookDTO : resultDTO.getDtoList()) {
            System.out.println(guestbookDTO);
        }

        System.out.println("========================================");
        resultDTO.getPageList().forEach(i -> System.out.println(i));
    }

}

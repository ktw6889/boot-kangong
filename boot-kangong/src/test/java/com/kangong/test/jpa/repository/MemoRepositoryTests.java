package com.kangong.test.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import com.kangong.guestbook.entity.Guestbook;
import com.kangong.guestbook.repository.GuestbookRepository;
import com.kangong.test.jpa.entity.Memo;

@SpringBootTest
public class MemoRepositoryTests {

	@Autowired
	MemoRepository memoRepository;	
	
	@Autowired
	private GuestbookRepository guestbookRepository;
	
	public void testClass() {
		System.out.println(memoRepository.getClass().getName());
	}	
	
	public void testInsertDummies() {
		IntStream.rangeClosed(1,90).forEach(i->{
			Memo memo = Memo.builder().memoText("Sample..."+i).build();
			memoRepository.save(memo);
		});
	}	
	
	public void testSelect() {
		Long mno  = 10L;
		Optional<Memo> result = memoRepository.findById(mno);
		System.out.println("===================================");
		
		if(result.isPresent()) {
			Memo memo = result.get();
			System.out.println(memo);			
		}
	}
	
	@Transactional
	public void testSelect2() {
		Long mno = 10L;
		Memo memo = memoRepository.getOne(mno);
		System.out.println("==================================");
		System.out.println("memo:"+memo);
	}	
	
	public void testUpdate() {
		Memo memo = Memo.builder().mno(10L).memoText("Update Text").build();
		System.out.println(memoRepository.save(memo));
	}
	
	
	public void testDelete() {
		Long mno = 10L;
		memoRepository.deleteById(mno);
	}
	
	
	public void testPageDefault() {
		Pageable pageable = PageRequest.of(0,10);
		Page<Memo> result = memoRepository.findAll(pageable);
		System.out.println(result);
		System.out.println("====================================");
		System.out.println("Total Pages: "+result.getTotalPages());
		System.out.println("Total Count: "+result.getTotalElements());
		System.out.println("Page Number: "+result.getNumber());
		System.out.println("Page Size: "+result.getSize());
		System.out.println("has next page?: "+result.hasNext());
		System.out.println("first page?: "+result.isFirst());
		System.out.println("====================================");
		for(Memo memo : result.getContent()) {
			System.out.println("memo: "+memo);
		}
	}
	
	public void testSort() {
		Sort sort1 = Sort.by("mno").descending();
		Sort sort2 = Sort.by("memoText").ascending();
		Pageable pageable = PageRequest.of(0,10,sort1);
		Page<Memo> result = memoRepository.findAll(pageable);
		
		result.get().forEach(memo -> {
			System.out.println("memo: "+memo);
		});
	}
	
	public void testQueryMethods() {
		List<Memo> list = memoRepository.findByMnoBetweenOrderByMnoDesc(70L, 80L);
		for(Memo memo : list) {
			System.out.println("memo:"+memo);
		}
	}
	
	public void testQueryMethodWithPagable() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());
		Page<Memo> result = memoRepository.findByMnoBetween(10L, 50L, pageable);
		result.get().forEach(memo -> System.out.println(memo));
	}
	
	//@Test
	//@Transactional
	//@Commit
	public void testDeleteQueryMethods() {
		memoRepository.deleteMemoByMnoLessThan(10L);
	}
	
	@Test
	public void insertGuestbook() {
		IntStream.rangeClosed(1, 300).forEach(i -> {
			Guestbook guestbook = Guestbook.builder()
					.title("Title...."+i)
					.content("Content..."+i)
					.writer("user"+(i%10))
					.build();
			System.out.println(guestbookRepository.save(guestbook));
		});
	}
}

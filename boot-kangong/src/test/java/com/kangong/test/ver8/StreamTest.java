package com.kangong.test.ver8;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StreamTest {

	@Test
	public void stream(){
		/*
		 * double avg = Arrays.stream(new int[] {1,2,3,4,5}) .filter(n -> n%2 == 0)
		 * .average() .getAsDouble(); System.out.println("avg: "+avg);
		 */
		
		List<Integer>  intList = Arrays.asList(new Integer[] {1,2,3,4,5});
		double avgDouble = intList.stream()
					.mapToInt(Integer::intValue)
					.average()
					.getAsDouble();
		System.out.println(avgDouble);
		// 요소가 없어 평균이 없다. 예외 발생
		
	
		//첫번째 예외처리 방법
		OptionalDouble optional = intList.stream()
				.mapToInt(Integer::intValue)
				.average();
		if(optional.isPresent()) {
			System.out.println(optional.getAsDouble() );
		}else {
			System.out.println("no val");
		}
		
		//두번째 예외처리 방법. 디폴트 값을 설정한다
		double avg = Arrays.stream(new int[] {1,2,3,4,5})
					.filter(n -> n%2 == 0)
					.average()
					.orElse(0.0);
		//세번째 예외처리 방법 값이 있을때만 출력한다.
		Arrays.stream(new int[] {1,2,3,4,5})
					.filter(n -> n%2 == 0)
					.average()
					.ifPresent(a -> System.out.println(a));
				
		
	}
}

package com.kangong.common.taglib;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomFunction {
	
	public static String filter(String str) {
	 // return str.replaceAll("\"", "\\\"").replaceAll("\r\n", "");
		return str+" ktw 만세!!";
	}
	
	public static String getTest(String txt){
        return txt + "님";
    }
	
	public static String getIndex(String startIdx, String range, String idx) throws Exception{
		/*
		try{
			Integer.parseInt(startIdx);
			Integer.parseInt(range);
			Integer.parseInt(idx);
			System.out.println("startIdx: "+startIdx);
			System.out.println("range: "+range);
			System.out.println("idx: "+idx);
		}catch(NumberFormatException n) {
			System.out.println("startIdx: "+startIdx);
			System.out.println("range: "+range);
			System.out.println("idx: "+idx);
			
			startIdx = "1";
			range = "10";
			idx = "1";
		}
		*/
		
		log.debug("startIdx: {}, range: {}, idx: {}", startIdx, range, idx);
		
		int resultIdx = ( Integer.parseInt(startIdx) - 1 ) * Integer.parseInt(range) + Integer.parseInt(idx);
		
        return Integer.toString(resultIdx);
    }

}

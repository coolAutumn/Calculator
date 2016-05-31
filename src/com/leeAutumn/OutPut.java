package com.leeAutumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leeAutumn.TokenAnalyser.idenType;

public class OutPut {
	//等待被输出的变量
    private List<String> waitToBeWritten;
    //所有变量的信息
    private Map<String,Variable> symbolTable;
    
    public OutPut(List<String> toBeWriiten,Map<String,Variable> symbolTab){
    	waitToBeWritten = new ArrayList<>();
    	symbolTable = new HashMap<>();
    	
    	//将参数的值传递进output的对应属性里面
    	symbolTable.putAll(symbolTab);
    	waitToBeWritten.addAll(toBeWriiten);
    }
    
    public void outPut(){
    	System.out.println("**************结果输出面板**************");
    	for(String s:waitToBeWritten){
    		System.out.print(s+"=");
    		int i = (int) symbolTable.get(s).value;
    		float f = (float)symbolTable.get(s).value;
    		if(symbolTable.get(s).type.equals(idenType.FLOAT)){
    			System.out.println(f);
    		}else{
    			System.out.println(i);
    		}
    	}	
    	System.out.println("***************************************");
    }
}

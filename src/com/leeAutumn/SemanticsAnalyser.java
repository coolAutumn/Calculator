package com.leeAutumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SemanticsAnalyser {
	//记录被声明的变量
	private ArrayList<String> isDeclared;
	
	//检测某变量是否存在isDeclared中
	private boolean isDeclared(String s){
		boolean flag = false;
		for(String s1:isDeclared){
			if(s1.equals(s.trim())){
				flag = true;
			}
		}
		return flag;
	}
	
    public SemanticsAnalyser(){
    	isDeclared = new ArrayList<>();
    }
    
	public boolean isRightVariableDeclared(String expr){
		if(expr.contains("=")){
			ArrayList<String> infix = Caculator.StringToInfix(expr.split("=")[1]);
			for(String s:infix){
				if(Caculator.isVariable(s.trim())){
					if(!isDeclared(s)){
						return false;
					}
				}
			}
		}else if(expr.contains("float")||expr.contains("int")){
			String[] exprs = expr.trim().replaceAll("\\s{1,}", " ").split(" ");
			isDeclared.add(new String(exprs[1]));
		}
		return true;
	}
	
	public void analyse(String expression){
		String[] exprs =expression.split(";");
		int cur=0; //记录检测的语句排序号
		for(String ex:exprs){
			cur++;
			System.out.println("语义分析器正在分析第"+cur+"句语句:"+ex);
			if(!isRightVariableDeclared(ex)){
				System.out.println("第"+cur+"句语句中有变量未被声明即被使用");
				System.exit(-1);
			}else{
				System.out.println("没有出现错误，通过！");
			}
		}
	}
}

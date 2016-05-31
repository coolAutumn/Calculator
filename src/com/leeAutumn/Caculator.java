package com.leeAutumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class Caculator {
	//符号表（存所有变量的信息）
    public Map<String,Variable> symbolTable;
    
    public Caculator(){
    	symbolTable = new HashMap<>();
    }
    
	//检测是否为变量
	public static boolean isVariable(String st){
		if(st.matches("[a-zA-Z]+[0-9]{0,}")){
			return true;
		}else{
			return false;
		}
	}
	
	//检测是否为数字
	public static boolean isNumber(String st){
		if(st.matches("[1-9]+")||st.matches("[1-9][0-9]+")||st.matches("[0-9].[0-9]+")||st.matches("[1-9]+[0-9]*.[0-9]+")){
			return true;
		}else{
			return false;
		}
	}
	
	//计算各种符号的优先级
	private int getPriority(String c){
		if(c.equals("+") || c.equals("-")){
			return 1;
		}else if(c.equals("*") || c.equals("/")){
			return 2;
		}else if(c.equals("(")){
			return 3;
		}else{
			return 0;
		}
	}
		
	//检测某字符是否为操作符
  	public static boolean opCheck(String te){
  		if(te.equals("+")||te.equals("-")||te.equals("*")||te.equals("/")||
  				te.equals("(")||te.equals(")")||te.equals(";")||te.equals("?")){
  			return true;
  		}else{
  			return false;
  		}
  	}
    
    //将String类型的中缀表达式转换成ArrayList类型的中缀表达式
    public static ArrayList<String> StringToInfix(String expression){
    	ArrayList<String> infixExpression = new ArrayList<>();
    	String mid = "";   //用作处理拆分表达式的中间符号或者变量或者操作数
		char[] strs = expression.toCharArray();
		for(char c: strs){
			String ci = String.valueOf(c);
			if(!ci.equals(" ")&&!ci.equals("")){
				if(opCheck(ci)||ci.trim().equals("=")){
					if(!mid.equals("")){
						infixExpression.add(new String(mid));
						mid = "";
					}
					mid += c;
					infixExpression.add(new String(mid));
					mid = "";
				}else{
					mid += c;
				}
			}
		}
		infixExpression.add(new String(mid));
		infixExpression.add(new String(";"));
		return infixExpression;
    }
    
  //将中缀表达式转换成后缀表达式
  	private ArrayList<String> infixToSuffix(ArrayList<String> infixExpression){
  		ArrayList<String> suffixExpression = new ArrayList<>();   //存放后缀表达式
  		Stack<String> opStack = new Stack<>();   //存放操作符的栈

  		infixExpression.add(new String(";"));
  		Iterator<String> it = infixExpression.iterator();
  		while(it.hasNext()){
  			String lit = it.next();
  			if(opCheck(lit)){
  				if(opStack.empty()){
  					opStack.push(new String(lit));
  				}else if(lit.equals(";")){
  					while(!opStack.empty()){
  						suffixExpression.add(new String(opStack.pop()));
  					}
  					suffixExpression.add(";");
  				}else if(lit.equals(")")){
  					while(!(opStack.peek().equals("("))){
  						suffixExpression.add(new String(opStack.pop()));
  					}
  					opStack.pop();
  				}else{
  					if(opStack.peek().trim().equals("(")){
  						opStack.push(new String(lit));
  					}else{
  						while(!(opStack.empty())&&!(opStack.peek().trim().equals("("))&&getPriority(lit)<=getPriority(opStack.peek())){
  							suffixExpression.add(new String(opStack.pop()));
  						}
  						opStack.push(new String(lit));
  					}
  				}
  			}else{
  				suffixExpression.add(new String(lit));
  			}
  		}
  		return suffixExpression;
  	}
 
	//计算两个数的四则运算
	private double calculateValue(String type,double a,double b){
		if("+".equals(type)){
			return a+b;
		}else if("-".equals(type)){
			return b-a;
		}else if("*".equals(type)){
			return a*b;
		}else if("/".equals(type)){
			if(0==a || 0==b){
				return 0;
			}else{
				return b/a;
			}
		}
		return 0;		
	}
	
	//计算后缀表达式的值。
	private double calculateSuffixExpression(ArrayList<String> suffixExpression){
		double value = 0.0;
		boolean flag=false;   //检测
		for(String s:suffixExpression){
			if(opCheck(s) && !s.trim().equals(";")){
				flag = true;
				break;
			}
		}
		//如果表达式中只有变量或者数字
		if(!flag){
			for(String s:suffixExpression){
				if(isVariable(s))
					return symbolTable.get(s.trim()).getValue();
				else if(isNumber(s))
					return Double.parseDouble(s.trim());
			}
		}
		Stack<String> stack = new Stack<>();
		Iterator<String> it = suffixExpression.iterator();
		while(it.hasNext()){
			String lit = it.next().trim();
			if(!lit.equals("")){
				if(!opCheck(lit)){
					stack.push(new String(lit));
				}else if(lit.equals(";")){
					value = Double.parseDouble(stack.pop().trim());
				}else{
					stack.push(String.valueOf(calculateValue(
							lit,stack.peek().matches("[a-zA-Z]+[0-9]{0,}")?symbolTable.get(stack.pop().trim()).getValue():Double.parseDouble(stack.pop().trim()),
							stack.peek().matches("[a-zA-Z]+[0-9]{0,}")?symbolTable.get(stack.pop().trim()).getValue():Double.parseDouble(stack.pop().trim()))));
				}
			}
		}
		return value;
	}
	
	//运算等号分隔开后的右边表达式
	public double Caculate(String expr){
		return calculateSuffixExpression(infixToSuffix(StringToInfix(expr)));
	}
	
	//将表达式以等号为分隔符拆分
	private String[] expressionDivision(String expression){
		return expression.split("=");
	}
	
	//将指定symboltable导入
	public void importSymbolTable(Map<String,Variable> symbolTab){
		this.symbolTable.putAll(symbolTab);
	}
	
	public void execute(String expr){
		String[] expressions = expr.split(";");
		for(String ex:expressions){
			if(ex.contains("=")){
				String[] exs = ex.split("=");
				symbolTable.get(exs[0].trim()).setValue(Caculate(exs[1].trim()));
			}
		}
	}
}

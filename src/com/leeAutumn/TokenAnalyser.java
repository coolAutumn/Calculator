package com.leeAutumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenAnalyser {
    //DFA的状态转化表
    public List<State> stateTransformTable;
    //符号表
    public Map<String,Variable> symbolTable;
    //每一句的token集合
    public List<List<String>> tokenList;
    //identifier的类型
    public enum idenType{
        INT,
        FLOAT
    };
    //所遇到的char的类型(移进字符的类型)
    public enum typeOfChar{
        LETTER,
        NUMBER,
        ZERO,
        DOT,
        EQUAL,
        SEMICOLON,
        BLANK,
        OTHER,   //指那些运算符
        NONE
    }

    public TokenAnalyser(){
        symbolTable=new HashMap<>();
        tokenList=new ArrayList<>();
        stateTransformTable=new ArrayList<>();
        initialTransformTable();
    }

    //初始化DFA的转化图
    public void initialTransformTable(){
        //0状态(初始状态)
        stateTransformTable.add(new State(0,new typeOfChar[]{typeOfChar.LETTER},new int[]{1}));
        //1状态,还未遇到等号,包含了变量声明部分
        stateTransformTable.add(new State(1,new typeOfChar[]{typeOfChar.LETTER,typeOfChar.NUMBER,typeOfChar.BLANK,typeOfChar.SEMICOLON},new int[]{1,1,2}));
        //2状态,遇到了等号
        stateTransformTable.add(new State(2,new typeOfChar[]{typeOfChar.BLANK,typeOfChar.EQUAL},new int[]{2,3}));
        //3状态
        stateTransformTable.add(new State(3,new typeOfChar[]{typeOfChar.LETTER,typeOfChar.NUMBER},new int[]{7,4}));
        //4状态
        stateTransformTable.add(new State(4,new typeOfChar[]{typeOfChar.ZERO,typeOfChar.NUMBER,typeOfChar.DOT,typeOfChar.OTHER,typeOfChar.SEMICOLON},new int[]{4,4,5,7,10}));
        //5状态(xx.xx)
        stateTransformTable.add(new State(5,new typeOfChar[]{typeOfChar.NUMBER,typeOfChar.ZERO},new int[]{6,6}));
        //暂定10是终态
    }

    //获取移进字符的类型
    public typeOfChar getTypeOfChar(char c){
        if((c>='A'&&c<='Z')||(c>='a'&&c<='z')){
            return typeOfChar.LETTER;
        }else if(c>='1'&&c<='9'){
            return typeOfChar.NUMBER;
        }else if(c=='0'){
            return typeOfChar.ZERO;
        }else if(c=='.'){
            return typeOfChar.DOT;
        }else if(c=='='){
            return typeOfChar.EQUAL;
        }else if(c==';'){
            return typeOfChar.SEMICOLON;
        } else if(c==' '){
            return typeOfChar.BLANK;
        } else if(c=='+'||c=='-'||c=='*'||c=='/'||c=='('||c==')'){
            return typeOfChar.OTHER;
        }else {
            return typeOfChar.NONE;
        }
    }

    public void Analyser(String text){
        String[] lines=text.trim().split("\n");
        int lineNumber=0;
        for (String line:lines) {
            for(String expr:line.split(";")) {
                char[] words = expr.toCharArray();

            }
            lineNumber++;
        }
    }

    class Variable{
        String name="";
        idenType type=idenType.INT;
        double value=0;
    }

    class State{
        int stateNumber;    //当前state的状态号
        Map<typeOfChar,Integer> transformMap;

        public State(int stateNumber,typeOfChar[] types,int[] targetState){
            this.stateNumber=stateNumber;
            this.transformMap=new HashMap<>();
            if(types.length!=targetState.length){
                System.out.println("状态图转化两参数长度不一致.");
                System.exit(0);
            }
            int i=0;
            for(typeOfChar type:types){
                transformMap.put(type,targetState[i]);
                i++;
            }
        }
    }
}

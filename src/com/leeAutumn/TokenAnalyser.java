package com.leeAutumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenAnalyser {
    //DFA的状态转化表
    public List<State> stateDefinitionTransformTable;
    public List<State> stateExpressionTransformTable;
    //符号表
    public Map<String,Variable> symbolTable;
    //每一句的token集合
    public List<List<String>> tokenList;
    //等待被输出的变量
    public List<String> waitToBeWritten;
    //identifier的类型
    public enum idenType{
        INT,
        FLOAT
    };
    int lineNumber=1;
    String[] lines;
    boolean allOver=false;
    String returnDefination;
    String returnExperssion;

    //所遇到的char的类型(移进字符的类型)
    public enum typeOfChar{
        LETTER,
        NUMBER,
        ZERO,
        DOT,
        EQUAL,
        SEMICOLON,
        BLANK,
        KUOHAO,
        OTHER,   //指那些运算符
        NONE
    }

    public TokenAnalyser(){
        symbolTable=new HashMap<>();
        tokenList=new ArrayList<>();
        stateDefinitionTransformTable =new ArrayList<>();
        stateExpressionTransformTable =new ArrayList<>();
        waitToBeWritten=new ArrayList<>();
        initialTransformTable();
    }

    //初始化DFA的转化图
    public void initialTransformTable(){
        //0状态(初始状态)
        stateDefinitionTransformTable.add(new State(0,new typeOfChar[]{typeOfChar.LETTER,typeOfChar.BLANK},new int[]{1,0}));
        //1状态,开始出现字符
        stateDefinitionTransformTable.add(new State(1,new typeOfChar[]{typeOfChar.LETTER,typeOfChar.NUMBER,typeOfChar.BLANK},new int[]{1,1,2}));
        //2状态,开始声明变量名
        stateDefinitionTransformTable.add(new State(2,new typeOfChar[]{typeOfChar.BLANK,typeOfChar.LETTER},new int[]{2,3}));
        //3状态,正在声明变量名
        stateDefinitionTransformTable.add(new State(3,new typeOfChar[]{typeOfChar.LETTER,typeOfChar.NUMBER,typeOfChar.ZERO,typeOfChar.SEMICOLON,typeOfChar.BLANK},new int[]{3,3,3,4,5}));
        stateDefinitionTransformTable.add(new State(5,new typeOfChar[]{typeOfChar.BLANK,typeOfChar.SEMICOLON},new int[]{5,4}));
        //4状态,终态
        stateDefinitionTransformTable.add(new State(4,new typeOfChar[]{},new int[]{}));


        //0状态(初始状态)
        stateExpressionTransformTable.add(new State(0,new typeOfChar[]{typeOfChar.BLANK,typeOfChar.LETTER},new int[]{0,1}));
        stateExpressionTransformTable.add(new State(1,new typeOfChar[]{typeOfChar.NUMBER,typeOfChar.LETTER,typeOfChar.ZERO,typeOfChar.BLANK,typeOfChar.EQUAL},new int[]{1,1,1,2,3}));
        stateExpressionTransformTable.add(new State(2,new typeOfChar[]{typeOfChar.BLANK,typeOfChar.EQUAL},new int[]{2,3}));
        stateExpressionTransformTable.add(new State(3,new typeOfChar[]{typeOfChar.BLANK,typeOfChar.LETTER,typeOfChar.NUMBER,typeOfChar.KUOHAO,typeOfChar.OTHER},new int[]{3,10,4,12,8}));
        stateExpressionTransformTable.add(new State(4,new typeOfChar[]{typeOfChar.NUMBER,typeOfChar.ZERO,typeOfChar.DOT,typeOfChar.OTHER,typeOfChar.BLANK,typeOfChar.SEMICOLON,typeOfChar.KUOHAO},new int[]{4,4,5,8,7,9,12}));
        stateExpressionTransformTable.add(new State(5,new typeOfChar[]{typeOfChar.NUMBER,typeOfChar.ZERO},new int[]{6,6}));
        stateExpressionTransformTable.add(new State(6,new typeOfChar[]{typeOfChar.NUMBER,typeOfChar.ZERO,typeOfChar.BLANK,typeOfChar.OTHER},new int[]{6,6,7,8}));
        stateExpressionTransformTable.add(new State(7,new typeOfChar[]{typeOfChar.BLANK,typeOfChar.OTHER,typeOfChar.SEMICOLON},new int[]{7,8,9}));
        stateExpressionTransformTable.add(new State(8,new typeOfChar[]{typeOfChar.BLANK,typeOfChar.LETTER,typeOfChar.NUMBER,typeOfChar.SEMICOLON,typeOfChar.KUOHAO},new int[]{8,10,4,9,12}));
        //终态
        stateExpressionTransformTable.add(new State(9,new typeOfChar[]{},new int[]{}));
        stateExpressionTransformTable.add(new State(10,new typeOfChar[]{typeOfChar.BLANK,typeOfChar.LETTER,typeOfChar.NUMBER,typeOfChar.ZERO,typeOfChar.SEMICOLON,typeOfChar.KUOHAO},new int[]{11,10,10,10,9,12}));
        stateExpressionTransformTable.add(new State(11,new typeOfChar[]{typeOfChar.BLANK,typeOfChar.OTHER},new int[]{11,8}));
        stateExpressionTransformTable.add(new State(12,new typeOfChar[]{typeOfChar.KUOHAO,typeOfChar.NUMBER,typeOfChar.LETTER,typeOfChar.SEMICOLON,typeOfChar.OTHER},new int[]{12,4,10,9,8}));
    }

    //获取移进字符的类型
    public typeOfChar getTypeOfChar(char c){
        if((c>='A'&&c<='Z')||(c>='a'&&c<='z')){
            return typeOfChar.LETTER;
        }else if((c>='1'&&c<='9')){
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

        } else if(c=='('||c==')'){
            return typeOfChar.KUOHAO;
        } else if(c=='+'||c=='-'||c=='*'||c=='/'||c=='+'||c=='-'){
            return typeOfChar.OTHER;
        }else {
            return typeOfChar.NONE;
        }
    }

    //整合结果判断过程,防止Anlyser函数过度复杂
    public boolean isDefineVar_Father(char[] arr){
        int res=isDefineVar(arr);
        String temp=null;
        if(res==-1){
            return false;
        }else if(res==99){
            temp=returnDefination.trim();
            temp=temp.replaceAll(";","");
            temp=temp.replaceAll("^float ","");
            temp=temp.replaceAll(" ","");
            System.out.println("声明的float变量名为:"+temp);
            symbolTable.put(temp,new Variable(temp));
        }else if(res==98){
            temp=returnDefination.trim();
            temp=temp.replaceAll(";","");
            temp=temp.replaceAll("^int ","");
            temp=temp.replaceAll(" ","");
            System.out.println("声明的int变量名为:"+temp);
            symbolTable.put(temp,new Variable(temp));
        }else if(res==1){
            System.out.println("第"+lineNumber+"行声明语句有问题");
            System.exit(0);
        }
        return true;
    }
    //判断是否为声明变量的语句的词法分析器
    //返回结果声明:
    //1     :   左侧变量未定义
    //99    :   发现了float
    //98    :   发现了int
    public int isDefineVar(char[] arr){
        int result=0;
        int lastState=-1;
        int currentState=0;
        int length=arr.length;
        int i;
        for(i=0;i<length;i++){

            //当遇到了'.'
            if(arr[i]=='.'&&currentState!=2){
                allOver=true;
                break;
            }

            typeOfChar t=getTypeOfChar(arr[i]);
            Integer integer= stateDefinitionTransformTable.get(currentState).transformMap.get(t);

            if(integer!=null){
                lastState=currentState;
                currentState=integer;
                //判断是否有float或者int
                if(lastState == 1 && currentState == 2){
                    StringBuffer sb=new StringBuffer("");
                    for(int j=0;j<=i;j++){
                        sb.append(arr[j]);
                    }
                    if(sb.toString().contains("float")){
                        result=99;
                    }
                    if(sb.toString().contains("int")){
                        result=98;
                    }
                }

            }else{
                //表示不是变量声明语句
                if(result!=99&&result!=98){
                    result=-1;
                }
                break;
            }
        }

        //如果到达了终态
        if(currentState!=4&&result!=99&&result!=98){
            result=-1;
        }
        //提前结束但是后面变量名不满足规格
        if(i!=length&&(result==99||result==98)&&!allOver){
            result=1;
        }
        StringBuffer sb=new StringBuffer("");
        for(int j=0;j<i;j++){
            sb.append(arr[j]);
        }
        returnDefination=sb.toString();
        return result;
    }

    public boolean isExpression_Father(char[] arr){
        boolean result=true;
        int res=isExpression(arr);
        if(res==-1){
            return false;
        }else{
            if(res==1){
            }
            if(res==2){
            }
            if(res==3){
                System.out.println("第"+lineNumber+"表达式中的变量名不符合规则");
                result=false;
            }
        }
        return result;
    }

    //返回变量结果说明
    //1     :   结果正确
    //2     :   左侧变量未定义
    //3     :   变量名不符合规则
    public int isExpression(char[] arr){
        List<String> list=new ArrayList<>();
        int start=0;
        int result=0;
        int lastState=-1;
        int currentState=0;
        int length=arr.length;
        int i;
        for(i=0;i<length;i++){

//            if(arr[i]=='.'&&currentState!=4&&i==length-1){
//                allOver=true;
//                break;
//            }
            if(allOver){
                break;
            }
            if(arr[i]=='.'&&i==length-1&&currentState==4){
                allOver=true;
                currentState=9;
//                i--;
            }
            typeOfChar t=getTypeOfChar(arr[i]);
            Integer integer= stateExpressionTransformTable.get(currentState).transformMap.get(t);

            if(integer!=null&&!allOver){
                lastState=currentState;
                currentState=integer;
//                //当'='前面出现了的变量名
//                if((lastState == 1 && currentState == 2)||(lastState==1 && currentState ==3)){
//                    StringBuffer sb=new StringBuffer("");
//                    for(int j=0;j<i;j++){
//                        if(arr[j]!=' ')
//                            sb.append(arr[j]);
//                    }
//                    if(symbolTable.get(sb.toString())==null){
//                        System.out.println("第"+lineNumber+"行左侧变量"+sb.toString()+"未定义");
//                        result=2;
//                        return result;
//                    }else{
//                        list.add(sb.toString());
//                    }
//                    start=i+1;
//                }
                if((lastState==3&&currentState==4)||(lastState==3&&currentState==10)){
                    start=i;
                }
                if(t.equals(typeOfChar.EQUAL)){
                    StringBuffer sb=new StringBuffer("");
                    for(int j=0;j<i;j++){
                        if(arr[j]!=' ')
                            sb.append(arr[j]);
                    }
                    if(symbolTable.get(sb.toString())==null){
                        System.out.println("第"+lineNumber+"行左侧变量"+sb.toString()+"未定义");
                        result=2;
                        return result;
                    }else{
                        list.add(sb.toString());
                        list.add("=");
                    }
                    start=i+1;
                }
                //如果遇到了括号
                if(currentState==12){
                    StringBuffer sb=new StringBuffer("");
                    for(int j=start;j<i;j++){
                        sb.append(arr[j]);
                    }
                    if(!sb.toString().equals(""))
                        list.add(sb.toString().trim());
                    list.add(""+arr[i]);
                    start=i+1;
                }
                //如果遇到了OTHER
//                if((lastState==4||lastState==6||lastState==7||(lastState==8&&currentState!=8)||lastState==10||lastState==11)&&(currentState==8||currentState==9)){
                if(t.equals(typeOfChar.OTHER)){
                    StringBuffer sb=new StringBuffer("");
                    for(int j=start;j<i;j++){
                        sb.append(arr[j]);
                    }
                    if(!sb.toString().equals(""))
                        list.add(sb.toString().trim());
                    list.add(""+arr[i]);
                    start=i+1;
                }
                //如果遇到了分号,代表着一句话结束了
                if(t.equals(typeOfChar.SEMICOLON)){
                    StringBuffer sb=new StringBuffer("");
                    for(int j=start;j<i;j++){
                        sb.append(arr[j]);
                    }
                    if(!sb.toString().equals(""))
                        list.add(sb.toString().trim());
                    start=i+1;
                }
            }else if(!allOver){
                break;
            }
        }
        if(allOver){
            StringBuffer sb=new StringBuffer("");
            for(int j=start;j<i;j++){
                sb.append(arr[j]);
            }
            list.add(sb.toString());
        }
        if(currentState==9) {
            result = 1;
            list.add(";");
            tokenList.add(list);
        }else{
            result=-1;
        }

        if(i!=length&&currentState==7&&!allOver){
            result=3;
        }
        return result;
    }

    public void Analyser(String text){
        lines=text.trim().split("\n");
        boolean resultOfDefinition=false;
        int i=0;
        for (String line:lines) {
            for(String expr:line.split(";")) {
                if(i!=lines.length-1)
                    expr = expr + ";";
                i++;
                System.out.println("词法分析器正在分析第" + lineNumber + "行:"+expr);

                char[] words = expr.toCharArray();
                //是否已被'.'结束
                if (allOver) {
                    break;
                }
                if (expr.matches("^write\\((([a-z]|[A-Z])(([0-9]|[A-Z]|[a-z])*))\\)(\\.|;)")) {
                    if (expr.replaceFirst("write\\(","").replace(").","").matches("([a-z]|[A-Z])([0-9]|[A-Z]|[a-z])*")) {
                        waitToBeWritten.add(expr.replaceFirst("write\\(","").replace(").",""));
                    }else{
                        System.out.println("第"+lineNumber+"行"+expr+"是什么鬼");
                    }
                }else
                {
                    if (!isDefineVar_Father(words)) {
                        if (!isExpression_Father(expr.trim().toCharArray())) {
                            //什么语句都不是
                            System.out.println("第"+lineNumber+"行什么语句都不是");
                        }
                    }
                }
            }
            lineNumber++;
        }
        System.out.println("需要被输出的变量有"+waitToBeWritten);
        System.out.println("需要被输出的token有"+tokenList);
    }


    class Variable{

        public void setName(String name) {
            this.name = name;
        }

        public void setType(idenType type) {
            this.type = type;
        }

        public void setValue(double value) {
            this.value = value;
        }

        String name="";
        idenType type=idenType.INT;

        public void setSet(boolean set) {
            isSet = set;
        }

        boolean isSet=false;
        double value=0;

        public Variable(String name){
            this.name=name;
        }
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

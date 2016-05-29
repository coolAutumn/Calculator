package com.leeAutumn;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;

public class Main {

    private File file;
    private FileInputStream fin;
    private Reader reader;
    public String str_get;

    public void ReadExprFromFile()
    {
        //从当前路径下读取名为expression.txt的文本文件
        file=new File("expr.txt");
        try {
            fin = new FileInputStream(file);
            str_get="";
        }catch (Exception e)
        {
            System.out.println("expr.txt not found");
            e.printStackTrace();
        }
    }

    /**
     * 用于从文件中取得表达式并且清除换行符
     * @return
     */
    public void getExpr ()
    {
        byte[] bytes=new byte[16];
        int hasread=0;
        int offset=0;
        try {
            while ((hasread = fin.read(bytes)) != -1) {
                str_get += new String(bytes, 0, hasread);
                offset += hasread;
            }
        }catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
	// write your code here
        Main m=new Main();
        m.ReadExprFromFile();
        m.getExpr();
        TokenAnalyser tokenAnalyser=new TokenAnalyser();
        tokenAnalyser.Analyser(m.str_get);
    }
}

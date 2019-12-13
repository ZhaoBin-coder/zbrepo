package com.benwu.com.excute;

import java.io.IOException;

public class RunSpider {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        long start = System.currentTimeMillis();
         Spider jdSpider = new Spider();
        jdSpider.start();
        long end = System.currentTimeMillis();
        System.out.println("100页抓取完毕并保存至数据库用时:" + ((double)(end-start))/1000.00 + "s");
    }

}

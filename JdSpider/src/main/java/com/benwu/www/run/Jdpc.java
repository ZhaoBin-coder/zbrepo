package com.benwu.www.run;

import com.benwu.www.dao.ProductDao;

import com.benwu.www.pojo.Product;
import com.benwu.www.utils.HttpClientUtils;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Jdpc {
    private static BlockingQueue<String> queue=new ArrayBlockingQueue<String>(1000);

    private static  ExecutorService executorService = Executors.newFixedThreadPool(50);

    private static ProductDao productDao=new ProductDao();



    public static void main(String[] args) throws Exception {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int size = queue.size();
                    System.out.println("当前队列中有"+size+"个pid");

                }
            }
        });

        for (int i = 0; i < 30; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        String pid = null;
                        try {
                            pid = queue.take();
                            Product product = parsePid(pid);
                            productDao.addProduct(product);
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                queue.put(pid);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }

                    }
                }
            });
        }

        page();

    }
    private static void page() throws Exception {
        for (int i = 1; i <=100 ; i++) {
            String url="https://gkcx.eol.cn/school/179/newsdetail/68002/165010";
            String html = HttpClientUtils.doGet(url);
            parseIndex(html);
        }
    }
    private static void parseIndex(String html) throws InterruptedException {
        Document document = Jsoup.parse(html);
        Elements liEl = document.select("[class=gl-warp clearfix]>li");
        for (Element li : liEl) {
            queue.put(li.attr("data-sku"));
        }
    }
    private static Product parsePid(String pid) throws Exception {
        String url="https://item.jd.com/"+pid+".html";
        String html = HttpClientUtils.doGet(url);
        Document document = Jsoup.parse(html);

        Product product = new Product();

        product.setPid(pid);

        Elements titleEl = document.select("[class=sku-name]");
        product.setTitle(titleEl.text());

        Elements brandEl = document.select("#parameter-brand>li");
        product.setBrand(brandEl.attr("title"));

        Elements pnameEl = document.select("[class=parameter2 p-parameter-list]>li:first-child");
        product.setPname(pnameEl.attr("title"));

        String productUrl="https://p.3.cn/prices/mgets?pduid="+Math.random()+"&skuIds=J_"+pid;
        String json = HttpClientUtils.doGet(productUrl);
        Gson gson = new Gson();
        List<Map<String,String>> list = gson.fromJson(json, List.class);
        String price = list.get(0).get("p");

        product.setPrice(price);

        return product;
    }
}
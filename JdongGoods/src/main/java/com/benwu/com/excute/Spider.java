package com.benwu.com.excute;



import com.benwu.com.pojo.Phone;
import com.benwu.com.utils.JdbcUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Spider {

    //线程池
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    //阻塞队列,用于存放商品盒子li
    private static final BlockingQueue<Element> queueLi = new ArrayBlockingQueue<Element>(100);
    //阻塞队列,用于存放phone
    private static final BlockingQueue<Phone> queuePhone = new ArrayBlockingQueue<Phone>(100);
    //爬取的首页
    private String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&wq=%E6%89%8B%E6%9C%BA&pvid=5b2751339d874f89b1a53a0b7eb6a55c";

    //开始爬取
    public void start() throws IOException {

        final String sql = "insert into phone(id,name,price,shop) values(?,?,?,?)";
        //创建10个消费者,消费phone队列并向数据库中插入商品信息
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                public void run() {
                    QueryRunner queryRunner = new QueryRunner(JdbcUtils.getDataSource());
                    while (true) {
                        try {
                            Phone phone = queuePhone.take();
                            queryRunner.update(sql,phone.getId(),phone.getName(),phone.getPrice(),phone.getShop());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        //创建10个消费者(解析队列中存放的li)
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                public void run() {
                    //从队列中取出li进行解析
                    while (true) {
                        Element li = null;
                        try {
                            li = queueLi.take();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Phone phone = parseLi(li);
                        if (phone != null) {
                            queuePhone.offer(phone);
                        }
                    }
                }
            });
        }
        //获取首页
        CloseableHttpResponse indexRes = sendGet(url);
        //解析结果
        parseIndex(indexRes, 1);

    }

    //发送get请求,获取响应结果
    public CloseableHttpResponse sendGet(String url) throws IOException {

        //创建httpClient客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建请求对象,发送请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3514.0 Safari/537.36");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return response;

    }

    //解析首页
    public void parseIndex(CloseableHttpResponse indexRes, int page) throws IOException {

        System.out.println("---第" + page + "页抓取完毕---");
        //得到document对象
        String indexHtml = EntityUtils.toString(indexRes.getEntity(), "UTF-8");
        //System.out.println(indexHtml);
        Document document = Jsoup.parse(indexHtml);
        //获取所有商品盒子(li.gl-item)
        Elements lis = document.select("li[class=clearfix]");
        //取出每个盒子置于队列中
        for (Element li : lis) {
            queueLi.offer(li);
        }
        if (++page <= 100) {
            int index = 2 * page - 1;
            String url = "https://search.jd.com/Search?keyword=手机&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=手机&cid2=653&cid3=655&page=" + index + "&click=0";
            CloseableHttpResponse nextRes = sendGet(url);
            parseIndex(nextRes, page);
        }

    }
    //解析每个盒子,封装到phone并返回
    public Phone parseLi(Element li) {

        try {
            Phone phone = new Phone();
            String id = li.attr("data-sku");
            String name = li.select("div.p-name em").get(0).text();
            String price = li.select("div.p-price i").get(0).text();
            String shop = li.select("div.p-shop a").attr("title");
            phone.setId(id);
            phone.setName(name);
            phone.setPrice(price);
            phone.setShop(shop);
            return phone;
        } catch (Exception e) {
            //System.out.println("错误数据");
        }
        return null;

    }

}

package run;




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
import pojo.University;
import utils.JdbcUtils;

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
    //阻塞队列,用于存放university
    private static final BlockingQueue<University> queuePhone = new ArrayBlockingQueue<University>(100);
    //爬取的首页
    private String url ="https://www.youzy.cn/tzy/search/colleges/collegeList?page=1";

    //开始爬取
    public void start() throws IOException {

        final String sql = "insert into uninfo(cid,url,name) values(?,?,?)";
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                public void run() {
                    QueryRunner queryRunner = new QueryRunner(JdbcUtils.getDataSource());
                    while (true) {
                        try {
                            University university = queuePhone.take();
                            queryRunner.update(sql,university.getCid(),university.getUrl(),university.getName());
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
                        University university = parseLi(li);
                        if (university != null) {
                            queuePhone.offer(university);
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
        httpGet.setHeader("Cookie","Youzy2CCurrentProvince=%7B%22provinceId%22%3A%22854%22%2C%22provinceName%22%3A%22%E9%87%8D%E5%BA%86%22%2C%22isGaokaoVersion%22%3Afalse%7D; Max-Age=604800; Path=/; Expires=Tue, 17 Dec 2019 01:57:30 GMT");
        CloseableHttpResponse response = httpClient.execute(httpGet);

        return response;

    }

    //解析首页
    public void parseIndex(CloseableHttpResponse indexRes, int page) throws IOException {

        System.out.println("---第" + page + "页抓取完毕---");
        //得到document对象
        String indexHtml = EntityUtils.toString(indexRes.getEntity(), "UTF-8");
        Document document = Jsoup.parse(indexHtml);
        Elements lis = document.select("li[class=clearfix]");
        //取出每个盒子置于队列中
        for (Element li : lis) {
            queueLi.offer(li);
        }
        if (++page <= 145) {
            int index = 1+page-1;
            String url ="https://www.youzy.cn/tzy/search/colleges/collegeList?page=" + index ;
            CloseableHttpResponse nextRes = sendGet(url);
            parseIndex(nextRes, page);
        }
    }
    //解析每个盒子,封装到phone并返回
    public University parseLi(Element li) {
        try {
            University university = new University();
            String url ="https://www.youzy.cn"+li.select("a").attr("href");
            String name = li.select("a").text();
          //  System.out.println(url);
            int start=url.indexOf("=");
            int end=url.length();
            String cid=url.substring(start+1,end);
            university.setName(name);
            university.setUrl(url);
            university.setCid(cid);

            return university;
        } catch (Exception e) {
            //System.out.println("错误数据");
        }
        return null;

    }

}

package run;

import com.google.gson.Gson;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.Artical;
import pojo.Two;
import pojo.UniversityInfo;
import utils.HttpClientUtils;
import utils.JdbcUtils;
import utils.MysqlUtils;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SpiderUniversity {
    int x=0;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final BlockingQueue<UniversityInfo> queuePhone = new ArrayBlockingQueue<UniversityInfo>(100);
    public void start() throws IOException, SQLException, PropertyVetoException {

      //  final String sql = "insert into universityinfo(cid,year,batch,up,low,avg,difvalue,lowlevel) values(?,?,?,?,?,?,?,?)";
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                public void run() {
            //        QueryRunner queryRunner = new QueryRunner(JdbcUtils.getDataSource());
                    while (true) {
                        try {
                            UniversityInfo university = queuePhone.take();
          //                  queryRunner.update(sql,university.getCid(),university.getBatch(),university.getUp(),university.getLow(),university.getAvg(),university.getDifvalue(),university.getLowlevel());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        String sql1 = "select url from ur";
        QueryRunner queryRunner = new QueryRunner(JdbcUtils.getDataSource());
        List<Map<String, Object>> list = queryRunner.query(sql1, new MapListHandler());
        List<Artical> universityList = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : list) {

            String res = stringObjectMap.toString();
            int start = res.indexOf("=");
            int end=res.indexOf("}");
            String url = res.substring(start + 1, end);
            int cidStart=url.indexOf("?");
            int cidEnd=url.indexOf("&");

            String cid=url.substring(cidStart+1,cidEnd);
            String html = HttpClientUtils.doGet(url);
            Document document = Jsoup.parse(html);
            Element first = document.getElementsByClass("content").first();

        if(first!=null) {
           String result = first.toString();
            Artical art=new Artical();
            art.setCid(cid.substring(4,cid.length()));
            art.setCatalog(result);
            universityList.add(art);
            System.out.println(cid);
            System.out.println(result.length());
         }
        }
       MysqlUtils.SaveData(universityList);
    }


    public void parseJson(String json,String result) throws IOException, SQLException, PropertyVetoException {
        List<UniversityInfo> universityList = new ArrayList<>();
        Gson gson = new Gson();
        Map map = gson.fromJson(json, Map.class);
        ArrayList<Map> dataList = (ArrayList<Map>) map.get("extraData");

        if (dataList != null && dataList.size() > 0) {
            for (Map obj : dataList) {
                String cid = result;
                String year = (String) obj.get("year");
                String batch = (String) obj.get("pici");
                String up = (String) obj.get("gaofen");
                String low = (String) obj.get("difen");
                String avg = (String) obj.get("pjfen");
                Double difvalue = (Double) obj.get("xc");
                String lowlevel = (String) obj.get("zdwc");
                UniversityInfo universityInfo = new UniversityInfo();
                universityInfo.setCid(cid);
                universityInfo.setYear(year);
                universityInfo.setBatch(batch);
                universityInfo.setUp(up);
                universityInfo.setLow(low);
                universityInfo.setAvg(avg);
                universityInfo.setDifvalue(difvalue);
                universityInfo.setLowlevel(lowlevel);
                universityList.add(universityInfo);
            }
        }
    //   MysqlUtils.SaveData(universityList);
    }
}
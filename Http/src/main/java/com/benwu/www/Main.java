package com.benwu.www;



import com.benwu.www.pojo.News;
import com.benwu.www.utils.HttpClientUtil;

import com.benwu.www.utils.MysqlUtil;
import com.google.gson.Gson;


import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, PropertyVetoException {
        //确定URL
        String str1="https://pacaio.match.qq.com/irs/rcd?cid=137&token=d0f13d594edfc180f5bf6b845456f3ea&id=&ext=top&page=0&expIds=&callback=__jp1";
        String str2="https://pacaio.match.qq.com/irs/rcd?cid=4&token=9513f1a78a663e1d25b46a826f248c3c&ext=&page=0&expIds=&callback=__jp2";

        //发起请求获取响应
        String json1 = HttpClientUtil.doGet(str1);
        String json2 = HttpClientUtil.doGet(str2);

        //解析数据
        parseJsonNews(json1);
        parseJsonNews(json2);

    }
    public static void parseJsonNews(String json) throws PropertyVetoException {
             //去除首尾的（ ）
        int start = json.indexOf("(");
        int end = json.indexOf(")");

        json = json.substring(start + 1, end);
        List<News> newsList = new ArrayList<>();

        Gson gson = new Gson();
        Map map=gson.fromJson(json, Map.class);
        ArrayList<Map> dataList = (ArrayList<Map>) map.get("data");

        if(dataList != null && dataList.size() > 0) {
            for(Map obj : dataList) {
                // 单个map就是单个json对象（一条新闻）

                String content = (String) obj.get("intro");
                String time = (String) obj.get("publish_time");
                String source = (String) obj.get("source");
                String title = (String) obj.get("title");
                String url = (String) obj.get("url");

                 News news=new News();
                news.setTitle(title);
                news.setContent(content);
                news.setSource(source);
                news.setTime(time);
                news.setUrl(url);

                newsList.add(news);
            }
        }
        MysqlUtil.SaveData(newsList);
    }
}

package com.benwu.www.utils;

import com.benwu.www.pojo.News;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcTemplate;


import java.beans.PropertyVetoException;
import java.util.List;

public class MysqlUtil {
    public static void SaveData(List<News> newsList) throws PropertyVetoException {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
        comboPooledDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/spider?characterEncoding=utf8");
        comboPooledDataSource.setUser("root");
        comboPooledDataSource.setPassword("123456");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(comboPooledDataSource);
        String sql = "insert into news(title,content,time,source,url) values(?,?,?,?,?)";

        if (newsList != null && newsList.size() > 0) {
            for (int i = 0; i < newsList.size(); i++) {
                News news = newsList.get(i);
                // 保存数据库
                jdbcTemplate.update(sql,news.getTitle(), news.getContent(), news.getTime()
                        , news.getSource(), news.getUrl());

            }
        }
    }
}
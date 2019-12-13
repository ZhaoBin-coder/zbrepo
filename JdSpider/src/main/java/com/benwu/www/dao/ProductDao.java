package com.benwu.www.dao;


import com.benwu.www.pojo.Product;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.beans.PropertyVetoException;

public class ProductDao extends JdbcTemplate {
    public ProductDao(){

        //定义c3p0连接池
        ComboPooledDataSource ds = new ComboPooledDataSource();
        try {
            ds.setDriverClass("com.mysql.jdbc.Driver");
            ds.setUser("root");
            ds.setPassword("123456");
            ds.setJdbcUrl("jdbc:mysql://localhost:3306/spider?characterEncoding=utf-8");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        super.setDataSource(ds);
    }
    public void addProduct(Product product){
        super.update("insert into goods values (?,?,?,?,?)",
                product.getPid(),product.getTitle(),product.getPname(),product.getBrand(),product.getPrice());

    }


}

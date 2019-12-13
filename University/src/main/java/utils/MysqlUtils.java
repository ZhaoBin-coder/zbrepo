package utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import pojo.Artical;
import pojo.Two;
import pojo.UniversityInfo;

import java.beans.PropertyVetoException;
import java.util.List;


public class MysqlUtils {
    public static void SaveData(List<Artical> universityList) throws PropertyVetoException {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
        comboPooledDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/spider?characterEncoding=utf8");
        comboPooledDataSource.setUser("root");
        comboPooledDataSource.setPassword("123456");
        comboPooledDataSource.setInitialPoolSize(2);
        comboPooledDataSource.setMinPoolSize(2);
       comboPooledDataSource.setMaxPoolSize(20);
       comboPooledDataSource.setMaxIdleTime(20);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(comboPooledDataSource);
        String sql = "insert into articl4(cid,catalog) values(?,?)";

        if (universityList != null && universityList.size() > 0) {
            for (int i = 0; i < universityList.size(); i++) {
                Artical info = universityList.get(i);
                // 保存数据库
                jdbcTemplate.update(sql,info.getCid(),info.getCatalog());

            }
        }
    }
}

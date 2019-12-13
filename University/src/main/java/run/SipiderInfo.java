package run;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

public class SipiderInfo {
    public static void main(String[] args) throws IOException, SQLException, PropertyVetoException {
        long start = System.currentTimeMillis();
        SpiderUniversity spider = new SpiderUniversity();
        spider.start();
        long end = System.currentTimeMillis();
        System.out.println("抓取完毕并保存至数据库用时:" + ((double)(end-start))/1000.00 + "s");

    }
}

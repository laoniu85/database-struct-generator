package com.leyoss;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * Created by liyongliu on 2017/3/24.
 */
public class JDBCTemplateTest {
    @Test
    @Ignore
    public void test() {
        // 创建工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建工作表
        HSSFSheet sheet = workbook.createSheet("数据库表结构");

        MysqlDataSource mysqlDataSource = new MysqlDataSource();

        String database = "lyss_test";
        mysqlDataSource.setDatabaseName(database);
        mysqlDataSource.setPassword("root");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setServerName("localhost");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(mysqlDataSource);
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(String.format("select * from information_schema.tables where TABLE_SCHEMA='%s';", database));
        tables.forEach(table -> {
            String tableName = table.get("TABLE_NAME").toString();
            System.out.println(format("tableName: %s comment: %s", tableName, table.get("TABLE_COMMENT")));
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(format("select COLUMN_NAME,COLUMN_TYPE,COLUMN_COMMENT from information_schema.columns where TABLE_SCHEMA= '%s' or  TABLE_NAME ='%s'", database, tableName));
            columns.forEach(column -> {
                System.out.println(format("name: %s type: %s comment: %s", column.get("COLUMN_NAME"), column.get("COLUMN_TYPE"), column.get("COLUMN_COMMENT")));
            });

        });

        System.out.println("table count:" + tables.size());
    }
}

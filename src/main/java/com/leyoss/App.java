package com.leyoss;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * Hello world!
 */
public class App {

    int row = 0;
    // 创建工作薄
    HSSFWorkbook workbook = new HSSFWorkbook();
    // 创建工作表
    HSSFSheet sheet = workbook.createSheet("数据库表结构");

    CellStyle style;

    public void test() throws IOException {
        style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.GOLD.getIndex());
        style.setFillForegroundColor(IndexedColors.GOLD.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        MysqlDataSource mysqlDataSource = new MysqlDataSource();

        String database = "lyss_test";
        mysqlDataSource.setDatabaseName(database);
        mysqlDataSource.setPassword("root");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setServerName("localhost");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(mysqlDataSource);
        row = 0;
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(String.format("select * from information_schema.tables where TABLE_SCHEMA='%s';", database));
        tables.forEach(table -> {
            sheet.createRow(row++);
            HSSFRow tableInfoRow = sheet.createRow(row++);
            String tableName = table.get("TABLE_NAME").toString();


            List<Map<String, Object>> columns = jdbcTemplate.queryForList(format("select COLUMN_NAME,COLUMN_TYPE,COLUMN_COMMENT from information_schema.columns where TABLE_SCHEMA= '%s' and  TABLE_NAME ='%s'", database, tableName));
            int c = 0;
            createCell(tableInfoRow, tableName, c++);
            createCell(tableInfoRow, "说明:" + table.get("TABLE_COMMENT"), c++);
            createCell(tableInfoRow, "字段数:" + columns.size(), c++);

            System.out.println(format("tableName: %s comment: %s 字段数: %d", tableName, table.get("TABLE_COMMENT"), columns.size()));
            columns.forEach(column -> {
                HSSFRow columnInfoRow = sheet.createRow(row++);
                columnInfoRow.createCell(0).setCellValue("" + column.get("COLUMN_NAME"));
                columnInfoRow.createCell(1).setCellValue("" + column.get("COLUMN_TYPE"));
                columnInfoRow.createCell(2).setCellValue("" + column.get("COLUMN_COMMENT"));
                System.out.println(format("name: %s type: %s comment: %s", column.get("COLUMN_NAME"), column.get("COLUMN_TYPE"), column.get("COLUMN_COMMENT")));
            });

        });
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        HSSFSheet tableSheet = workbook.createSheet("库说明");

        tables = jdbcTemplate.queryForList(String.format("select * from information_schema.tables where TABLE_SCHEMA='%s';", database));
        row = 0;
        HSSFRow headerRow = tableSheet.createRow(row++);
        headerRow.createCell(0).setCellValue("表名");
        headerRow.createCell(1).setCellValue("字段数量");
        headerRow.createCell(2).setCellValue("说明");
        tables.forEach(table -> {
            HSSFRow tableInfoRow = tableSheet.createRow(row++);
            String tableName = table.get("TABLE_NAME").toString();
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(format("select COLUMN_NAME,COLUMN_TYPE,COLUMN_COMMENT from information_schema.columns where TABLE_SCHEMA= '%s' and  TABLE_NAME ='%s'", database, tableName));
            tableInfoRow.createCell(0).setCellValue("" + tableName);
            tableInfoRow.createCell(1).setCellValue("" + columns.size());
            tableInfoRow.createCell(2).setCellValue("" + table.get("TABLE_COMMENT"));
        });
        tableSheet.autoSizeColumn(0);
        tableSheet.autoSizeColumn(1);
        tableSheet.autoSizeColumn(2);


        File xlsFile = new File("target/table_info.xls");
        FileOutputStream xlsStream = new FileOutputStream(xlsFile);
        workbook.write(xlsStream);

        System.out.println("table count:" + tables.size());
    }

    public static void main(String[] args) throws IOException {
        App app = new App();
        app.test();
    }

    private void createCell(HSSFRow tableInfoRow, String value, int c) {
        HSSFCell cell = tableInfoRow.createCell(c);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

}

package com.leyoss;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;

/**
 * Hello world!
 */

@EnableAutoConfiguration
public class App extends JPanel {

    int row = 0;
    // 创建工作薄
    HSSFWorkbook workbook = new HSSFWorkbook();
    // 创建工作表
    HSSFSheet sheet = workbook.createSheet("数据库表结构");

    CellStyle style;
    GridBagConstraints gridBagConstraints;

    String host="localhost";
    int port=3306;
    String db="lyss_test";
    String user="root";
    String pass="root";

    JdbcTemplate jdbcTemplate;

    public void initField() throws IOException {
        Properties pro = new Properties();
        FileInputStream in = new FileInputStream("conf.properties");
        pro.load(in);
        in.close();
        host = pro.getProperty("mysql.host", "localhost");
        port = Integer.parseInt(pro.getProperty("mysql.port", "3306"));
        db = pro.getProperty("mysql.db", "lyss_test");
        user = pro.getProperty("mysql.user", "root");
        pass = pro.getProperty("mysql.pass", "root");
    }

    public void initDB() {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setDatabaseName(db);
        mysqlDataSource.setPassword(pass);
        mysqlDataSource.setPort(port);
        mysqlDataSource.setUser(user);
        mysqlDataSource.setServerName(host);
        jdbcTemplate = new JdbcTemplate(mysqlDataSource);
    }


    public App() {

        try {
            initField();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error!", JOptionPane.WARNING_MESSAGE);
        }
        try {
            //设置本属性将改变窗口边框样式定义
            //BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
            //org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
            //TODO exception
        }
        JFrame jFrame = new JFrame("mysql-database-tool");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea textArea = new JTextArea();
        //anel.setLayout(new GridLayout());
        textArea.setText("test");
        GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 8;
        gridBagConstraints.weighty = 4;
        //host
        addComponent(new JLabel("host"), 0, 0, 1, 1);
        TextField hostField = new TextField(host, 15);
        addComponent(hostField, 1, 0, 1, 1);
        //
        addComponent(new JLabel("port"), 4, 0, 1, 1);
        TextField portField = new TextField(Integer.toString(port), 15);
        addComponent(portField, 5, 0, 1, 1);
        //
        addComponent(new JLabel("user"), 0, 1, 1, 1);
        TextField userField = new TextField(user, 15);
        addComponent(userField, 1, 1, 1, 1);
        //
        addComponent(new JLabel("pass"), 4, 1, 1, 1);
        TextField passFeild = new TextField(pass, 15);
        addComponent(passFeild, 5, 1, 3, 1);

        //
        addComponent(new JLabel("db"), 0, 2, 1, 1);
        TextField dbField = new TextField(db, 30);
        addComponent(dbField, 1, 2, 3, 1);
        //
        JButton saveButton = new JButton("save");
        addComponent(saveButton, 0, 3, 1, 2);
        JButton testButton = new JButton("testConnect");
        addComponent(testButton, 2, 3, 1, 2);
        JButton generateButton = new JButton("generate");
        addComponent(generateButton, 4, 3, 1, 2);
        final Runnable getValues = () -> {
            host = hostField.getText();
            port = Integer.parseInt(portField.getText());
            db = dbField.getText();
            user = userField.getText();
            pass = passFeild.getText();
        };

        saveButton.addActionListener(action -> {
            Properties pro = new Properties();
            FileOutputStream oFile = null;
            try {
                getValues.run();
                oFile = new FileOutputStream("conf.properties");
                pro.put("mysql.host", host);
                pro.put("mysql.port", Integer.toString(port));
                pro.put("mysql.db", db);
                pro.put("mysql.user", user);
                pro.put("mysql.pass", pass);
                pro.store(oFile, "Comment");
                oFile.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "error!", JOptionPane.WARNING_MESSAGE);
            }


        });

        testButton.addActionListener(action -> {
            try {
                getValues.run();
                initDB();
                test();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "error!", JOptionPane.WARNING_MESSAGE);
            }
        });

        generateButton.addActionListener(action -> {
            try {
                getValues.run();
                initDB();
                generate();
                JOptionPane.showMessageDialog(this, "generate success!", "success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "error!", JOptionPane.WARNING_MESSAGE);
            }
        });

        //当TextArea里的内容过长时生成滚动条
        //panel.add(new JScrollPane(textArea));
        jFrame.add(this);
        jFrame.setSize(600, 300);
        jFrame.setVisible(true);


    }

    public void test() throws IOException {
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(String.format("select * from information_schema.tables where TABLE_SCHEMA='%s';", db));
        String message = "connect success!\n" +
                "tableCount:" + tables.size();
        JOptionPane.showMessageDialog(this, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    public void generate() throws IOException {
        style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.GOLD.getIndex());
        style.setFillForegroundColor(IndexedColors.GOLD.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        row = 0;
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(String.format("select * from information_schema.tables where TABLE_SCHEMA='%s';", db));
        tables.forEach(table -> {
            sheet.createRow(row++);
            HSSFRow tableInfoRow = sheet.createRow(row++);
            String tableName = table.get("TABLE_NAME").toString();


            List<Map<String, Object>> columns = jdbcTemplate.queryForList(format("select COLUMN_NAME,COLUMN_TYPE,COLUMN_COMMENT from information_schema.columns where TABLE_SCHEMA= '%s' and  TABLE_NAME ='%s'", db, tableName));
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

        tables = jdbcTemplate.queryForList(String.format("select * from information_schema.tables where TABLE_SCHEMA='%s';", db));
        row = 0;
        HSSFRow headerRow = tableSheet.createRow(row++);
        headerRow.createCell(0).setCellValue("表名");
        headerRow.createCell(1).setCellValue("字段数量");
        headerRow.createCell(2).setCellValue("说明");
        tables.forEach(table -> {
            HSSFRow tableInfoRow = tableSheet.createRow(row++);
            String tableName = table.get("TABLE_NAME").toString();
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(format("select COLUMN_NAME,COLUMN_TYPE,COLUMN_COMMENT from information_schema.columns where TABLE_SCHEMA= '%s' and  TABLE_NAME ='%s'", db, tableName));
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


    private void addComponent(Component component, int x, int y, int w, int h) {
        gridBagConstraints.gridx = x;
        gridBagConstraints.gridy = y;
        gridBagConstraints.gridwidth = w;
        gridBagConstraints.gridheight = h;
        //Container中的方法
        add(component, gridBagConstraints);
    }


    public static void main(String[] args) throws IOException {
        App app = new App();
        //app.test();
    }

    private void createCell(HSSFRow tableInfoRow, String value, int c) {
        HSSFCell cell = tableInfoRow.createCell(c);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

}

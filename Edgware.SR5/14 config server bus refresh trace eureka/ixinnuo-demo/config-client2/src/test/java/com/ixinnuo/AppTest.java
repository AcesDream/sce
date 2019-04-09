package com.ixinnuo;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

    private Logger logger = LoggerFactory.getLogger(AppTest.class);


    @Test
    public void sameTimestampCompare() throws Exception{
        String day = "2018-11-07";
        List<Map<String, String>> tycDataList = getTxtTyc(day, "D:\\gitwork\\接口\\对账\\天眼查对账\\454-2018.11.txt");
        //List<Map<String, String>> tycDataList = getTxtTyc(day, "D:\\gitwork\\接口\\对账\\天眼查对账\\730-2018.11.txt");
        //List<Map<String, String>> tycDataList = getTxtTyc(day, "D:\\gitwork\\接口\\对账\\天眼查对账\\730-2018.11.txt");

        for(int i = 0; i < tycDataList.size(); i++) {
            logger.info("{}", tycDataList.get(i));
        }

        Map<String, Integer> tycKeyWordCount = new HashMap<>();
        for(int i = 0; i < tycDataList.size();i++) {
            String keyword = tycDataList.get(i).get("keyWord");
            tycKeyWordCount.put(keyword, tycKeyWordCount.get(keyword) == null ? 1:tycKeyWordCount.get(keyword)+1);
        }

        List<String> unEqualKeyList = new ArrayList<>();
        for(String key : tycKeyWordCount.keySet()) {


            if(tycKeyWordCount.get(key) > 1) {
                unEqualKeyList.add(key);
            }

        }

        for(String key : unEqualKeyList) {
            for(int i = 0; i < tycDataList.size(); i++) {
                if(key.equals(tycDataList.get(i).get("keyWord"))) {
                    logger.info("tyc:{}", tycDataList.get(i));
                }
            }
        }

        //Map<Long, Integer> timestampCount = new HashMap<>();
        //for(int i = 0; i < tycDataList.size(); i++) {
        //    Long timestamp = Long.valueOf(tycDataList.get(i).get("time"));
        //    timestampCount.put(timestamp, timestampCount.get(timestamp) == null ? 1:timestampCount.get(timestamp)+1);
        //}
		//
        //for(Long key : timestampCount.keySet()) {
        //    if(timestampCount.get(key) > 1) {
        //        logger.info("不止一个：{}，数量：{}", key, timestampCount.get(key));
        //    }
        //}
    }

    public void dayCompare(String day) throws Exception{
        //String day = "2018-11-22";
        List<Map<String, String>> tycDataList = getTxtTyc(day, "D:\\gitwork\\接口\\对账\\天眼查对账\\549-2018.11.txt");
        List<Map<String, String>> ixnDataList = getMySqlTyc(day);

        //writeFile(basePath + fileName + ".error", errorList);
        writeFile(day + "-tyc.txt", tycDataList);
        writeFile(day + "-ixn.txt", ixnDataList);

        Map<String, Integer> tycKeyWordCount = new HashMap<>();
        for(int i = 0; i < tycDataList.size();i++) {
            String keyword = tycDataList.get(i).get("keyWord");
            tycKeyWordCount.put(keyword, tycKeyWordCount.get(keyword) == null ? 1:tycKeyWordCount.get(keyword)+1);
        }

        Map<String, Integer> ixnKeyWordCount = new HashMap<>();
        for(int i = 0; i < ixnDataList.size();i++) {
            String keyword = ixnDataList.get(i).get("keyWord");
            ixnKeyWordCount.put(keyword, ixnKeyWordCount.get(keyword) == null ? 1:ixnKeyWordCount.get(keyword)+1);
        }


        List<String> unEqualKeyList = new ArrayList<>();
        for(String key : tycKeyWordCount.keySet()) {

            if(!ixnKeyWordCount.containsKey(key)) {
                logger.info("爱信诺没有这个：{}", key);
            } else {
                if(tycKeyWordCount.get(key) != ixnKeyWordCount.get(key)) {
                    logger.info("爱信诺天眼查数量不一致：{}，数量：{}", tycKeyWordCount.get(key) + "\t" + ixnKeyWordCount.get(key), key);
                    unEqualKeyList.add(key);
                }
            }
        }

        logger.info("{}:{}", tycDataList.size(), ixnDataList.size());


        for(String key : unEqualKeyList) {
            for(int i = 0; i < tycDataList.size(); i++) {
                if(key.equals(tycDataList.get(i).get("keyWord"))) {
                    logger.info("tyc:{}", tycDataList.get(i));
                }
            }

            for(int i = 0; i < ixnDataList.size(); i++) {
                if(key.equals(ixnDataList.get(i).get("keyWord"))) {
                    logger.info("ixn:{}", ixnDataList.get(i));
                }
            }
        }

    }


    @Test
    public void compare() throws Exception{

        Calendar cal = Calendar.getInstance();


        for(int i = 1; i < 31; i++) {

            cal.set(2018, 10, i);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String day = sdf.format(cal.getTime());

            List<Map<String, String>> tycDataList = getTxtTyc(day, "D:\\gitwork\\接口\\对账\\天眼查对账\\549-2018.11.txt");
            List<Map<String, String>> ixnDataList = getMySqlTyc(day);

            int max = tycDataList.size() > ixnDataList.size() ? ixnDataList.size() : tycDataList.size();
            if(tycDataList.size() != ixnDataList.size()) {
                logger.info("数据不一致：{}", day);
                logger.info("{}\t{}", tycDataList.size(),ixnDataList.size());

                dayCompare(day);
            }



        }


    }
    /**
     * Rigorous Test :-)
     */

    public List<Map<String, String>> getTxtTyc(String day, String filePath) throws Exception {
        List<String> fileData = readTxtFileIntoStringArrList(filePath);

        //logger.info("股东信息总条数：{}", fileData.size());

        List<Map<String, String>> dataList = new ArrayList<>();

        int i = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        for(String line : fileData) {
            Map<String, Object> dataMap = objectMapper.readValue(line.substring(26), Map.class);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long time = (long)dataMap.get("time");
            Date callTime = new Date(time);
            String keyWord = dataMap.get("keyword").toString();


            if(sdf.format(callTime).contains(day)) {
                //logger.info("{}", keyWord);
                Map<String, String> temp = new HashMap<>();
                temp.put("time", time+"");
                temp.put("callTime", sdf.format(callTime));
                temp.put("keyWord", keyWord);

                dataList.add(temp);
            }

        }

        return dataList;
    }

    /**
     * 功能：Java读取txt文件的内容 步骤：1：先获得文件句柄 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流 4：一行一行的输出。readline()。 备注：需要考虑的是异常情况
     *
     * @param filePath
     *            文件路径[到达文件:如： D:\aa.txt]
     * @return 将这个文件按照每一行切割成数组存放到list中。
     */
    public static List<String> readTxtFileIntoStringArrList(String filePath)
    {
        List<String> list = new ArrayList<String>();
        try
        {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists())
            { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while ((lineTxt = bufferedReader.readLine()) != null)
                {
                    list.add(lineTxt);
                }
                bufferedReader.close();
                read.close();
            }
            else
            {
                System.out.println("找不到指定的文件");
            }
        }
        catch (Exception e)
        {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

        return list;
    }


    public List<Map<String, String>> getMySqlTyc(String day) {

        List<Map<String, String>> dataList = new ArrayList<>();
        //声明Connection对象
        Connection con;
        //驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://172.16.16.12:3306/interface_plat_v2?useUnicode=true&characterEncoding=UTF-8";
        //MySQL配置时的用户名
        String user = "interface_rw";
        //MySQL配置时的密码
        String password = "4321qwer";
        //遍历查询结果集
        try {
            //加载驱动程序
            Class.forName(driver);
            //1.getConnection()方法，连接MySQL数据库！！
            con = DriverManager.getConnection(url,user,password);
            if(!con.isClosed()) {
                //System.out.println("Succeeded connecting to the Database!");
            }

            //2.创建statement类对象，用来执行SQL语句！！
            Statement statement = con.createStatement();
            //要执行的SQL语句
            String sql = "SELECT create_time, params FROM out_interface_log where interface_name = 'tyc' and interface_type = 'gdxx' and create_time BETWEEN '" + day + " 00:00:00' and '" + day + " 23:59:59'";
            //3.ResultSet类，用来存放获取的结果集！！
            ResultSet rs = statement.executeQuery(sql);


            String createTime = null;
            String params = null;
            Map<String, String> result = new HashMap<String, String>();

            ObjectMapper objectMapper = new ObjectMapper();

            while(rs.next()){
                //获取stuname这列数据
                createTime = rs.getString("create_time");
                //获取stuid这列数据
                params = rs.getString("params");


                Map<String, Object> dataMap = objectMapper.readValue(params, Map.class);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                String keyWord = dataMap.get("name").toString();
                //logger.info("{}",keyWord);

                Map<String, String> temp = new HashMap<>();
                temp.put("callTime", createTime.substring(0, 19));
                temp.put("keyWord", keyWord);

                dataList.add(temp);

                //输出结果
            }
            rs.close();
            con.close();


        } catch(ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();
        } catch(SQLException e) {
            //数据库连接失败异常处理
            e.printStackTrace();
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally{
            //System.out.println("数据库数据成功获取！！");
        }

        return dataList;
    }

    public void writeFile(String filePath, List<Map<String,String>> data) throws Exception{
        File file = new File(filePath);

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for(int i = 0; i < data.size(); i++) {
            bw.write(data.get(i).get("keyWord")+"\n");
        }
        bw.close();
    }
}

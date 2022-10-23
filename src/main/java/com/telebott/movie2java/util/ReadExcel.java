package com.telebott.movie2java.util;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
public class ReadExcel {
    public static JSONArray getData() {
        JSONArray array = new JSONArray();
        try {
            URL url = new URL("http://github1.oss-cn-hongkong.aliyuncs.com/java/ppvod.xls");
//            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            ReadExcel obj = new ReadExcel();
            List excelList = obj.readExcel(inStream);
            for (int i = 1; i < excelList.size(); i++) {
                JSONObject object = new JSONObject();
                List list = (List) excelList.get(i);
                if (list.get(0) != null) object.put("m3u8", list.get(0).toString());
                if (list.get(1) != null){
                    String title = list.get(1).toString();
                    if (title.contains("【小视频】")){
                        title = title.substring("【小视频】".length());
                    }
                    object.put("title", title);
                }
                if (list.get(2) != null) object.put("pic", list.get(2).toString());
                array.add(object);
//                for (int j = 0; j < list.size(); j++) {
//                    System.out.println(list.get(j));
//                }
//                System.out.println(object);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return array;
    }
    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
    public List readExcel(File file) {
        try {
            return readExcel(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<List>();
    }
    public List readExcel(InputStream is) {
        try {
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            for (int index = 0; index < sheet_size; index++) {
                List<List> outerList=new ArrayList<List>();
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);
                // sheet.getRows()返回该页的总行数
                for (int i = 0; i < sheet.getRows(); i++) {
                    List innerList=new ArrayList();
                    // sheet.getColumns()返回该页的总列数
                    for (int j = 0; j < sheet.getColumns(); j++) {
                        String cellinfo = sheet.getCell(j, i).getContents();
                        if(cellinfo.isEmpty()){
                            continue;
                        }
                        innerList.add(cellinfo);
//                        System.out.print(cellinfo);
                    }
                    outerList.add(i, innerList);
//                    System.out.println();
                }
                return outerList;
            }
        } catch (BiffException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

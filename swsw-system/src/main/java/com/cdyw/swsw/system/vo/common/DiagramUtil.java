package com.cdyw.swsw.system.vo.common;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DiagramUtil {


    /**
     * @param params type 10 MONITOR 状态数据
     * @param params type 20 WNDFFT 功率谱数据
     * @param params type 31 WNDOBS/ROBS 实时数据
     * @param params type 32 WNDOBS/HOBS 半小时数据
     * @param params type 33 WNDOBS/OOBS 1小时数据
     * @param params type 40 WNDRAD 径向数据
     * @param params day 时间戳
     * @return
     */
    public static List<Map<String, Object>> getFilePathList(Map<String, Object> params) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int type = Integer.valueOf(params.get("type").toString());
        String day = params.get("day").toString();// 时间戳
        String path = (String) params.get("path");//得到配置文件中存储的目录

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        // 查询指定天以及前面2天的数据
        long timestamp = Long.valueOf(day);// 参数为时间戳
        day = TimeUtil.getDayStringByTimestamp(timestamp, "yyyyMMdd");
        try {
            date = sdf.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        } // 数据
        String month = day.substring(0, 6);
        for (int i = 0; i < 3; i++) {
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -i);
            String tempDay = sdf.format(calendar.getTime());
            day = month + "/" + sdf.format(calendar.getTime());

            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("data", getFileList(type, tempDay, path));
            map1.put("day", tempDay);
            list.add(map1);
        }
        return list;
    }


    /**
     * 获取指定天数的文件列表名称
     *
     * @param type
     * @param day
     * @return
     */
    public static List<String> getFileList(int type, String day, String path) {

        List<String> list = new ArrayList<String>();

        String v = path;// D:/log/
        String pathTemp = day + RadarFileType.getName(type);// 20170320/WNDOBS/ROBS/
        String rePath = v + pathTemp;   //获得本地文件路径 D:/log/20170320/WNDOBS/ROBS/


        File file = new File(rePath);
        File[] fileList = file.listFiles();
        //比如fileList[0] = D:/log/20170320/WNDOBS/ROBS/Z_RADA_I_54511_20170320000000_P_WPRD_LC_ROBS.TXT
        if (fileList != null && fileList.length > 0) {

            Arrays.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    long diff = f1.lastModified() - f2.lastModified();
                    if (diff > 0) {
                        return 1;
                    } else if (diff == 0) {
                        return 0;
                    } else {
                        return -1;
                    }
                }


                @Override
                public boolean equals(Object obj) {
                    return true;
                }
            });

            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isFile()) {
                    String fileName = fileList[i].getName();//Z_RADA_I_54511_20170320000000_P_WPRD_LC_ROBS.TXT
                    list.add(pathTemp + fileName);// 20170320/WNDOBS/ROBS/Z_RADA_I_54511_20170320000000_P_WPRD_LC_ROBS.TXT
                }
            }
        }


        return list;
    }


    public static void getFileByName(Map<String, Object> params, HttpServletResponse response) {
        String fileName = (String) params.get("fileName");
        //System.out.println("fileName:" + fileName);

        String filePathTemp = (String) params.get("filePath");// 文件存放路径
        if (StringUtils.isEmpty(filePathTemp)) {
            return;
        }
        String v = filePathTemp;
        String rePath = v + fileName;
        //System.out.println("rePath："+rePath);
        File file = new File(rePath);
        if (file.exists()) {
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //System.out.println("suffixName:" + suffixName);
            ReadZipFile.readTxtFile(rePath, response);
        }
    }

}

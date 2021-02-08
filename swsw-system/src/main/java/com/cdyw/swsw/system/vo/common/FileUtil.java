package com.cdyw.swsw.system.vo.common;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.enums.DataProductTypeEnum;
import com.cdyw.swsw.common.domain.ao.enums.FileTypeEnum;
import com.cdyw.swsw.common.domain.ao.enums.ProductEnum;
import com.cdyw.swsw.common.domain.entity.fusion12h.Fusion12hWeaInformTable;
import com.cdyw.swsw.common.domain.entity.fusion12h.Fusion12hWeatherInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 文件操作的工具类：目前下载和在线预览功能在这里
 *
 * @author jovi
 */
public class FileUtil {

    public static void getFileByName(Map<String, Object> params, HttpServletResponse response) {
        String fileName = (String) params.get("fileName");
        // 文件存放路径
        String filePathTemp = (String) params.get("filePath");
        if (StringUtils.isEmpty(filePathTemp)) {
            return;
        }
        String rePath = filePathTemp + fileName;
        File file = new File(rePath);
        if (file.exists()) {
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            ReadZipFile.readTxtFile(rePath, response);
        }
    }

    /**
     * 下载或者在线展示文件的公共方法
     */
    public static void downloadOrOnlineFile(File filePathName, HttpServletResponse response, boolean isOnLine) throws IOException {
        if (!filePathName.exists()) {
            response.sendError(404, "File not found!");
        }
        InputStream inputStream = new FileInputStream(filePathName);
        response.reset(); // 非常重要
        if (isOnLine) {
            URL u = new URL("file:///" + filePathName);
            URLConnection urlConnection = u.openConnection();
            String contentType = urlConnection.getContentType();
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline;filename=" + filePathName.getName());
            // 文件名应该编码成UTF-8
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        } else { // 纯下载方式
            String fileName = filePathName.getName();
            // 防止文件名中文乱码
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            // dll
//            response.setContentType("application/x-msdownload");
            // doc
//            response.setContentType("application/msword");
            // docx
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            // 防止文件名中文乱码
//            response.setHeader( "Content-Disposition", "attachment;filename=" + new String( fileName.getBytes("gb2312"), "ISO8859-1" ) );
        }
        OutputStream out = response.getOutputStream();
        byte[] bytes = IoUtil.readBytes(inputStream, true);
        IoUtil.write(out, true, bytes);
    }

    /**
     * 支持修改 word 指定文字内容（最后一个参数代表处理不同产品类型表格的操作，如果没有就置空）
     */
    @SuppressWarnings("unchecked")
    public static File modifyFileText(File srcFile, File destFile, Map<String, Object> map, DataProductTypeEnum productType) throws FileNotFoundException {
        InputStream ins = new FileInputStream(srcFile);
        String suffix = cn.hutool.core.io.FileUtil.getSuffix(srcFile);
        // 此处有个坑：(this exception is not set as Runtime in order to force user to manage the exception in a try/catch)
        if (FileTypeEnum.FILE_TYPE_DOC.getSuffix().equals(suffix)) {
            try {
                HWPFDocument document = new HWPFDocument(ins);
                Range range = document.getRange();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    range.replaceText(entry.getKey(), entry.getValue().toString() != null ? entry.getValue().toString() : "");
                }
                OutputStream ots = new FileOutputStream(destFile);
                document.write(ots);
                ots.close();
                ins.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else if (FileTypeEnum.FILE_TYPE_DOCX.getSuffix().equals(suffix)) {
            try {
                XWPFDocument document = new XWPFDocument(ins);
                // 处理文本
                List<XWPFParagraph> paragraphs = document.getParagraphs();
                handleParagraph(paragraphs, map);
                // 处理表格（暂时仅有产品制作的时候涉及表格替换操作，后续如果有其他地方也涉及表格替换，则重新 copy 便是）
                if (productType != null) {
                    List<XWPFTable> tables = document.getTables();
                    for (int t = 0; t < tables.size(); t++) {
                        // 每种产品对应的表格数量都不一样，分开处理
                        switch (productType) {
                            case PRODUCT_TYPE_SHORT_FORECAST:
                                XWPFTable tableShort = tables.get(t);
                                List<XWPFTableRow> rowShorts = tableShort.getRows();
                                for (XWPFTableRow rowShort : rowShorts) {
                                    List<XWPFTableCell> cellShorts = rowShort.getTableCells();
                                    for (XWPFTableCell cellShort : cellShorts) {
                                        List<XWPFParagraph> cellParagraphs = cellShort.getParagraphs();
                                        handleParagraph(cellParagraphs, map);
                                    }
                                }
                                break;
                            case PRODUCT_TYPE_GRID_FORECAST:
                                // 产品不同，文档的表格也不同，所以要不同处理
                                List<Fusion12hWeatherInformation> fusion12hWeatherInformation = new ArrayList<>();
                                List<Fusion12hWeatherInformation> fusion12hWeatherInformations = new ArrayList<>();
                                String baseTime = null;
                                try {
                                    fusion12hWeatherInformation = (List<Fusion12hWeatherInformation>) map.get("fusion12hWeatherInformation");
                                    fusion12hWeatherInformations = (List<Fusion12hWeatherInformation>) map.get("fusion12hWeatherInformations");
                                    baseTime = (String) map.get("baseTime");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // 处理天气信息数据，整合在同一个时刻所有要素的实体类 Fusion12hWeaInformTable 中
                                List<Fusion12hWeaInformTable> fusion12hWeaInformTables = getOneList(fusion12hWeatherInformation);
                                List<Fusion12hWeaInformTable> fusion12hWeaInformTable2s = getOneList(fusion12hWeatherInformations);
                                // 第一张表不处理
                                if (t == 0) {
                                    continue;
                                }
                                XWPFTable tableGrid = tables.get(t);
                                List<XWPFTableRow> rowGrids = tableGrid.getRows();
                                if (t == 1) {
                                    for (int r = 0; r < rowGrids.size(); r++) {
                                        // 前两行都是表头跳过
                                        if (r == 0 || r == 1) {
                                            continue;
                                        }
                                        if (r - 2 < fusion12hWeaInformTables.size()) {
                                            Fusion12hWeaInformTable weather = fusion12hWeaInformTables.get(r - 2);
                                            String typeTemValue = weather.getTypeTemValue();
                                            String typeRhuValue = weather.getTypeRhuValue();
                                            String typeRainValue = weather.getTypeRainValue();
                                            String timeStr = weather.getForecastTime();
                                            XWPFTableRow row = rowGrids.get(r);
                                            List<XWPFTableCell> cells = row.getTableCells();
                                            for (int c = 0; c < cells.size(); c++) {
                                                XWPFTableCell cell = cells.get(c);
                                                // 第一个cell放时次
                                                if (c == 0) {
                                                    cell.setText(timeStr);
                                                    continue;
                                                }
                                                if (c == 2) {
                                                    cell.setText(typeTemValue);
                                                    continue;
                                                }
                                                if (c == 3) {
                                                    cell.setText(typeRhuValue);
                                                    continue;
                                                }
                                                if (c == 1 || c == 4 || c == 5) {
                                                    continue;
                                                }
                                                if (c == 6) {
                                                    cell.setText(typeRainValue);
                                                }
                                            }
                                        }
                                        break;
                                    }
                                    continue;
                                }
                                if (t == 2) {
                                    for (int r = 0; r < rowGrids.size(); r++) {
                                        // 前两行都是表头跳过
                                        if (r == 0 || r == 1) {
                                            continue;
                                        }
                                        if (r - 2 < fusion12hWeaInformTable2s.size()) {
                                            Fusion12hWeaInformTable weather = fusion12hWeaInformTable2s.get(r - 2);
                                            String typeTemValue = weather.getTypeTemValue();
                                            String typeRhuValue = weather.getTypeRhuValue();
                                            String typeRainValue = weather.getTypeRainValue();
                                            String timeStr = weather.getForecastTime();
                                            XWPFTableRow row = rowGrids.get(r);
                                            List<XWPFTableCell> cells = row.getTableCells();
                                            for (int c = 0; c < cells.size(); c++) {
                                                XWPFTableCell cell = cells.get(c);
                                                // 第一个cell放时次
                                                if (c == 0) {
                                                    if (baseTime != null) {
                                                        cell.setText(baseTime.substring(8, 11));
                                                    } else {
                                                        cell.setText(LocalDateTime.now().getDayOfMonth() + "日");
                                                    }
                                                    continue;
                                                }
                                                if (c == 1) {
                                                    cell.setText(timeStr);
                                                    continue;
                                                }
                                                if (c == 3) {
                                                    cell.setText(typeTemValue);
                                                    continue;
                                                }
                                                if (c == 2 || c == 4 || c == 5) {
                                                    continue;
                                                }
                                                if (c == 6) {
                                                    cell.setText(typeRainValue);
                                                    continue;
                                                }
                                                if (c == 7) {
                                                    cell.setText(typeRhuValue);
                                                }
                                            }
                                        }
                                    }
                                    continue;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                OutputStream ots = new FileOutputStream(destFile);
                document.write(ots);
                ots.close();
                ins.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            return null;
        }
        return srcFile;
    }

    /**
     * 将一个 list 里面的多条数据合成一条数据，最终生成新的 list
     *
     * @param lists 多条数据
     * @return 新的 list
     */
    private static List<Fusion12hWeaInformTable> getOneList(List<Fusion12hWeatherInformation> lists) {
        List<Fusion12hWeaInformTable> fusion12hWeaInformTables = new LinkedList<>();
        Set<String> forecastTimes = new LinkedHashSet<>();
        List<String> temValues = new LinkedList<>();
        List<String> rhuValues = new LinkedList<>();
        List<String> rainValues = new LinkedList<>();
        // 先将预报时间取出来
        for (Fusion12hWeatherInformation weather : lists) {
            String type = weather.getType();
            String typeValue = weather.getTypeValue();
            String forecastTimeStr = DateUtils.getDayStringByTimestamp(weather.getForecastTime(), "HH:mm");
            forecastTimes.add(forecastTimeStr);
            if (String.valueOf(ProductEnum.FUSION12H_TEM.getValue()).equals(type)) {
                temValues.add(typeValue);
                continue;
            }
            if (String.valueOf(ProductEnum.FUSION12H_RHU.getValue()).equals(type)) {
                rhuValues.add(typeValue);
                continue;
            }
            if (String.valueOf(ProductEnum.FUSION12H_RAIN.getValue()).equals(type)) {
                rainValues.add(typeValue);
            }
        }
        for (int i = 0; i < forecastTimes.size(); i++) {
            Fusion12hWeaInformTable defaultValue = new Fusion12hWeaInformTable();
            defaultValue.setForecastTime(ListUtil.toList(forecastTimes.iterator()).get(i));
            defaultValue.setTypeTemValue(temValues.get(i));
            defaultValue.setTypeRhuValue(rhuValues.get(i));
            defaultValue.setTypeRainValue(rainValues.get(i));
            fusion12hWeaInformTables.add(defaultValue);
        }
        return fusion12hWeaInformTables;
    }

    /**
     * 处理文档段落
     *
     * @param paragraphs 段落
     */
    private static void handleParagraph(List<XWPFParagraph> paragraphs, Map<String, Object> map) {
        for (XWPFParagraph xwpfParagraph : paragraphs) {
            List<XWPFRun> runs = xwpfParagraph.getRuns();
            for (XWPFRun run : runs) {
                if (run != null) {
                    String oneparaString = run.getText(run.getTextPosition());
                    // 此处防止读取到图片或其他元素，POI会自动置空，避免引起 NullPointException
                    if (oneparaString != null) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getValue() != null) {
                                oneparaString = oneparaString.replace(entry.getKey(), entry.getValue().toString() != null ? entry.getValue().toString() : "");
                            }
                        }
                        run.setFontFamily("仿宋");
                        run.setText(oneparaString, 0);
                    }
                }
            }
        }
    }
}

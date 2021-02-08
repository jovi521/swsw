package com.cdyw.swsw.system.app.util;

import cn.hutool.core.util.StrUtil;
import com.cdyw.swsw.common.domain.ao.enums.FileTypeEnum;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.*;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 实现文档的在线预览功能：Poi实现
 *
 * @author jovi
 */
@Component
public class FilePoiConvertUtil {

    //使用的字体，需要配置字体文件路径
    public static final String FONT = "E:\\NotoSansMyanmarUI-Bold.ttf";

    /**
     * 仅支持2003版.doc文件的转化
     */
    public static void file2Html(File inputFile) throws IOException, ParserConfigurationException, TransformerException {
        InputStream input = new FileInputStream(inputFile);
        HWPFDocument wordDocument = new HWPFDocument(input);

        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        wordToHtmlConverter.setPicturesManager((content, pictureType, suggestedName, widthInches, heightInches) -> suggestedName);
        wordToHtmlConverter.processDocument(wordDocument);

        List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
        if (pics != null) {
            for (Picture pic : pics) {
                try {
                    pic.writeImageContent(new FileOutputStream(inputFile.getPath() + pic.suggestFullFileName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        org.w3c.dom.Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(outStream);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        outStream.close();

        String content = new String(outStream.toByteArray());
        String filePath = inputFile.getParent();
        String fileName = StrUtil.subBefore(inputFile.getName(), ".", false) + "." + FileTypeEnum.FILE_TYPE_HTML.getSuffix();

//        org.jsoup.nodes.Document jsoupDocument = Jsoup.parse(content);
//        content = jsoupDocument.html();

        FileUtils.writeStringToFile(new File(filePath, fileName), content, "utf-8");
    }

    /**
     * 仅支持2007版及以上.docx文件的转化
     */
    public static void fileX2Html(File inputFile) throws IOException {
        InputStream input = new FileInputStream(inputFile);
        XWPFDocument xwpfDocument = new XWPFDocument(input);

        File imgPath = new File(inputFile.getParent() + "/image");
        XHTMLOptions options = XHTMLOptions.create();
        options.setExtractor(new FileImageExtractor(imgPath));
        // html中图片的路径 相对路径
        options.URIResolver(new BasicURIResolver("image"));
        options.setIgnoreStylesIfUnused(false);
        options.setFragment(true);

        File htmlPath = new File(inputFile.getParent() + "/html");
        OutputStream out = new FileOutputStream(htmlPath + ".html");

        XHTMLConverter.getInstance().convert(xwpfDocument, out, options);
    }

    /**
     * 使用jsoup规范html并保存到磁盘
     */
    public static void html2Html() throws FileNotFoundException {
        OutputStream outputStream = new FileOutputStream(new File(""));
        Writer writer = new BufferedWriter(null);

        org.jsoup.nodes.Document document = Jsoup.parse("");
    }

    /**
     * 仅支持2007版及以上.docx文件的转化
     */
    public static void fileX2Pdf(File inputFile) throws IOException {
        InputStream input = new FileInputStream(inputFile);
        XWPFDocument document = new XWPFDocument(input);

        File imgPath = new File(inputFile.getParent() + "/image");
        PdfOptions options = PdfOptions.create();
        options.setExtractor(new FileImageExtractor(imgPath));
        // html中图片的路径 相对路径
        options.fontEncoding(StandardCharsets.UTF_8.name());

        File htmlPath = new File(inputFile.getParent() + "/pdf");
        OutputStream out = new FileOutputStream(htmlPath + ".pdf");

        PdfConverter.getInstance().convert(document, out, options);
    }

    /**
     * 仅支持2003版.doc文件的转化
     */
    public static void file2Pdf2(File inputFile) {
        try {
            //create file inputstream object to read data from file
            FileInputStream fs = new FileInputStream(inputFile);
            //create document object to wrap the file inputstream object
            XWPFDocument doc = new XWPFDocument(fs);
            //72 units=1 inch
            com.lowagie.text.Document pdfdoc = new com.lowagie.text.Document(PageSize.A4, 72, 72, 72, 72);
            //create a pdf writer object to write text to mypdf.pdf file
            PdfWriter pwriter = PdfWriter.getInstance(pdfdoc, new FileOutputStream("E:\\test.pdf"));
            //specify the vertical space between the lines of text
            pwriter.setInitialLeading(20);
            //get all paragraphs from word docx
            List<XWPFParagraph> plist = doc.getParagraphs();

            //open pdf document for writing
            pdfdoc.open();
            for (XWPFParagraph pa : plist) {
                //read through the list of paragraphs
                //get all run objects from each paragraph
                List<XWPFRun> runs = pa.getRuns();
                //read through the run objects
                for (XWPFRun run : runs) {
                    //get pictures from the run and add them to the pdf document
                    List<XWPFPicture> piclist = run.getEmbeddedPictures();
                    //traverse through the list and write each image to a file
                    for (XWPFPicture pic : piclist) {
                        XWPFPictureData picdata = pic.getPictureData();
                        byte[] bytepic = picdata.getData();
                        Image imag = Image.getInstance(bytepic);
                        pdfdoc.add(imag);
                    }
                    //get color code
                    int color = getCode(run.getColor());
                    //construct font object
                    Font f;
                    if (run.isBold() && run.isItalic()) {
                        f = FontFactory.getFont(FontFactory.TIMES_ROMAN, String.valueOf(run.getFontSize()), Font.BOLDITALIC, color);
                    } else if (run.isBold()) {
                        f = FontFactory.getFont(FontFactory.TIMES_ROMAN, String.valueOf(run.getFontSize()), Font.BOLD, color);
                    } else if (run.isItalic()) {
                        f = FontFactory.getFont(FontFactory.TIMES_ROMAN, String.valueOf(run.getFontSize()), Font.ITALIC, color);
                    } else if (run.isStrike()) {
                        f = FontFactory.getFont(FontFactory.TIMES_ROMAN, String.valueOf(run.getFontSize()), Font.STRIKETHRU, color);
                    } else {
                        f = FontFactory.getFont(FontFactory.TIMES_ROMAN, String.valueOf(run.getFontSize()), Font.NORMAL, color);
                    }
                    //construct unicode string
                    String text = run.getText(-1);
                    byte[] bs;
                    if (text != null) {
                        bs = text.getBytes();
                        String str = new String(bs, StandardCharsets.UTF_8);
                        //add string to the pdf document
                        Chunk chObj1 = new Chunk(str, f);
                        pdfdoc.add(chObj1);
                    }

                }
                //output new line
                pdfdoc.add(new Chunk(Chunk.NEWLINE));
            }
            //close pdf document
            pdfdoc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getCode(String code) {
        int colorCode;
        if (code != null) {
            colorCode = Long.decode("0x" + code).intValue();
        } else {
            colorCode = Long.decode("0x000000").intValue();
        }
        return colorCode;
    }

    /**
     * 仅支持2003版.doc文件的转化
     */
    public File file2Pdf(File inputFile, File outputFile) throws IOException {
        InputStream input = new FileInputStream(inputFile);
        // TODO Auto-generated method stub

        POIFSFileSystem fs;
        com.lowagie.text.Document document = new com.lowagie.text.Document();

        try {
            fs = new POIFSFileSystem(input);

            HWPFDocument doc = new HWPFDocument(fs);
            WordExtractor we = new WordExtractor(doc);

            // 此处 out 为路径加最终 pdf 文件名
            OutputStream file = new FileOutputStream(outputFile);

            PdfWriter writer = PdfWriter.getInstance(document, file);

            Range range = doc.getRange();
            document.open();
            writer.setPageEmpty(true);
            document.newPage();
            writer.setPageEmpty(true);

            String[] paragraphs = we.getParagraphText();
            for (int i = 0; i < paragraphs.length; i++) {
                org.apache.poi.hwpf.usermodel.Paragraph pr = range.getParagraph(i);
                // CharacterRun run = pr.getCharacterRun(i);
                // run.setBold(true);
                // run.setCapitalized(true);
                // run.setItalic(true);
                paragraphs[i] = paragraphs[i].replaceAll("\\cM?\r?\n", "");
                // add the paragraph to the document
                document.add(new Paragraph(paragraphs[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close the document
            document.close();
        }
        return outputFile;
    }


}


//package com.cdyw.swsw.system;
//
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.io.StreamProgress;
//import cn.hutool.core.lang.Console;
//import cn.hutool.core.util.CharsetUtil;
//import cn.hutool.extra.emoji.EmojiUtil;
//import cn.hutool.extra.mail.MailUtil;
//import cn.hutool.extra.qrcode.QrCodeUtil;
//import cn.hutool.http.HttpUtil;
//import cn.hutool.http.webservice.SoapClient;
//import com.cdyw.swsw.system.vo.common.Student;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//
//@SpringBootTest
//class SwswSystemApplicationTests {
//
//	/*
//	@Autowired
//	BeanConfig beanConfig;
//	*/
//
//    @Autowired
//    private Student student;
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value(value = "${spring.mail.username}")
//    private String username;
//	/*
//	@Test
//	public void test1() {
//		System.out.println(beanConfig.getRadarfilepath());
//
//
//	}
//	*/
//
//    @Test
//    public void test2() {
//        System.out.println(student);
//    }
//
//    @Test
//    public void testHutool_qrCode() {
//        QrCodeUtil.generate("https://hutool.cn/", 300, 300, FileUtil.file("d:/qrcode.jpg"));
//    }
//
//    @Test
//    public void testHutool_emoji() {
//        String aliasStr = EmojiUtil.toAlias("😂");//:smile:
//        System.out.println(aliasStr);
//        String emoji = EmojiUtil.toUnicode(":joy:");//😄
//        System.out.println(emoji);
//        String aliasHtml = EmojiUtil.toHtml("😄");//&#x1f604;
//        System.out.println(aliasHtml);
//    }
//
//    @Test
//    public void testHutool_mail() {
//        MailUtil.send("506997994@qq.com", "测试", "邮件来自Hutool测试", false);
//    }
//
//    @Test
//    public void testBoot_mail() {
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setFrom(username);
//        simpleMailMessage.setTo("vivi506997994@icloud.com");//接收邮件的邮箱
//        simpleMailMessage.setSubject("Test-" + DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd hh:mm:ss"));
//        simpleMailMessage.setText("12341234像首歌");
//
//        mailSender.send(simpleMailMessage);
//    }
//
//    @Test
//    public void testHutool_http() {
//        // 最简单的HTTP请求，可以自动通过header等信息判断编码，不区分HTTP和HTTPS
//        String result1 = HttpUtil.get("https://www.baidu.com");
//
//        // 当无法识别页面编码的时候，可以自定义请求页面的编码
//        String result2 = HttpUtil.get("https://www.baidu.com", CharsetUtil.CHARSET_UTF_8);
//
//        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("city", "北京");
//
//        String result3 = HttpUtil.get("https://www.baidu.com", paramMap);
//
//        String result4 = HttpUtil.post("https://www.baidu.com", paramMap);
//
//    }
//
//    @Test
//    public void testHutool_down() {
//        String fileUrl = "http://mirrors.sohu.com/centos/7.3.1611/isos/x86_64/CentOS-7-x86_64-DVD-1611.iso";
//
////将文件下载后保存在E盘，返回结果为下载文件大小
////        long size = HttpUtil.downloadFile(fileUrl, FileUtil.file("e:/"));
////        System.out.println("Download size: " + size);
//
//        //带进度显示的文件下载
//        HttpUtil.downloadFile(fileUrl, FileUtil.file("e:/"), new StreamProgress() {
//
//            @Override
//            public void start() {
//                Console.log("开始下载。。。。");
//            }
//
//            @Override
//            public void progress(long progressSize) {
//                Console.log("已下载：{}", FileUtil.readableFileSize(progressSize));
//            }
//
//            @Override
//            public void finish() {
//                Console.log("下载完成！");
//            }
//        });
//    }
//
//    @Test
//    public void testHutool_up() {
//        HashMap<String, Object> paramMap = new HashMap<>();
////文件上传只需将参数中的键指定（默认file），值设为文件对象即可，对于使用者来说，文件上传与普通表单提交并无区别
//        paramMap.put("file", FileUtil.file("D:\\qrcode.jpg"));
//
//        String result = HttpUtil.post("https://www.baidu.com", paramMap);
//
//    }
//
//    @Test
//    public void testHutool_webservice() {
//        // 新建客户端
//        SoapClient client = SoapClient.create("http://www.webxml.com.cn/WebServices/IpAddressSearchWebService.asmx")
//                // 设置要请求的方法，此接口方法前缀为web，传入对应的命名空间
//                .setMethod("web:getCountryCityByIp", "http://WebXml.com.cn/")
//                // 设置参数，此处自动添加方法的前缀：web
//                .setParam("theIpAddress", "218.21.240.106");
//
//        // 发送请求，参数true表示返回一个格式化后的XML内容
//        // 返回内容为XML字符串，可以配合XmlUtil解析这个响应
//        Console.log(client.send(true));
//    }
//}

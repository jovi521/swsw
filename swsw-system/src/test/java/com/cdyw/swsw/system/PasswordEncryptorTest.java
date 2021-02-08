package com.cdyw.swsw.system;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用来获取加密后密码的测试类
 *
 * @author jovi
 */
@SpringBootTest
public class PasswordEncryptorTest {

    @Autowired
    PasswordListBean passwordListBean;

    @Autowired
    private StringEncryptor passwordEncryptorBean;

    @Value(value = "${spring.datasource.druid.password}")
    private String mysqlOriginPswd;

    @Test
    public void testPasswordEncryptor() {
        System.out.println(mysqlOriginPswd);
        // 加密
        String mysqlEncryptedPswd = encrypt(passwordListBean.getMysqlOriginPswd());

        String druidEncryptedPswd = encrypt(passwordListBean.getDruidOriginPswd());

        String redisEncryptedPswd = encrypt(passwordListBean.getRedisOriginPswd());

        String mailEncryptedPswd = encrypt(passwordListBean.getMailOriginPswd());

        // 打印加密前后的结果对比
        System.out.println("MySQL原始明文密码为：" + passwordListBean.getMysqlOriginPswd());
        System.out.println("Druid登录原始明文密码为：" + passwordListBean.getDruidOriginPswd());
        System.out.println("Redis原始明文密码为：" + passwordListBean.getRedisOriginPswd());
        System.out.println("邮件原始STMP密码为：" + passwordListBean.getMailOriginPswd());
        System.out.println("====================================");
        System.out.println("MySQL原始明文密码加密后的结果为：" + mysqlEncryptedPswd);
        System.out.println("Druid登录原始明文密码加密后的结果为：" + druidEncryptedPswd);
        System.out.println("Redis原始明文密码加密后的结果为：" + redisEncryptedPswd);
        System.out.println("邮件原始STMP密码加密后的结果为：" + mailEncryptedPswd);
    }

    /**
     * 加密
     *
     * @param originPassord
     * @return
     */
    private String encrypt(String originPassord) {
        return passwordEncryptorBean.encrypt(originPassord);
    }

    /**
     * 解密
     *
     * @param encryptedPswd
     * @return
     */
    private String decrypt(String encryptedPswd) {
        return passwordEncryptorBean.decrypt(encryptedPswd);
    }
}

package com.cdyw.swsw.data.pwd;

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
    PasswordListComponent passwordListComponent;

    @Autowired
    private StringEncryptor passwordEncryptorBean;

    @Value(value = "${spring.datasource.druid.password}")
    private String mysqlOriginPswd;

    @Test
    public void testPasswordEncryptor() {
        System.out.println(mysqlOriginPswd);
        // 加密
        String mysqlEncryptedPswd = encrypt(passwordListComponent.getMysqlOriginPswd());

        String druidEncryptedPswd = encrypt(passwordListComponent.getDruidOriginPswd());

        String redisEncryptedPswd = encrypt(passwordListComponent.getRedisOriginPswd());

        String mailEncryptedPswd = encrypt(passwordListComponent.getMailOriginPswd());

        // 打印加密前后的结果对比
        System.out.println("MySQL原始明文密码为：" + passwordListComponent.getMysqlOriginPswd());
        System.out.println("Druid登录原始明文密码为：" + passwordListComponent.getDruidOriginPswd());
        System.out.println("Redis原始明文密码为：" + passwordListComponent.getRedisOriginPswd());
        System.out.println("Mail原始STMP密码为：" + passwordListComponent.getMailOriginPswd());
        System.out.println("====================================");
        System.out.println("MySQL原始明文密码加密后的结果为：" + mysqlEncryptedPswd);
        System.out.println("Druid登录原始明文密码加密后的结果为：" + druidEncryptedPswd);
        System.out.println("Redis原始明文密码加密后的结果为：" + redisEncryptedPswd);
        System.out.println("Mail原始STMP密码加密后的结果为：" + mailEncryptedPswd);
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

package com.cdyw.swsw.system.controller.systemuser;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.security.constant.Constants;
import com.cdyw.swsw.system.security.util.Base64;
import com.cdyw.swsw.system.security.util.IdUtils;
import com.cdyw.swsw.system.security.util.RedisCache;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 *
 * @author ruoyi
 */
@RestController
public class CaptchaController {

    private final RedisCache redisCache;

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    /**
     * 验证码类型
     */
    @Value("${swsw.captchaType}")
    private String captchaType;

    @Autowired
    public CaptchaController(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public CommonResult<?> getCode(HttpServletResponse response) {
        String captchaTypeChar = "char";
        String captchaTypeMath = "math";
        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        String capStr, code = null;
        BufferedImage image = null;

        // 生成验证码
        if (captchaTypeMath.equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if (captchaTypeChar.equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            assert image != null;
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return CommonResult.failed(e.getMessage());
        }
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("uuid", uuid);
        resultMap.put("img", Base64.encode(os.toByteArray()));
        return CommonResult.success(resultMap);
    }
}

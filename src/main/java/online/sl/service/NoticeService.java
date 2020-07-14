package online.sl.service;

import cn.sl.manager.database.tables.records.UserRecord;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import online.sl.config.WeChatConfig;
import online.sl.constant.RedisKey;
import online.sl.exception.ApplicationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.sl.manager.database.tables.User.USER;

@Service
@Slf4j
public class NoticeService {


    private WeChatConfig weChatConfig;

    @Autowired
    public void setWeChatConfig(WeChatConfig weChatConfig) {
        this.weChatConfig = weChatConfig;
    }

    private Gson gson;

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static final String GET_WECHAT_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&";

    private static final String SEND_SAME_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=";

    private static final String APPOINT_SEND = "wy54IccSWkC-ErSbDKMHw5MC4xE92xkvKJ59ctEa0Vk";

    // 订单状态
    private static final String ORDER_STATUS = "x5p48UF0N6YqDhZNjropuhhujKoZt1kHPJnrcMrgxkM";

    // 发送验证码
    private static final String VERIFY_CODE = "sVgJnbJ4Uza7PEigO6098BJN4DWCf94bfz6ZUmtpRGg";
    private OkHttpClient okHttpClient;


    @Autowired
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 获取access-token
     */
    public String getAccessToken() {
        String ast = stringRedisTemplate.opsForValue().get(RedisKey.ACCESS_TOKEN_KEY);
        if (StringUtils.isNotBlank(ast)) {
            return ast;
        } else {
            Request request = new Request.Builder()
                    .url(GET_WECHAT_ACCESS_TOKEN_URL + "appid=" + weChatConfig.getAppId() + "&secret=" + weChatConfig.getSecret())
                    .get()
                    .build();
            try {
                Response execute = okHttpClient.newCall(request).execute();
                String string = execute.body().string();
                String access_token = gson.fromJson(string, Result.class).getAccess_token();
                stringRedisTemplate.opsForValue().set(RedisKey.ACCESS_TOKEN_KEY, access_token, 1, TimeUnit.HOURS);
                return gson.fromJson(string, Result.class).getAccess_token();
            } catch (IOException e) {
                log.error("获取token失败", e);
            }
            return null;

        }


    }

    @Data
    public static class Result {
        private String access_token;
    }

    /**
     * 预约通知
     */
    public void sendSameMessage(Integer userId, String startAt, String endAt) {


        UserRecord userRecord = dsl.selectFrom(USER).where(USER.ID.eq(userId))
                .fetchSingle();
        String openId = userRecord.getOpenId();

        String accessToken = getAccessToken();
        WeappTemplateMsg weappTemplateMsg = new WeappTemplateMsg();
        weappTemplateMsg.setTouser(openId);
        weappTemplateMsg.setTemplate_id(APPOINT_SEND);
        weappTemplateMsg.setForm_id("1");
        weappTemplateMsg.setEmphasis_keyword("phrase9");
        weappTemplateMsg.setPage("pages/account/list");
        Map<String, Map<String, String>> data = new HashMap<>();

        Map<String, String> thing5 = new HashMap<>();
        thing5.put("value", "预约成功通知");
        data.put("thing5", thing5);
        Map<String, String> thing2 = new HashMap<>();
        thing2.put("value", "陈集钢厂西门");
        data.put("thing2", thing2);
        Map<String, String> thing22 = new HashMap<>();
        thing22.put("value", startAt);
        data.put("time22", thing22);
        Map<String, String> thing23 = new HashMap<>();
        thing23.put("value", endAt);
        data.put("time23", thing23);
        Map<String, String> thing9 = new HashMap<>();
        thing9.put("value", "预约完成");
        data.put("phrase9", thing9);
        weappTemplateMsg.setData(data);

        String s = gson.toJson(weappTemplateMsg);
        log.info(s);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            Request request = new Request.Builder()
                    .url(SEND_SAME_MESSAGE_URL + accessToken)
                    .post(RequestBody.create(JSON, s))
                    .build();

            Response execute = okHttpClient.newCall(request).execute();

            log.info(execute.body().string());
        } catch (IOException e) {
            log.error("发送通知失败", e);
        }


    }


    @Data
    public static class WeappTemplateMsg {

        private String touser;

        private String template_id;

        private String page;

        private String form_id;

        private Map<String, Map<String, String>> data;

        private String emphasis_keyword;
    }

    /**
     * 订单状态变更
     */

    public void sendOrderStatusMessage(Integer userId, String content, String remark, String dateTime) {

        UserRecord userRecord = dsl.selectFrom(USER).where(USER.ID.eq(userId))
                .fetchSingle();
        String openId = userRecord.getOpenId();
        String accessToken = getAccessToken();
        WeappTemplateMsg weappTemplateMsg = new WeappTemplateMsg();
        weappTemplateMsg.setTouser(openId);
        weappTemplateMsg.setTemplate_id(ORDER_STATUS);
        weappTemplateMsg.setForm_id("1");
        weappTemplateMsg.setEmphasis_keyword("thing5");
        weappTemplateMsg.setPage("pages/account/list");
        Map<String, Map<String, String>> data = new HashMap<>();

        Map<String, String> thing1 = new HashMap<>();
        thing1.put("value", content);
        data.put("thing1", thing1);
        Map<String, String> date10 = new HashMap<>();
        date10.put("value", dateTime);
        data.put("date10", date10);
        Map<String, String> thing5 = new HashMap<>();
        thing5.put("value", remark);
        data.put("thing5", thing5);
        weappTemplateMsg.setData(data);
        String s = gson.toJson(weappTemplateMsg);
        log.info(s);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(SEND_SAME_MESSAGE_URL + accessToken)
                .post(RequestBody.create(JSON, s))
                .build();
        try {
            Response execute = okHttpClient.newCall(request).execute();

            log.info(execute.body().string());
        } catch (IOException e) {
            log.error("发送通知失败", e);
        }


    }

    // verifyCode
    public void sendVerifyCode(String phone) {
        log.info("发送验证码");
        UserRecord userRecord = dsl.selectFrom(USER).where(USER.PHONE.eq(phone))
                .fetchOne();
        if (userRecord == null) {
            throw new ApplicationException("账号不存在，请先注册");
        } else {
            if (StringUtils.isBlank(userRecord.getOpenId())) {
                throw new ApplicationException("手机号已于微信解绑，无法发送验证码，请联系客服进行密码重置");
            }
        }
        String openId = userRecord.getOpenId();
        String accessToken = getAccessToken();
        WeappTemplateMsg weappTemplateMsg = new WeappTemplateMsg();
        weappTemplateMsg.setTouser(openId);
        weappTemplateMsg.setTemplate_id(VERIFY_CODE);
        weappTemplateMsg.setForm_id("1");
        weappTemplateMsg.setEmphasis_keyword("character_string1");
        Map<String, Map<String, String>> data = new HashMap<>();
        // 随机码
        String code = RandomStringUtils.randomNumeric(6);
        Map<String, String> thing1 = new HashMap<>();
        thing1.put("value", code);
        data.put("character_string1", thing1);
        Map<String, String> date10 = new HashMap<>();
        date10.put("value", userRecord.getPhone());
        data.put("character_string2", date10);
        weappTemplateMsg.setData(data);
        String s = gson.toJson(weappTemplateMsg);
        log.info(s);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(SEND_SAME_MESSAGE_URL + accessToken)
                .post(RequestBody.create(JSON, s))
                .build();

        try {
            Response execute = okHttpClient.newCall(request).execute();
            stringRedisTemplate.opsForValue().set(RedisKey.VERIFY_CODE + userRecord.getId(), code, 5, TimeUnit.MINUTES);
            log.info(execute.body().string());
        } catch (IOException e) {
            log.error("发送验证码失败", e);
        }

    }
}

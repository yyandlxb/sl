package online.sl.service;

import cn.sl.manager.database.tables.records.AccountRecordRecord;
import cn.sl.manager.database.tables.records.DiscountCouponRecord;
import cn.sl.manager.database.tables.records.UserRecord;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import online.sl.config.WeChatConfig;
import online.sl.constant.RedisKey;
import online.sl.domain.User;
import online.sl.exception.ApplicationException;
import online.sl.session.SessionManager;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static cn.sl.manager.database.tables.DiscountCoupon.DISCOUNT_COUPON;
import static cn.sl.manager.database.tables.User.USER;

@Service
@Slf4j
public class UserService {
    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    private OkHttpClient okHttpClient;

    private WeChatConfig weChatConfig;

    private StringRedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Autowired
    public void setWeChatConfig(WeChatConfig weChatConfig) {
        this.weChatConfig = weChatConfig;
    }

    private Gson gson;

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    private SessionManager sessionManager;

    private static final String CODE_TO_SESSION = "https://api.weixin.qq.com/sns/jscode2session";

    private NoticeService noticeService;

    @Autowired
    public void setNoticeService(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Autowired
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void signIn(HttpServletRequest request, String phone, String password, String mpCode) throws IOException {
        UserRecord userRecord = dsl.selectFrom(USER).where(USER.PHONE.eq(phone))
                .fetchOne();
        if (userRecord == null) {
            throw new ApplicationException("用户不存在");
        } else {

            boolean matches = passwordEncoder.matches(password, userRecord.getPassword());
            if (!matches) {
                throw new ApplicationException("密码错误");
            }
//            List<UserRecord> fetch = dsl.selectFrom(USER).where(USER.OPEN_ID.eq(mpCode))
//                    .and(USER.PHONE.ne(phone))
//                    .fetch();
//            if (fetch.size() >= 1) {
//                throw new ApplicationException("此微信绑定过，手机号为：" + fetch.get(0).getPhone() + "。请先解绑！");
//            }
            CodeSession codeSession = getCodeSession(mpCode);
            userRecord.setOpenId(codeSession.getOpenid());
            dsl.executeUpdate(userRecord);
            User user = new User();
            user.setId(userRecord.getId());
            user.setName(userRecord.getName());
            user.setType(userRecord.getType());
            user.setPhone(userRecord.getPhone());
            sessionManager.bind(request.getSession(), user);
        }

    }

    @Transactional
    public boolean createUser(String phone, String name, String password) {

        boolean present = dsl.selectOne().from(USER).where(USER.PHONE.eq(phone)).and(USER.DELETED.isFalse())
                .forUpdate()
                .fetchOptional().isPresent();
        if (present) {
            throw new ApplicationException("用户已存在");
        }
        return dsl.insertInto(USER)
                .set(USER.PHONE, phone)
                .set(USER.NAME, name)
                .set(USER.PASSWORD, passwordEncoder.encode(password))
                .onDuplicateKeyIgnore().execute() > 0;

    }

    public CodeSession getCodeSession(String code) throws IOException {

        String url = CODE_TO_SESSION + "?appid=" + weChatConfig.getAppId() +
                "&secret=" + weChatConfig.getSecret() + "&js_code=" + code + "&grant_type=authorization_code";
        Request request = new Request.Builder()
                .url(url)
                .build();
        log.info("request url = {}", url);
        Response response = okHttpClient.newCall(request).execute();
        return gson.fromJson(response.body().string(), CodeSession.class);
    }

    public String autoSignIn(HttpServletRequest httpServletRequest, String code) throws IOException {

        CodeSession codeSession = getCodeSession(code);
        log.info("result={}", codeSession);
        if (codeSession.errcode == null) {
            String openid = codeSession.getOpenid();
            User userRecord = dsl.selectFrom(USER).where(USER.OPEN_ID.eq(openid))
                    .fetchOneInto(User.class);
            if (userRecord == null) {
                throw new ApplicationException("自动登陆失败，请使用手机号登陆");
            }
            sessionManager.bind(httpServletRequest.getSession(true), userRecord);
            return userRecord.getPhone();
        } else {
            throw new ApplicationException("自动登陆失败，请使用手机号登陆");
        }

    }

    @Transactional
    public void addAccount(String code, Integer id) {

        DiscountCouponRecord discountCouponRecord = dsl.selectFrom(DISCOUNT_COUPON)
                .where(DISCOUNT_COUPON.CODE.eq(code))
                .forUpdate()
                .fetchOne();
        if (discountCouponRecord.getUsed()) {
            throw new ApplicationException("优惠券已被使用");
        } else {
            boolean b = dsl.update(USER).set(USER.ACCOUNT_FEE, USER.ACCOUNT_FEE.add(discountCouponRecord.getFee()))
                    .where(USER.ID.eq(id))
                    .execute() > 0;
            if (b) {
                AccountRecordRecord accountRecordRecord = new AccountRecordRecord();
                accountRecordRecord.setFee(discountCouponRecord.getFee());
                accountRecordRecord.setUserId(id);
                dsl.executeInsert(accountRecordRecord);
            }

        }
    }

    @Transactional
    public void addDisCount(String code, Integer userId) {

        DiscountCouponRecord discountCouponRecord = dsl.selectFrom(DISCOUNT_COUPON)
                .where(DISCOUNT_COUPON.CODE.eq(code))
                .and(DISCOUNT_COUPON.USED.isFalse())
                .forUpdate()
                .fetchOne();
        if (discountCouponRecord == null) {
            throw new ApplicationException("优惠券已被使用");
        } else {
            AccountRecordRecord accountRecordRecord = new AccountRecordRecord();
            accountRecordRecord.setFee(discountCouponRecord.getFee());
            accountRecordRecord.setUserId(userId);
            dsl.executeInsert(accountRecordRecord);
            dsl.update(USER)
                    .set(USER.ACCOUNT_FEE, USER.ACCOUNT_FEE.add(discountCouponRecord.getFee()))
                    .where(USER.ID.eq(userId))
                    .execute();
            discountCouponRecord.setUserId(userId);
            discountCouponRecord.setUsed(true);
            dsl.executeUpdate(discountCouponRecord);
        }
    }

    public void verifyCode(String phone) throws IOException {
        noticeService.sendVerifyCode(phone);
    }

    public void updatePassword(String phone, String code, String password) {

        UserRecord userRecord = dsl.selectFrom(USER).where(USER.PHONE.eq(phone)).fetchOne();
        if (userRecord == null) {
            throw new ApplicationException("用户不存在");
        }
        String s = redisTemplate.opsForValue().get(RedisKey.VERIFY_CODE + phone);
        if (StringUtils.isBlank(s)) {
            throw new ApplicationException("验证码已过期");
        } else if (!s.equals(code)) {
            throw new ApplicationException("验证码不正确");
        }

        userRecord.setPassword(passwordEncoder.encode(password));
        dsl.executeUpdate(userRecord);


    }

    @Data
    private static class CodeSession {

        private String openid;

        private String session_key;

        private String unionid;

        private Integer errcode;

        private String errmsg;

    }

    public void signOut(HttpServletRequest request, Integer userId) {
        request.getSession().invalidate();
        dsl.update(USER).set(USER.OPEN_ID, (String) null).where(USER.ID.eq(userId)).execute();
    }
}

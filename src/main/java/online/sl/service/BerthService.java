package online.sl.service;

import cn.sl.manager.database.tables.records.*;
import lombok.extern.slf4j.Slf4j;
import online.sl.constant.BerthOrderStatus;
import online.sl.constant.FeeEnum;
import online.sl.constant.RedisKey;
import online.sl.constant.UserType;
import online.sl.controller.BerthController;
import online.sl.exception.ApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static cn.sl.manager.database.tables.Berth.BERTH;
import static cn.sl.manager.database.tables.BerthOrder.BERTH_ORDER;
import static cn.sl.manager.database.tables.Fee.FEE;
import static cn.sl.manager.database.tables.User.USER;

@Service
@Slf4j
public class BerthService {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private DSLContext dsl;

    private static final double ONE_MIN = 0.06d; // 15快

    private static final long SIX_HOUR = 360; // 21快

    private static final int HALF_DAY = 720, HALF_FEE = 20; // 20快

    private static final int ONE_DAY = 1440, DAY_FEE = 30; // 30

    private StringRedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    private NoticeService noticeService;

    @Autowired
    public void setNoticeService(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    public void create(BerthController.BerthForm berthForm, Integer id) throws IOException {
        // 查询是否有未支付的订单
        boolean present = dsl.selectFrom(BERTH_ORDER).where(BERTH_ORDER.STATUS.in(BerthOrderStatus.NO_PAY, BerthOrderStatus.AUDIT))
                .and(BERTH_ORDER.USER_ID.eq(id))
                .limit(1)
                .fetchOptional().isPresent();
        if (present) {
            throw new ApplicationException("有订单未审核或未结算，不能预约");
        }
        String sa = berthForm.getStartAt();
        LocalDateTime parse = Timestamp.valueOf(sa).toLocalDateTime();
        LocalDateTime end = Timestamp.valueOf(berthForm.getEndAt()).toLocalDateTime();
        if (parse.compareTo(LocalDateTime.now()) <= 0 || parse.compareTo(end) >= 0) {
            throw new ApplicationException("选择的时间应小于当前时间");
        }

        BerthOrderRecord br = dsl.select(BERTH_ORDER.fields()).from(BERTH_ORDER).innerJoin(BERTH)
                .on(BERTH_ORDER.BERTH_ID.eq(BERTH.ID))
                .where(BERTH_ORDER.STATUS.lessThan(BerthOrderStatus.FINISH))
                .and(BERTH.ID.eq(berthForm.getBerthId()))
                .and(BERTH_ORDER.START_AT.le(parse).and(BERTH_ORDER.END_AT.ge(parse))
                        .or(BERTH_ORDER.START_AT.le(end).and(BERTH_ORDER.END_AT.ge(end))))
                .orderBy(BERTH_ORDER.START_AT.desc())
                .limit(1)
                .forUpdate()
                .fetchOneInto(BerthOrderRecord.class);
        if (br != null) {
            throw new ApplicationException("此床铺在此时间段已被预约,您可在："
                    + br.getStartAt().format(DateTimeFormatter.ofPattern(DATE_FORMAT)) + "之前预约，" + "或在"
                    + br.getEndAt().format(DateTimeFormatter.ofPattern(DATE_FORMAT)) + "之后预约");
        }

        // 预计费用
        BerthOrderRecord berthOrderRecord = new BerthOrderRecord();
        berthOrderRecord.from(berthForm);
        berthOrderRecord.setUserId(id);
        berthOrderRecord.setStartAt(Timestamp.valueOf(berthForm.getStartAt()).toLocalDateTime());
        berthOrderRecord.setEndAt(Timestamp.valueOf(berthForm.getEndAt()).toLocalDateTime());
        berthOrderRecord.setFee(culFee(berthOrderRecord.getStartAt(), berthOrderRecord.getEndAt()));
        boolean b = dsl.executeInsert(berthOrderRecord) > 0;
        if (b) {
            noticeService.sendSameMessage(id, berthOrderRecord.getStartAt()
                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT)), berthOrderRecord.getEndAt()
                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        }

    }

    public BigDecimal culFee(LocalDateTime startAt, LocalDateTime endAt) {
        double minutes = ChronoUnit.MINUTES.between(startAt, endAt);
        if (minutes <= SIX_HOUR) {
            long between = ChronoUnit.MINUTES.between(startAt, endAt);
            return BigDecimal.valueOf(ONE_MIN).multiply(BigDecimal.valueOf(between));
        } else if (minutes <= HALF_DAY) {
            return BigDecimal.valueOf(HALF_FEE);
        } else if (minutes <= ONE_DAY) {
            return BigDecimal.valueOf(DAY_FEE);
        } else {
            double days = ChronoUnit.MINUTES.between(startAt, endAt) / 60d / 24d;
            BigDecimal bigDecimal = BigDecimal.valueOf(days).setScale(0, BigDecimal.ROUND_UP);
            return BigDecimal.valueOf(DAY_FEE).multiply(bigDecimal);
        }
    }

    public void orderLeave(Integer id, Integer userId) {
        BerthOrderRecord berthOrderRecord = dsl.selectFrom(BERTH_ORDER)
                .where(BERTH_ORDER.ID.eq(id))
                .and(BERTH_ORDER.USER_ID.eq(userId))
                .forUpdate()
                .fetchSingle();
        BigDecimal multiply = culFee(berthOrderRecord.getStartAt(), berthOrderRecord.getEndAt());

        UserRecord userRecord = dsl.selectFrom(USER).where(USER.ID.eq(userId))
                .forUpdate()
                .fetchSingle();

        if (userRecord.getAccountFee().compareTo(BigDecimal.valueOf(0)) > 0 &&
                userRecord.getAccountFee().compareTo(multiply) > 0) {
            berthOrderRecord.setStatus(BerthOrderStatus.AUDIT);
            berthOrderRecord.setFee(multiply);
            berthOrderRecord.setLeaveAt(LocalDateTime.now());
            dsl.executeUpdate(berthOrderRecord);
            dsl.select(USER.ID).from(USER)
                    .where(USER.TYPE.eq(UserType.ADMIN))
                    .fetchInto(Integer.class).forEach(e ->
                    noticeService.sendOrderStatusMessage(e, "用户异常离开"
                            , "用户" + userRecord.getPhone() + "提前离开，请快审核", LocalDateTime.now()
                                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
            );


        } else {
            throw new ApplicationException("余额不足，联系添加微信：15736883328 进行充值");
        }

    }

    public void revocation(Integer id, Integer userId) {
        BerthOrderRecord berthOrderRecord = dsl.selectFrom(BERTH_ORDER)
                .where(BERTH_ORDER.ID.eq(id))
                .and(BERTH_ORDER.USER_ID.eq(userId))
                .forUpdate()
                .fetchSingle();
        if (berthOrderRecord.getStatus() != BerthOrderStatus.APPOINTING) {
            throw new ApplicationException("订单不再预约中，不能撤销");
        }
        FeeRecord feeRecord = dsl.selectFrom(FEE).where(FEE.CODE.eq(FeeEnum.BERTH_REVOCATION_FEE.name()))
                .fetchSingle();
        BigDecimal multiply = feeRecord.getFee().multiply(berthOrderRecord.getFee()).setScale(2, BigDecimal.ROUND_UP);

        UserRecord userRecord = dsl.selectFrom(USER).where(USER.ID.eq(userId))
                .forUpdate()
                .fetchSingle();

        if (userRecord.getAccountFee().compareTo(BigDecimal.valueOf(0)) > 0 &&
                userRecord.getAccountFee().compareTo(multiply) > 0) {
            berthOrderRecord.setStatus(BerthOrderStatus.REVOCATION);
            berthOrderRecord.setPracticalFee(multiply);
            dsl.executeUpdate(berthOrderRecord);
            userRecord.setAccountFee(userRecord.getAccountFee().subtract(multiply));
            dsl.executeUpdate(userRecord);
        } else {
            throw new ApplicationException("余额不足，联系添加微信：15736883328 进行充值");
        }
    }

    public void berthOrderUsing() {
        dsl.selectFrom(BERTH_ORDER)
                .where(BERTH_ORDER.STATUS.eq(BerthOrderStatus.APPOINTING))
                .and(BERTH_ORDER.START_AT.le(LocalDateTime.now()))
                .forUpdate()
                .fetch().forEach(e -> {
            e.setStatus(BerthOrderStatus.USING);
            dsl.executeUpdate(e);
        });

    }

    public void berthOrderAudit() {
        try {
            dsl.selectFrom(BERTH_ORDER)
                    .where(BERTH_ORDER.STATUS.eq(BerthOrderStatus.USING))
                    .and(BERTH_ORDER.END_AT.le(LocalDateTime.now()))
                    .forUpdate()
                    .fetch().forEach(e -> {
                e.setStatus(BerthOrderStatus.NO_PAY);
                dsl.executeUpdate(e);
                UserRecord userRecord = dsl.selectFrom(USER).where(USER.ID.eq(e.getUserId())).fetchSingle();
                BerthRecord berthRecord = dsl.selectFrom(BERTH).where(BERTH.ID.eq(e.getBerthId())).fetchSingle();
                dsl.select(USER.ID).from(USER)
                        .where(USER.TYPE.eq(UserType.ADMIN))
                        .fetchInto(Integer.class).forEach(r ->
                        noticeService.sendOrderStatusMessage(r, "用户离开"
                                , "用户" + userRecord.getPhone() + "订单时间已到,床铺为：" + berthRecord.getName(),
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                );

            });
        } catch (Exception e) {
            log.error("发送通知异常", e);
        }


    }

    public void overOrderNotice() {
        try {
            dsl.selectFrom(BERTH_ORDER)
                    .where(BERTH_ORDER.STATUS.eq(BerthOrderStatus.USING))
                    .and(BERTH_ORDER.END_AT.le(LocalDateTime.now().minusMinutes(5)))
                    .fetch().forEach(e -> {
                String rank = redisTemplate.opsForValue().get(RedisKey.NOTICE_ORDER_STATUS_KEY + e.getId());
                log.info("redis中的数据{}", rank);
                if (StringUtils.isBlank(rank)) {
                    log.info("订单专题变更");
                    noticeService.sendOrderStatusMessage(e.getUserId(), "床铺订单状态通知", "订单即将结束，如需继续请添加时长或预约新订单",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
                    redisTemplate.opsForValue().set(RedisKey.NOTICE_ORDER_STATUS_KEY + e.getId(), String.valueOf(e.getId()), 6, TimeUnit.MINUTES);
                }

            });
        } catch (Exception e) {
            log.error("通知异常", e);
        }


    }


    public void addTime(Integer id, Integer time) {
        BerthOrderRecord berthOrderRecord = dsl.selectFrom(BERTH_ORDER).where(BERTH_ORDER.ID.eq(id))
                .forUpdate()
                .fetchSingle();
        if (berthOrderRecord.getStatus() == BerthOrderStatus.FINISH) {
            throw new ApplicationException("订单已结束");
        }
        // 查询时间是否冲突

        LocalDateTime localDateTime = berthOrderRecord.getEndAt().plusMinutes(time);
        BerthOrderRecord b = dsl.select(BERTH_ORDER.fields()).from(BERTH_ORDER).innerJoin(BERTH)
                .on(BERTH_ORDER.BERTH_ID.eq(BERTH.ID))
                .where(BERTH_ORDER.BERTH_ID.eq(berthOrderRecord.getBerthId()))
                .and(BERTH_ORDER.START_AT.le(localDateTime))
                .and(BERTH_ORDER.END_AT.ge(localDateTime)).orderBy(BERTH_ORDER.START_AT.desc())
                .limit(1)
                .forUpdate()
                .fetchOneInto(BerthOrderRecord.class);
        if (b != null) {
            throw new ApplicationException("此床铺可以续约值：" + b.getStartAt().format(DateTimeFormatter.ISO_DATE_TIME));
        } else {
            // 续约
            berthOrderRecord.setEndAt(localDateTime);
            dsl.executeUpdate(berthOrderRecord);
        }
    }


    @Transactional
    public void pay(Integer id, Integer userId) {

        BerthOrderRecord berthOrderRecord = dsl.selectFrom(BERTH_ORDER).where(BERTH_ORDER.ID.eq(id))
                .forUpdate()
                .fetchSingle();
        UserRecord userRecord = dsl.selectFrom(USER).where(USER.ID.eq(userId)).forUpdate().fetchSingle();
        if (berthOrderRecord.getStatus() == BerthOrderStatus.NO_PAY &&
                userRecord.getAccountFee().compareTo(berthOrderRecord.getFee()) > 0) {
            AccountRecordRecord accountRecordRecord = new AccountRecordRecord();
            accountRecordRecord.setUserId(userId);
            accountRecordRecord.setFee(berthOrderRecord.getFee().negate());
            dsl.executeInsert(accountRecordRecord);
            userRecord.setAccountFee(userRecord.getAccountFee().subtract(berthOrderRecord.getFee()));
            berthOrderRecord.setStatus(BerthOrderStatus.FINISH);
            berthOrderRecord.setPracticalFee(berthOrderRecord.getFee());
            dsl.executeUpdate(userRecord);
            dsl.executeUpdate(berthOrderRecord);
        } else {
            throw new ApplicationException("余额不足，请联系：15736883328 进行充值");
        }
    }
}

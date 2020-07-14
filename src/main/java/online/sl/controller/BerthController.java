package online.sl.controller;

import cn.sl.manager.database.tables.records.BerthOrderRecord;
import cn.sl.manager.database.tables.records.BerthRecord;
import cn.sl.manager.database.tables.records.FeeRecord;
import lombok.Data;
import lombok.NonNull;
import online.sl.constant.FeeEnum;
import online.sl.constant.UserType;
import online.sl.domain.User;
import online.sl.service.BerthService;
import online.sl.session.Authenticated;
import online.sl.util.Reply;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static cn.sl.manager.database.Tables.USER;
import static cn.sl.manager.database.tables.Berth.BERTH;
import static cn.sl.manager.database.tables.BerthOrder.BERTH_ORDER;
import static cn.sl.manager.database.tables.Fee.FEE;

@RestController
@RequestMapping("/berth")
public class BerthController {

    private BerthService berthService;

    @Autowired
    public void setBerthService(BerthService berthService) {
        this.berthService = berthService;
    }

    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/order/getCul")
    public Reply getCul(String startAt, String endAt) {

        BigDecimal bigDecimal = berthService.culFee(Timestamp.valueOf(startAt).toLocalDateTime(),
                Timestamp.valueOf(endAt).toLocalDateTime());

        return Reply.success().data(bigDecimal);

    }

    @PostMapping("/order/pay")
    public Reply pay(@RequestParam Integer id, @Authenticated User user) {

        berthService.pay(id, user.getId());

        return Reply.success();

    }


    @PostMapping("/order/add_time")
    public Reply addTime(@RequestParam Integer id, @RequestParam Integer time) {

        berthService.addTime(id, time);

        return Reply.success();
    }


    @GetMapping("/order/revocation/fee")
    public Reply getBerthRevocationFee(Integer id, @Authenticated User user) {
        BerthOrderRecord berthOrderRecord = dsl.selectFrom(BERTH_ORDER)
                .where(BERTH_ORDER.ID.eq(id))
                .fetchSingle();
        FeeRecord feeRecord = dsl.selectFrom(FEE)
                .where(FEE.CODE.eq(FeeEnum.BERTH_REVOCATION_FEE.name()))
                .fetchSingle();
        BigDecimal multiply = feeRecord.getFee().multiply(berthOrderRecord.getFee()).setScale(2, BigDecimal.ROUND_UP);

        return Reply.success().data(multiply);
    }

    @PostMapping("/order/revocation")
    public Reply revocation(@RequestParam Integer id, @Authenticated User user) {

        berthService.revocation(id, user.getId());

        return Reply.success();

    }

    @PostMapping("/order/leave")
    public Reply orderLeave(@RequestParam Integer id, @Authenticated User user) {

        berthService.orderLeave(id, user.getId());

        return Reply.success();

    }

    @GetMapping("/cul_fee")
    public Reply list(Integer id, @Authenticated User user) {

        BerthOrderRecord berthOrderRecord = dsl.selectFrom(BERTH_ORDER).where(BERTH_ORDER.ID.eq(id))
                .fetchSingle();
        BigDecimal bigDecimal = berthService.culFee(berthOrderRecord.getStartAt(), LocalDateTime.now());

        return Reply.success().data(bigDecimal);

    }

    @GetMapping("/list")
    public Reply berthList(@Authenticated User user) {
        List<BerthRecord> fetch = dsl.selectFrom(BERTH).where(BERTH.DELETED.isFalse())
                .fetch();
        return Reply.success().data(fetch);

    }

    @GetMapping("/order/list")
    public Reply list(@Authenticated User user) {
        List<Condition> conditions = new ArrayList<>();
        if (user.getType() != UserType.ADMIN){
            conditions.add(BERTH_ORDER.USER_ID.eq(user.getId()));
        }

        List<BerthOrderView> berthName = dsl.select(BERTH_ORDER.fields())
                .select(BERTH.NAME.as("berthName"), USER.PHONE.as("phone"))
                .from(BERTH_ORDER).innerJoin(BERTH)
                .on(BERTH_ORDER.BERTH_ID.eq(BERTH.ID))
                .innerJoin(USER)
                .on(USER.ID.eq(BERTH_ORDER.USER_ID))
                .where(conditions)
                .orderBy(BERTH_ORDER.CREATED_AT.desc())
//                .limit((int) pageable.getOffset(), pageable.getPageSize())
                .fetchInto(BerthOrderView.class);

        berthName.forEach(e -> {
            if (e.getStartAt() != null) {
                LocalDateTime localDateTime = e.getStartAt().toLocalDateTime();
                e.setStartDate(localDateTime.toLocalDate().format(DateTimeFormatter.ISO_DATE));
                e.setStartTime(localDateTime.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
            }
            Timestamp endAt = e.getEndAt();

            if (endAt != null) {
                e.setEndDate(endAt.toLocalDateTime().toLocalDate().format(DateTimeFormatter.ISO_DATE));
                e.setEndTime(endAt.toLocalDateTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
            }

            LocalDateTime leaveAt = e.getLeaveAt();
            if (leaveAt != null){
                e.setLeaveDate(leaveAt.toLocalDate().format(DateTimeFormatter.ISO_DATE));
                e.setLeaveTime(leaveAt.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
            }

        });

        return Reply.success().data(berthName);

    }

    @Data
    private static class BerthOrderView {

        private Integer id;

        private Timestamp startAt;

        private Timestamp endAt;

        private String startDate;

        private String startTime;

        private String endTime;

        private String endDate;


        private BigDecimal fee;

        private String berthName;

        private Byte status;

        private BigDecimal practicalFee;

        private String leaveDate;

        private String leaveTime;

        private LocalDateTime leaveAt;

        private String phone;

    }

    @PostMapping("/order/create")
    public Reply create(@Validated BerthForm berthForm, @Authenticated User user) throws IOException {
        berthService.create(berthForm, user.getId());

        return Reply.success();

    }


    @Data
    public static class BerthForm {

        @NonNull
        private String startAt;
        @NonNull
        private String endAt;
        @NonNull
        private Integer berthId;
    }
}

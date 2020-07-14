package online.sl.admin.service;

import cn.sl.manager.database.tables.records.BerthOrderRecord;
import cn.sl.manager.database.tables.records.DiscountCouponRecord;
import cn.sl.manager.database.tables.records.UserRecord;
import online.sl.constant.BerthOrderStatus;
import online.sl.exception.ApplicationException;
import online.sl.service.BerthService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.UUID;

import static cn.sl.manager.database.tables.BerthOrder.BERTH_ORDER;
import static cn.sl.manager.database.tables.User.USER;

@Service
public class AuditService {

    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    private BerthService berthService;

    @Autowired
    public void setBerthService(BerthService berthService) {
        this.berthService = berthService;
    }

    @Transactional
    public void audit(Integer id, boolean audit) {
        BerthOrderRecord berthOrderRecord = dsl.selectFrom(BERTH_ORDER)
                .where(BERTH_ORDER.ID.eq(id))
                .forUpdate()
                .fetchSingle();
        if (berthOrderRecord.getStatus() != BerthOrderStatus.AUDIT) {
            throw new ApplicationException("订单状态不正确");
        }

        if (audit) {
            berthOrderRecord.setStatus(BerthOrderStatus.FINISH);
            BigDecimal multiply = berthOrderRecord.getPracticalFee();
            UserRecord userRecord = dsl.selectFrom(USER).where(USER.ID.eq(berthOrderRecord.getUserId()))
                    .forUpdate()
                    .fetchSingle();
            if (userRecord.getAccountFee().compareTo(BigDecimal.valueOf(0)) > 0 &&
                    userRecord.getAccountFee().compareTo(multiply) > 0) {
                berthOrderRecord.setStatus(BerthOrderStatus.FINISH);
                berthOrderRecord.setPracticalFee(multiply);
                dsl.executeUpdate(berthOrderRecord);
                userRecord.setAccountFee(userRecord.getAccountFee().subtract(multiply));
                dsl.executeUpdate(userRecord);
            } else {
                berthOrderRecord.setPracticalFee(multiply);
                berthOrderRecord.setStatus(BerthOrderStatus.NO_PAY);
                dsl.executeUpdate(berthOrderRecord);
            }
        } else {
//            BigDecimal fee = berthService.culFee(berthOrderRecord.getStartAt(), berthOrderRecord.getEndAt());
//            berthOrderRecord.setPracticalFee(fee);
            berthOrderRecord.setStatus(BerthOrderStatus.USING);
            dsl.executeUpdate(berthOrderRecord);
        }

    }

    public void addDiscount(BigDecimal amount) {

        DiscountCouponRecord discountCouponRecord = new DiscountCouponRecord();
        discountCouponRecord.setCode(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()).toString());
        discountCouponRecord.setFee(amount);
        dsl.executeInsert(discountCouponRecord);
    }
}

package online.sl.admin.service;

import cn.sl.manager.database.tables.AccountRecord;
import cn.sl.manager.database.tables.records.AccountCheckoutRecord;
import cn.sl.manager.database.tables.records.AccountRecordRecord;
import lombok.extern.slf4j.Slf4j;
import online.sl.constant.AccountCheckoutStatus;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static cn.sl.manager.database.tables.AccountCheckout.ACCOUNT_CHECKOUT;
import static cn.sl.manager.database.tables.User.USER;

@Service
@Slf4j
public class AdminAccountService {

    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public void applyConfirm(Integer id, boolean audit, String remark){

        if (audit){
            AccountCheckoutRecord accountCheckoutRecord = dsl.selectFrom(ACCOUNT_CHECKOUT).where(ACCOUNT_CHECKOUT.ID.eq(id))
                    .fetchSingle();
            boolean b = dsl.update(ACCOUNT_CHECKOUT)
                    .set(ACCOUNT_CHECKOUT.STATUS, AccountCheckoutStatus.PAY)
                    .where(ACCOUNT_CHECKOUT.ID.eq(id))
                    .and(ACCOUNT_CHECKOUT.STATUS.eq(AccountCheckoutStatus.WAIT_PAY))
                    .and(ACCOUNT_CHECKOUT.FEE.eq(accountCheckoutRecord.getFee()))
                    .execute() > 0;
            if(b){
                log.info("确认提现成功，id={}", id);
                AccountRecordRecord accountRecordRecord = new AccountRecordRecord();
                accountRecordRecord.setFee(accountCheckoutRecord.getFee().multiply(BigDecimal.valueOf(-1)));
                accountRecordRecord.setUserId(accountCheckoutRecord.getUserId());
                accountRecordRecord.setRemark("主动提现");
                dsl.executeInsert(accountRecordRecord);
                dsl.update(USER).set(USER.ACCOUNT_FEE, USER.ACCOUNT_FEE.minus(accountCheckoutRecord.getFee()))
                        .where(USER.ID.eq(accountCheckoutRecord.getUserId())).execute();
            }
        }else {
            dsl.update(ACCOUNT_CHECKOUT)
                    .set(ACCOUNT_CHECKOUT.STATUS, AccountCheckoutStatus.REJECT)
                    .set(ACCOUNT_CHECKOUT.REMARK, remark)
                    .where(ACCOUNT_CHECKOUT.ID.eq(id)).execute();
        }
    }
}

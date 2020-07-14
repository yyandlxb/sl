package online.sl.service;

import cn.sl.manager.database.tables.records.AccountCheckoutRecord;
import cn.sl.manager.database.tables.records.BankRecord;
import cn.sl.manager.database.tables.records.UserRecord;
import online.sl.constant.AccountCheckoutStatus;
import online.sl.exception.ApplicationException;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static cn.sl.manager.database.tables.AccountCheckout.ACCOUNT_CHECKOUT;
import static cn.sl.manager.database.tables.Bank.BANK;
import static cn.sl.manager.database.tables.User.USER;

@Service
public class AccountService {

    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public void apply(Integer id, BigDecimal fee, Integer bankId) {

        UserRecord userRecord = dsl.selectFrom(USER).where(USER.ID.eq(id))
                .forUpdate()
                .fetchSingle();
        BankRecord bankRecord = dsl.selectFrom(BANK).where(BANK.ID.eq(bankId)).fetchOne();
        if (bankRecord == null) {
            throw new ApplicationException("银行不存在");
        }

        if (userRecord.getAccountFee().compareTo(fee) > 0 && fee.compareTo(BigDecimal.valueOf(0)) > 0) {

            AccountCheckoutRecord accountCheckoutRecord = new AccountCheckoutRecord();
            accountCheckoutRecord.setUserId(id);
            accountCheckoutRecord.setFee(fee);
            accountCheckoutRecord.setUsername(bankRecord.getUserName());
            accountCheckoutRecord.setCode(bankRecord.getCode());
            accountCheckoutRecord.setBankId(bankId);
            dsl.executeInsert(accountCheckoutRecord);

        } else {
            throw new ApplicationException("余额应小于提现金额");
        }

    }

    @Transactional
    public void update(Integer id, BigDecimal fee, Integer bankId, Integer userId) {

        AccountCheckoutRecord accountCheckoutRecord = dsl.selectFrom(ACCOUNT_CHECKOUT)
                .where(ACCOUNT_CHECKOUT.ID.eq(id))
                .and(ACCOUNT_CHECKOUT.STATUS.eq(AccountCheckoutStatus.WAIT_PAY))
                .and(ACCOUNT_CHECKOUT.USER_ID.eq(userId))
                .fetchOne();

        if (accountCheckoutRecord == null) {
            throw new ApplicationException("申请信息状态不正确");
        }

        UserRecord userRecord = dsl.selectFrom(USER).where(USER.ID.eq(userId))
                .forUpdate()
                .fetchSingle();

        BankRecord bankRecord = dsl.selectFrom(BANK).where(BANK.ID.eq(bankId)).fetchOne();
        if (bankRecord == null) {
            throw new ApplicationException("银行不存在");
        }

        if (userRecord.getAccountFee().compareTo(fee) > 0 && fee.compareTo(BigDecimal.valueOf(0)) > 0) {
            accountCheckoutRecord.setFee(fee);
            accountCheckoutRecord.setUsername(bankRecord.getUserName());
            accountCheckoutRecord.setCode(bankRecord.getCode());
            dsl.executeUpdate(accountCheckoutRecord);

        } else if(fee.compareTo(BigDecimal.valueOf(0)) <= 0){
            throw new ApplicationException("提现金额应大于0");
        }else {
            throw new ApplicationException("余额小于提现金额");
        }

    }
}

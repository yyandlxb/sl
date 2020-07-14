package online.sl.service;

import cn.sl.manager.database.tables.records.BankRecord;
import online.sl.controller.BankController;
import online.sl.exception.ApplicationException;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.sl.manager.database.tables.Bank.BANK;

@Service
public class BankService {

    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public void create(BankController.BankForm bankForm, Integer id) {
        boolean present = dsl.selectOne().from(BANK)
                .where(BANK.CODE.eq(bankForm.getCode()))
                .forUpdate()
                .fetchOptional().isPresent();
        if (present){
            throw new ApplicationException("卡号已存在");
        }
        BankRecord bankRecord = new BankRecord();
        bankRecord.from(bankForm);
        bankRecord.setUserId(id);
        dsl.executeInsert(bankRecord);
    }
    @Transactional
    public boolean update(BankController.BankForm bankForm, Integer id){

        boolean present = dsl.selectOne().from(BANK)
                .where(BANK.CODE.eq(bankForm.getCode()))
                .and(BANK.ID.ne(id))
                .forUpdate()
                .fetchOptional().isPresent();
        if (present){
            throw new ApplicationException("卡号已存在");
        }
        BankRecord bankRecord = new BankRecord();
        bankRecord.from(bankForm);
        bankRecord.setId(id);
        return dsl.executeUpdate(bankRecord) > 0;

    }

    @Transactional
    public boolean delete(Integer id){
        return dsl.deleteFrom(BANK).where(BANK.ID.eq(id)).execute() > 0;
    }
}

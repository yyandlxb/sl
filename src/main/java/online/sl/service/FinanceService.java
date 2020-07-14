package online.sl.service;

import cn.sl.manager.database.tables.records.FinanceBookRecord;
import lombok.Data;
import lombok.NonNull;
import online.sl.exception.ApplicationException;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static cn.sl.manager.database.tables.Goods.GOODS;
import static cn.sl.manager.database.tables.Motorcade.MOTORCADE;

@Service
public class FinanceService {


    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    public void create(FinanceForm financeForm, Integer userId){


        boolean present = dsl.selectOne().from(GOODS)
                .where(GOODS.ID.eq(financeForm.getGoodsId()))
                .fetchOptional().isPresent();
        if (!present){
            throw new ApplicationException("货物名不存在");
        }
        boolean p = dsl.selectOne().from(MOTORCADE)
                .where(MOTORCADE.ID.eq(financeForm.getMotorcadeId()))
                .fetchOptional().isPresent();
        if (!p){
            throw new ApplicationException("车队不存在");
        }

        FinanceBookRecord financeBookRecord = new FinanceBookRecord();
        financeBookRecord.from(financeForm);
        financeBookRecord.setUserId(userId);
        dsl.executeInsert(financeBookRecord);


    }

    @Data
    public static class FinanceForm{

        @NonNull
        private String code;

        @NonNull
        private Integer goodsId;

        private Integer motorcadeId;

        private BigDecimal sendWight;

        private String senderName;

        private String receiverName;

    }
}

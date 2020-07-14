package online.sl.admin.service;

import online.sl.constant.BerthOrderStatus;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cn.sl.manager.database.tables.BerthOrder.BERTH_ORDER;

@Service
public class AdminBerthService {

    private DSLContext dsl;


    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    public boolean deleteBerthOrder(Integer id) {
        return dsl.update(BERTH_ORDER).set(BERTH_ORDER.STATUS, BerthOrderStatus.DELETED)
                .where(BERTH_ORDER.ID.eq(id))
                .and(BERTH_ORDER.STATUS.lessThan(BerthOrderStatus.FINISH))
                .execute() > 0;
    }

}

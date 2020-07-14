package online.sl.controller;

import cn.sl.manager.database.tables.records.BankRecord;
import lombok.Data;
import lombok.NonNull;
import online.sl.domain.User;
import online.sl.service.BankService;
import online.sl.session.Authenticated;
import online.sl.util.Reply;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static cn.sl.manager.database.tables.Bank.BANK;

@RestController
@RequestMapping("/bank")
public class BankController {


    private BankService bankService;

    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/create")
    public Reply create(@Valid BankForm bankForm, @Authenticated User user) {

        bankService.create(bankForm, user.getId());

        return Reply.success();

    }

    @PostMapping("/update")
    public Reply update(@Valid BankForm bankForm, Integer id, @Authenticated User user) {

        bankService.update(bankForm, id);

        return Reply.success();

    }

    @PostMapping("/delete")
    public Reply create(@RequestParam Integer id, @Authenticated User user) {

        bankService.delete(id);

        return Reply.success();

    }

    @GetMapping("/list")
    public Reply list(@Authenticated User user) {

        List<BankRecord> fetch = dsl.selectFrom(BANK).where(BANK.USER_ID.eq(user.getId()))
                .fetch();

        return Reply.success().data(fetch);

    }

    @GetMapping("/detail")
    public Reply detail(@Authenticated User user, Integer bankId) {
        BankRecord bankRecord = dsl.selectFrom(BANK)
                .where(BANK.ID.eq(bankId)).fetchOne();
        return Reply.success().data(bankRecord);

    }


    @Data
    public static class BankForm {

        @NonNull
        private String name;

        @NonNull
        private String userName;

        @NonNull
        private String code;

    }

}

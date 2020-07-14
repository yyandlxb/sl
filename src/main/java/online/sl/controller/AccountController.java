package online.sl.controller;

import lombok.Data;
import online.sl.domain.User;
import online.sl.service.AccountService;
import online.sl.session.Authenticated;
import online.sl.util.Page;
import online.sl.util.Reply;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static cn.sl.manager.database.tables.AccountCheckout.ACCOUNT_CHECKOUT;
import static cn.sl.manager.database.tables.AccountRecord.ACCOUNT_RECORD;
import static cn.sl.manager.database.tables.Bank.BANK;

@RestController("slAccountController")
@RequestMapping("/account")
public class AccountController {

    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    private AccountService accountService;

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/record/list")
    public Reply accountList(@Authenticated User user, Pageable pageable) {
        Integer count = dsl.selectCount()
                .from(ACCOUNT_RECORD).where(ACCOUNT_RECORD.USER_ID.eq(user.getId()))
                .fetchOne().value1();
        List<AccountRecord> fetch = dsl.select(ACCOUNT_RECORD.fields())
                .from(ACCOUNT_RECORD)
                .where(ACCOUNT_RECORD.USER_ID.eq(user.getId()))
                .limit((int) pageable.getOffset(), pageable.getPageSize())
                .fetchInto(AccountRecord.class);

        return Reply.success().data(new Page<>(fetch, pageable, count));
    }

    @Data
    public static class AccountRecord {

        private Integer id;

        private BigDecimal fee;

        private Timestamp createdAt;

        private String remark;

    }

    @GetMapping("/checkout/list")
    public Reply checkoutList(@Authenticated User user, Pageable pageable) {

        Integer count = dsl.selectCount()
                .from(ACCOUNT_CHECKOUT).where(ACCOUNT_CHECKOUT.USER_ID.eq(user.getId()))
                .fetchOne().value1();
        List<CheckoutForm> fetch = dsl.select(ACCOUNT_CHECKOUT.fields())
                .select(BANK.NAME, BANK.USER_NAME, BANK.CODE)
                .from(ACCOUNT_CHECKOUT)
                .innerJoin(BANK)
                .on(BANK.ID.eq(ACCOUNT_CHECKOUT.BANK_ID))
                .where(ACCOUNT_CHECKOUT.USER_ID.eq(user.getId()))
                .limit((int) pageable.getOffset(), pageable.getPageSize())
                .fetchInto(CheckoutForm.class);


        return Reply.success().data(new Page<>(fetch, pageable, count));
    }

    @Data
    public static class CheckoutForm {

        private Integer id;

        private Timestamp createAt;

        private BigDecimal fee;

        private String remark;

        private Byte status;

        private String code;

        private String userName;

        private String name;
    }

    @PostMapping("/checkout/apply")
    public Reply checkout(@Authenticated User user, @RequestParam BigDecimal fee, Integer bankId) {

        accountService.apply(user.getId(), fee, bankId);

        return Reply.success();
    }

    @PostMapping("/checkout/update")
    public Reply update(@Authenticated User user, @RequestParam BigDecimal fee, @RequestParam Integer id,
                        @RequestParam Integer bankId) {

        accountService.update(id, fee, bankId, user.getId());

        return Reply.success();
    }

    @GetMapping("/checkout/detail")
    public Reply checkoutDetail(@Authenticated User user, @RequestParam Integer id) {

        CheckoutView checkoutView = dsl.selectFrom(ACCOUNT_CHECKOUT).where(ACCOUNT_CHECKOUT.ID.eq(id))
                .and(ACCOUNT_CHECKOUT.USER_ID.eq(user.getId()))
                .fetchSingleInto(CheckoutView.class);
        return Reply.success().data(checkoutView);
    }

    @Data
    public static class CheckoutView {

        private Integer id;

        private String code;

        private String userName;

        private BigDecimal fee;

        private Integer bankId;
    }

}

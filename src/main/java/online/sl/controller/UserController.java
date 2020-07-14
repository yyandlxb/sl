package online.sl.controller;

import cn.sl.manager.database.tables.records.DiscountCouponRecord;
import cn.sl.manager.database.tables.records.UserRecord;
import lombok.extern.slf4j.Slf4j;
import online.sl.domain.User;
import online.sl.service.UserService;
import online.sl.session.Authenticated;
import online.sl.util.Reply;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static cn.sl.manager.database.tables.DiscountCoupon.DISCOUNT_COUPON;
import static cn.sl.manager.database.tables.User.USER;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @GetMapping("/token_check")
    public void tokenCheck(HttpServletRequest request, String signature, String timestamp, String nonce, String echostr
            , HttpServletResponse response) throws IOException {
        echostr = request.getParameter("echostr");
        log.info("========{}", echostr);

        String[] pars = {timestamp, timestamp, nonce};

//        return echostr;
        PrintWriter writer = response.getWriter();
        writer.write(echostr);
        writer.flush();
    }

    @GetMapping("/info")
    public Reply getUser(@Authenticated User user) {

        UserRecord userRecord = dsl.selectFrom(USER).where(USER.ID.eq(user.getId()))
                .fetchOne();
        userRecord.setPassword(null);
        return Reply.success().data(userRecord);


    }

    @PostMapping("discount/add")
    public Reply addDiscount(@RequestParam String code, @Authenticated User user) {

        userService.addDisCount(code, user.getId());
        return Reply.success();
    }

    @GetMapping("/discount/detail")
    public Reply getDiscount(String code) {

        DiscountCouponRecord discountCouponRecord = dsl.selectFrom(DISCOUNT_COUPON)
                .where(DISCOUNT_COUPON.CODE.eq(code))
                .fetchOne();
        return Reply.success().data(discountCouponRecord);


    }

    @PostMapping("/add_account")
    public Reply create(@RequestParam String code, @Authenticated User user) {

        userService.addAccount(code, user.getId());
        return Reply.success();
    }

    @PostMapping("/create")
    public Reply create(@RequestParam String phone, @RequestParam String password, String name) {

        boolean user = userService.createUser(phone, name, password);

        return user ? Reply.success() : Reply.fail();
    }

    @PostMapping("/auto/sign_in")
    public Reply autoSignIn(HttpServletRequest request, String code) throws IOException {

        String s = userService.autoSignIn(request, code);

        return Reply.success().data(s);
    }

    @PostMapping("/sign_in")
    public Reply signIn(HttpServletRequest request, String phone, String password, String mpCode) throws IOException {

        userService.signIn(request, phone, password, mpCode);

        return Reply.success().data(phone);
    }

    @PostMapping("/sign_out")
    public Reply signOut(HttpServletRequest request, @Authenticated User user) {

        userService.signOut(request,user.getId());

        return Reply.success();
    }
    @PostMapping("/verify_code")
    public Reply verifyCode(String phone) throws IOException {

        userService.verifyCode(phone);

        return Reply.success();
    }

    @PostMapping("/update_password")
    public Reply updatePassword(String phone, String code, String password) throws IOException {

        userService.updatePassword(phone,code,password);
        return Reply.success();
    }
}

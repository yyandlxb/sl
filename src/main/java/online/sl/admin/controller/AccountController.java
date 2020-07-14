package online.sl.admin.controller;

import online.sl.admin.service.AdminAccountService;
import online.sl.util.Reply;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminAccountController")
@RequestMapping("/admin/account")
public class AccountController {

    private AdminAccountService accountService;

    @Autowired
    public void setAccountService(AdminAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/apply/confirm")
    public Reply applyConfirm(Integer id, Boolean audit, String remark){

        accountService.applyConfirm(id, audit, remark);

        return Reply.success();
    }

}

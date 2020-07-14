package online.sl.admin.controller;

import online.sl.admin.service.AdminBerthService;
import online.sl.util.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminBerthController")
@RequestMapping("/admin")
public class BerthController {


    private AdminBerthService adminBerthService;

    @Autowired
    public void setAdminBerthService(AdminBerthService adminBerthService) {
        this.adminBerthService = adminBerthService;
    }

    @PostMapping("/berth/order/delete")
    public Reply deleteOrder(Integer id) {

        adminBerthService.deleteBerthOrder(id);
        return Reply.success();

    }
}

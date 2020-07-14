package online.sl.admin.controller;

import online.sl.admin.service.AuditService;
import online.sl.util.Reply;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/admin/audit")
public class AuditController {

    private DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    private AuditService auditService;

    @Autowired
    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping("/berth/confirm")
    public Reply audit(Integer id, boolean audit) {
        auditService.audit(id, audit);
        return Reply.success();
    }

    @PostMapping("/add_discount")
    public Reply addDiscount(BigDecimal amount) {
        auditService.addDiscount(amount);
        return Reply.success();
    }


}

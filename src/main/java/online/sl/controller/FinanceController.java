package online.sl.controller;

import online.sl.domain.User;
import online.sl.service.FinanceService;
import online.sl.session.Authenticated;
import online.sl.util.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/finance")
public class FinanceController {

    private FinanceService financeService;

    @Autowired
    public void setFinanceService(FinanceService financeService) {
        this.financeService = financeService;
    }

    @PostMapping("/create")
    public Reply create(@Valid FinanceService.FinanceForm financeForm, @Authenticated User user) {

        financeService.create(financeForm, user.getId());
        return Reply.success();
    }
}

package com.cg.service.withdraw;

import com.cg.model.Withdraw;
import com.cg.service.IGeneralService;

import java.math.BigDecimal;

public interface IWithdrawService extends IGeneralService<Withdraw,Long> {
    void withdraw(long id, BigDecimal amount);
}

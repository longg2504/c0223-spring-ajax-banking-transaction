package com.cg.service.withdraw;

import com.cg.model.Withdraw;
import com.cg.repository.WithdrawRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class WithdrawServiceImpl implements IWithdrawService{
    @Autowired
    private WithdrawRepository withdrawRepository;
    @Override
    public List<Withdraw> findAll() {
        return null;
    }

    @Override
    public Optional<Withdraw> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Withdraw save(Withdraw withdraw) {
        return withdrawRepository.save(withdraw);
    }

    @Override
    public void delete(Withdraw withdraw) {

    }

    @Override
    public void deleteById(Long id) {

    }
    @Override
    public void withdraw(long id, BigDecimal amount) {
        withdrawRepository.withdraw(id, amount);
    }

}

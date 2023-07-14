package com.cg.repository;


import com.cg.model.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Repository
public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Customer c " +
            "SET c.balance = c.balance - :amount " +
            "WHERE c.id = :id")
    void withdraw(@Param("id") long id, @Param("amount") BigDecimal amount);
}

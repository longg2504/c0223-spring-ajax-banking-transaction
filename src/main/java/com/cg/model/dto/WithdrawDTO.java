package com.cg.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WithdrawDTO implements Validator {
    private String transactionAmount;

    @Override
    public boolean supports(Class<?> aClass) {
        return WithdrawDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        WithdrawDTO withdrawDTO = (WithdrawDTO) o;

        String transactionAmountStr = withdrawDTO.getTransactionAmount();

        if (transactionAmountStr == null) {
            errors.rejectValue("transactionAmount", "transactionAmount.null", "Số tiền rút  là bắt buộc");
            return;
        }

        if (transactionAmountStr.length() == 0) {
            errors.rejectValue("transactionAmount", "transactionAmount.length", "Vui lòng nhập số tiền muốn rút");
        }
        else {
            if (!transactionAmountStr.matches("\\d+")) {
                errors.rejectValue("transactionAmount", "transactionAmount.matches", "Vui lòng nhập giá trị tiền bằng chữ số");
            }
            else {
                BigDecimal transactionAmount = BigDecimal.valueOf(Long.parseLong(transactionAmountStr));

                if (transactionAmount.compareTo(BigDecimal.valueOf(100L)) <= 0) {
                    errors.rejectValue("transactionAmount", "transactionAmount.min", "Số tiền muốn rút thấp nhất là $100");
                }

                }
            }
        }
    }

package com.cg.model.dto;

import com.cg.model.Customer;
import com.cg.utils.ValidateUtils;
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
public class CustomerResDTO implements Validator {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private BigDecimal balance;


    @Override
    public boolean supports(Class<?> aClass) {
        return CustomerResDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CustomerResDTO customerResDTO = (CustomerResDTO) o;

        String fullName = customerResDTO.fullName;

        if (fullName.length() == 0) {
            errors.rejectValue("fullName", "fullName.empty", "Họ tên là bắt buộc ");
        } else {
            if (fullName.length() < 5 || fullName.length() > 30) {
                errors.rejectValue("fullName", "fullName.length", "Họ tên nhập vào phải lớn hơn 5 ký tự và không vượt quá 30 ký tự");
            }
        }

        String email = customerResDTO.email;
        if (email.length() == 0) {
            errors.rejectValue("email", "email.empty ", "Email là bắt buộc ");
        } else {
            if (!ValidateUtils.isEmailValid(email)) {
                errors.rejectValue("email", "email.valid",
                        "Email không đúng định dạng vui lòng nhập (example : abc22@gmail.com)");
            }
        }
    }
}


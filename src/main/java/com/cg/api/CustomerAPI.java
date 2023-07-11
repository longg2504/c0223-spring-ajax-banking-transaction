package com.cg.api;

import com.cg.model.Customer;
import com.cg.model.dto.CustomerResDTO;
import com.cg.service.customer.ICustomerService;
import com.cg.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.cg.utils.AppUtils;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("api/customers")
public class CustomerAPI {
    @Autowired
    private ICustomerService customerService;

    @Autowired
    private AppUtils appUtils;

    @Autowired
    private ValidateUtils validateUtils;

    @GetMapping
    public ResponseEntity<?> getAllCustomers(){
        List<Customer> customers = customerService.findAll();

        List<CustomerResDTO> customerResDTOS = new ArrayList<>();

        for (Customer item : customers) {
            CustomerResDTO customerResDTO = new CustomerResDTO();
            customerResDTO.setId(item.getId());
            customerResDTO.setFullName(item.getFullName());
            customerResDTO.setEmail(item.getEmail());
            customerResDTO.setPhone(item.getPhone());
            customerResDTO.setBalance(item.getBalance());
            customerResDTO.setAddress(item.getAddress());

            customerResDTOS.add(customerResDTO);
        }

        return new ResponseEntity<>(customerResDTOS, HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getById(@PathVariable Long customerId){
        Optional<Customer> customerOptional = customerService.findById(customerId);

        if(customerOptional.isEmpty()){

            Map<String,String> data = new HashMap<>();
            data.put("message" , "Khách hàng không tồn tại");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(customerOptional.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CustomerResDTO customerResDTO , BindingResult bindingResult) {

        new CustomerResDTO().validate(customerResDTO,bindingResult);

        if(bindingResult.hasFieldErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }

        String email = customerResDTO.getEmail();
        boolean existsEmail = customerService.existsByEmail(email);
        if (existsEmail) {
            Map<String,String> data = new HashMap<>();
            data.put("message","Email nhập vào đã tồn tại");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }

        Customer customer = new Customer();
        customer.setId(null);
        customer.setFullName(customerResDTO.getFullName());
        customer.setEmail(customerResDTO.getEmail());
        customer.setPhone(customerResDTO.getPhone());
        customer.setAddress(customerResDTO.getAddress());
        customer.setBalance(BigDecimal.ZERO);
        Customer newCustomer = customerService.save(customer);

        return new ResponseEntity<>(newCustomer, HttpStatus.OK);
    }

    @PatchMapping("/{customerId}")
    public ResponseEntity<?> doUpdate(@PathVariable("customerId") String customerIdstr , @RequestBody CustomerResDTO customerResDTO,
                                      BindingResult bindingResult){

        new CustomerResDTO().validate(customerResDTO,bindingResult);
        if(bindingResult.hasFieldErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        if(!ValidateUtils.isNumberValid(customerIdstr)){
            Map<String,String> data = new HashMap<>();
            data.put("message","Mã khách hàng không hợp lệ");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }

        Long customerId = Long.parseLong(customerIdstr);

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if(customerOptional.isEmpty()){
            Map<String, String> data = new HashMap<>();
            data.put("message", "Mã khách hàng không tồn tại");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }

        String email = customerResDTO.getEmail();
        boolean checkEmail = customerService.existsByEmailAndIdNot(email, customerId);
        if(checkEmail){
            Map<String,String> data = new HashMap<>();
            data.put("message","Email nhập vào đã tồn tại");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }

        Customer customer = customerOptional.get();
        customer.setFullName(customerResDTO.getFullName());
        customer.setEmail(customerResDTO.getEmail());
        customer.setPhone(customerResDTO.getPhone());
        customer.setAddress(customerResDTO.getAddress());
        Customer newCustomer = customerService.save(customer);

        return new ResponseEntity<>(newCustomer, HttpStatus.OK);
    }

}

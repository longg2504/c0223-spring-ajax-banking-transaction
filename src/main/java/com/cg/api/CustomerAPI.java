package com.cg.api;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.model.dto.CustomerResDTO;
import com.cg.model.dto.DepositCreReqDTO;
import com.cg.model.dto.TransferDTO;
import com.cg.model.dto.WithdrawDTO;
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
        List<Customer> customers = customerService.findAllByDeletedIsFalse();

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

    @PatchMapping("/update/{customerId}")
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

    @PatchMapping("/delete/{customerId}")
    public ResponseEntity<?> doDelete(@PathVariable("customerId") String customerIdstr){
        if(!ValidateUtils.isNumberValid(customerIdstr)){
            Map<String,String> data = new HashMap<>();
            data.put("message","Mã khách hàng không hợp lệ");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
        Long customerId = Long.parseLong(customerIdstr);
        Optional<Customer> customerOptional = customerService.findById(customerId);
        Customer customer = customerOptional.get();
        customer.setDeleted(true);
        customerService.save(customer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/withdraw/{customerId}")
    public ResponseEntity<?> deposit(@PathVariable("customerId") String customerIdstr ,
                                     @RequestBody WithdrawDTO withdrawDTO , BindingResult bindingResult){
        new WithdrawDTO().validate(withdrawDTO, bindingResult);

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

        Customer customer = customerOptional.get();
        BigDecimal currentBalance = customer.getBalance();
        BigDecimal transactionAmount = BigDecimal.valueOf(Long.parseLong(withdrawDTO.getTransactionAmount()));
        if(transactionAmount.compareTo(currentBalance) > 0){
            Map<String, String> data = new HashMap<>();
            data.put("message", "Số dư tài khoản không đủ để rút");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
        Withdraw withdraw = new Withdraw();
        withdraw.setTransactionAmount(transactionAmount);
        withdraw.setCustomer(customer);

        Customer newCustomer = customerService.withdraw(withdraw);

        return new ResponseEntity<>(newCustomer, HttpStatus.OK);
    }

    @PostMapping("/transfers/{customerId}")
    public ResponseEntity<?> transfer(@PathVariable("customerId") String senderIdStr, @RequestBody TransferDTO transferDTO, BindingResult bindingResult) {


        new TransferDTO().validate(transferDTO, bindingResult);
        if(bindingResult.hasFieldErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }

        Map<String, String> data = new HashMap<>();
        if (!ValidateUtils.isNumberValid(senderIdStr)) {
            data.put("message", "Mã khách hàng không hợp lệ");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }

        Long senderId = Long.valueOf(senderIdStr);

        Optional<Customer> optionalSender = customerService.findById(senderId);
        if (optionalSender.isEmpty()) {
            data.put("message", "Sender không hợp lệ");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
        Customer sender = optionalSender.get();

        Long recipientId = transferDTO.getRecipientId();
        Optional<Customer> optionalRecipient = customerService.findById(recipientId);
        if (optionalRecipient.isEmpty()) {
            data.put("message", "Không tìm thấy Recipient");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
        Customer recipient = optionalRecipient.get();

        if (sender.getId() == recipient.getId()) {
            data.put("message", "Sender phải khác Recipient");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }



        BigDecimal currentBalance = sender.getBalance();
        Long transferAmountLong = Long.valueOf(transferDTO.getTransferAmount());
        BigDecimal transferAmount = BigDecimal.valueOf(transferAmountLong);
        Long fees = 10L;
        BigDecimal feesAmount = transferAmount.multiply(BigDecimal.valueOf(fees)).divide(BigDecimal.valueOf(100));
        BigDecimal transactionAmount = transferAmount.add(feesAmount);

        if (currentBalance.compareTo(transactionAmount) < 0) {
            data.put("message", "Số dư không đủ để thực hiện giao dịch");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }

        Transfer transfer = new Transfer();
        transfer.setTransferAmount(transferAmount);
        transfer.setTransactionAmount(transactionAmount);
        transfer.setSender(sender);
        transfer.setRecipient(recipient);
        transfer.setFees(10L);
        transfer.setFeesAmount(feesAmount);

        customerService.transfer(transfer);
        transfer.setSender(customerService.findById(sender.getId()).get());
        transfer.setRecipient(customerService.findById(recipient.getId()).get());
        List<Customer> customers = new ArrayList<>();
        customers.add(transfer.getSender());
        customers.add(transfer.getRecipient());

        return new ResponseEntity<>(customers, HttpStatus.OK);

    }



}

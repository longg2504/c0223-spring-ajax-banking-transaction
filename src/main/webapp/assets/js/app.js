class App {
    static DOMAIN_SERVER = window.origin;
    static API_SERVER = this.DOMAIN_SERVER + '/api';

    static API_CUSTOMER = this.API_SERVER + '/customers';
    static API_DEPOSIT = this.API_SERVER + '/deposits';
}

class Customer {
    constructor(id, fullName, email, phone, balance, address) {
      this.id = id;
      this.fullName = fullName;
      this.email = email;
      this.phone = phone;
      this.balance = balance;
      this.address = address;
    }
  }
  
  class Deposit {
    constructor(id, customerId, transactionAmount) {
      this.id = id;
      this.customerId = customerId;
      this.transactionAmount = transactionAmount;
    }
  }

  class Withdraw {
    constructor(id, transactionAmount) {
        this.id = id;
        this.transactionAmount = transactionAmount;
    }
}

class Transfer {
    constructor(id, transactionAmount, fees, feesAmount, transferAmount, senderId, recipientId,customerId) {
        this.id = id;
        this.transactionAmount = transactionAmount;
        this.customerId = customerId;
        this.fees = fees;
        this.feesAmount = feesAmount;
        this.transferAmount = transferAmount;
        this.senderId = senderId;
        this.recipientId = recipientId;
    }
}
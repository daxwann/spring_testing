package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
  public static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD, Currency.GBP);

  private final CustomerRepository customerRepository;
  private final PaymentRepository paymentRepository;
  private final CardPaymentCharger cardPaymentCharger;

  @Autowired
  public PaymentService(
      CustomerRepository customerRepository,
      PaymentRepository paymentRepository,
      CardPaymentCharger cardPaymentCharger) {
    this.customerRepository = customerRepository;
    this.paymentRepository = paymentRepository;
    this.cardPaymentCharger = cardPaymentCharger;
  }

  void chargeCard(UUID customerId, PaymentRequest paymentRequest) {
    // throw if customer does not exist
    if (!customerRepository.findById(customerId).isPresent()) {
      throw new IllegalStateException(String.format("Customer with id %s not found", customerId));
    }

    // throw if currency not supported
    if (!ACCEPTED_CURRENCIES.contains(paymentRequest.getPayment().getCurrency())) {
      throw new IllegalStateException(String.format("%s not supported", paymentRequest.getPayment().getCurrency()));
    }

    // charge card
    CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
        paymentRequest.getPayment().getSource(),
        paymentRequest.getPayment().getAmount(),
        paymentRequest.getPayment().getCurrency(),
        paymentRequest.getPayment().getDescription()
    );

    // if not debited
    if (!cardPaymentCharge.isCardDebited()) {
      throw new IllegalStateException(String.format("Card not debited for customer %s", customerId));
    }

    // insert payment
    paymentRequest.getPayment().setCustomerId(customerId);

    paymentRepository.save(paymentRequest.getPayment());
  }
}

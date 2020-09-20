package com.amigoscode.testing.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
    properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
    }
)
public class PaymentRepositoryTest {
  private PaymentRepository testPaymentRepository;

  @Autowired
  public PaymentRepositoryTest(PaymentRepository testPaymentRepository) {
    this.testPaymentRepository = testPaymentRepository;
  }

  @Test
  void itShouldInsertPayment() {
    long paymentId = 1L;
    Payment payment = new Payment(
        paymentId,
        UUID.randomUUID(),
        new BigDecimal("10.00"),
        Currency.USD, "card123",
        "Donation"
    );

    testPaymentRepository.save(payment);

    Optional<Payment> paymentOptional = testPaymentRepository.findById(paymentId);
    assertThat(paymentOptional)
        .isPresent()
        .hasValueSatisfying(p -> assertThat(p).isEqualTo(payment));
  }
}

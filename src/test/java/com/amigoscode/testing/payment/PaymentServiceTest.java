package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class PaymentServiceTest {
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private PaymentRepository paymentRepository;
  @Mock
  private CardPaymentCharger cardPaymentCharger;

  private PaymentService testPaymentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    testPaymentService = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
  }

  @Test
  void itShouldChargeCardSuccessfully() {
    UUID customerId = UUID.randomUUID();

    given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

    Payment payment = new Payment(
        null,
        null,
        new BigDecimal("100.00"),
        Currency.USD,
        "card12345",
        "Donation"
    );
    PaymentRequest paymentRequest = new PaymentRequest(payment);

    given(cardPaymentCharger.chargeCard(
        paymentRequest.getPayment().getSource(),
        paymentRequest.getPayment().getAmount(),
        paymentRequest.getPayment().getCurrency(),
        paymentRequest.getPayment().getDescription()
    )).willReturn(new CardPaymentCharge(true));

    testPaymentService.chargeCard(customerId, paymentRequest);

    ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

    then(paymentRepository).should().save(paymentArgumentCaptor.capture());

    Payment paymentRepositoryArgument = paymentArgumentCaptor.getValue();
    assertThat(paymentRepositoryArgument)
        .isEqualToIgnoringGivenFields(paymentRequest.getPayment(), "customerId");

    assertThat(paymentRepositoryArgument.getCustomerId()).isEqualTo(customerId);
  }
}

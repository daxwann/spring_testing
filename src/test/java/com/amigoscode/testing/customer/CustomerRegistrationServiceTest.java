package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

public class CustomerRegistrationServiceTest {
  @Mock
  private CustomerRepository customerRepository;

  @Captor
  private ArgumentCaptor<Customer> customerArgumentCaptor;

  private CustomerRegistrationService testService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    testService = new CustomerRegistrationService(customerRepository);
  }

  @Test
  void itShouldSaveNewCustomer() {
    // test customer
    String phone = "7777";
    Customer customer = new Customer(UUID.randomUUID(), "Marian", phone);

    // request
    CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

    // DB doesn't have customer with this phone number
    given(customerRepository.selectCustomerByPhoneNumber(phone)).willReturn(Optional.empty());

    // register customer
    testService.registerNewCustomer(customerRegistrationRequest);

    // check if DB is saving the same customer
    then(customerRepository).should().save(customerArgumentCaptor.capture());
    Customer capturedCustomerArg = customerArgumentCaptor.getValue();
    assertThat(capturedCustomerArg).isEqualTo(customer);
  }
}

package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.Optional;
import java.util.UUID;

@DataJpaTest(properties = {"spring.jpa.properties.javax.persistence.validation.mode=none"})
public class CustomerRepositoryTest {
  private CustomerRepository testRepository;

  @Autowired
  public CustomerRepositoryTest(CustomerRepository testRepository) {
    this.testRepository = testRepository;
  }

  @Test
  void itShouldSelectCustomerByPhoneNumber() {
    // given
    UUID id = UUID.randomUUID();
    String phoneNumber = "1111";
    String name = "Hansel";
    Customer customer = new Customer(id, name, phoneNumber);

    testRepository.save(customer);

    // test
    Optional<Customer> optionalCustomer = testRepository.selectCustomerByPhoneNumber(phoneNumber);
    assertThat(optionalCustomer)
        .isPresent()
        .hasValueSatisfying(c -> {
          assertThat(c).isEqualToComparingFieldByField(customer);
        });
  }
}

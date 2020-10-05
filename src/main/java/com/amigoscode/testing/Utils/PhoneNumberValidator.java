package com.amigoscode.testing.Utils;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class PhoneNumberValidator implements Predicate<String> {
  @Override
  public boolean test(String phoneNumber) {
    return phoneNumber.startsWith("+44") && phoneNumber.length() == 13;
  }
}

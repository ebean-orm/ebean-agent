package test.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Contact extends BaseEntity {

  @ManyToOne
  Customer customer;

  final String firstName;
  String lastName;
  String email;
  String phone;

  public Contact(String firstName) {
    this.firstName = firstName;
  }
  public ContactDto asDto() {

    ContactDto dto = new ContactDto();
    dto.firstName = firstName;
    dto.one = one;
    return dto;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }
}

package test.model.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.sql.Date;
import java.util.List;

/**
 * Order entity bean.
 */
@Entity
@Table(name = "o_order")
public class Order extends BaseModel {

  public enum Status {
    NEW, APPROVED, SHIPPED, COMPLETE
  }

  Status status;

  Date orderDate;

  Date shipDate;


  @ManyToOne
  Address shippingAddress;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
  @OrderBy("id asc")
  List<OrderDetail> details;

  @Override
  public String toString() {
    return id + " status:" + status;
  }

  /**
  * Return order date.
  */
  public Date getOrderDate() {
    return orderDate;
  }

  /**
  * Set order date.
  */
  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  /**
  * Return ship date.
  */
  public Date getShipDate() {
    return shipDate;
  }

  /**
  * Set ship date.
  */
  public void setShipDate(Date shipDate) {
    this.shipDate = shipDate;
  }

  /**
  * Return status.
  */
  public Status getStatus() {
    return status;
  }

  /**
  * Set status.
  */
  public void setStatus(Status status) {
    this.status = status;
  }

  /**
  * Return details.
  */
  public List<OrderDetail> getDetails() {
    return details;
  }

  /**
  * Set details.
  */
  public void setDetails(List<OrderDetail> details) {
    this.details = details;
  }

}

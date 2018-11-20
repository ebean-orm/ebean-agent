package test.model.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Product entity bean.
 */
@Entity
@Table(name = "o_product")
public class Product extends BaseModel {

  String sku;

  String name;

  /**
  * Return sku.
  */
  public String getSku() {
    return sku;
  }

  /**
  * Set sku.
  */
  public void setSku(String sku) {
    this.sku = sku;
  }

  /**
  * Return name.
  */
  public String getName() {
    return name;
  }

  /**
  * Set name.
  */
  public void setName(String name) {
    this.name = name;
  }

}

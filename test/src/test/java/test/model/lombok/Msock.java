package test.model.lombok;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Generated;

import java.util.List;

@Entity
@Table(name = "stock")
public class Msock {

  private String name;
  private String code;
  @ManyToMany(fetch = FetchType.LAZY)
  private List<App> myapps;

  @Generated
  public Msock() {
  }

  @Generated
  protected Msock(Msock.StockBuilder<?, ?> b) {
    this.name = b.name;
    this.code = b.code;
    this.myapps = b.apps;
  }

  @Generated
  public Msock(String name, String code, List<App> apps) {
    this.name = name;
    this.code = code;
    this.myapps = apps;
  }

  @Generated
  public static Msock.StockBuilder<?, ?> builder() {
    return new Msock.StockBuilderImpl();
  }

  @Generated
  public String getName() {
    return this.name;
  }

  @Generated
  public void setName(String name) {
    this.name = name;
  }

  @Generated
  public String getCode() {
    return this.code;
  }

  @Generated
  public void setCode(String code) {
    this.code = code;
  }

  @Generated
  public List<App> getApps() {
    return this.myapps;
  }

  @Generated
  public void setApps(List<App> myapps) {
    this.myapps = myapps;
  }

  @Generated
  public abstract static class StockBuilder<C extends Msock, B extends Msock.StockBuilder<C, B>> {
    @Generated
    private String name;
    @Generated
    private String code;
    @Generated
    private List<App> apps;

    public StockBuilder() {
    }

    @Generated
    public B name(String name) {
      this.name = name;
      return this.self();
    }

    @Generated
    public B code(String code) {
      this.code = code;
      return this.self();
    }

    @Generated
    public B apps(List<App> apps) {
      this.apps = apps;
      return this.self();
    }

    @Generated
    protected abstract B self();

    @Generated
    public abstract C build();

    @Generated
    public String toString() {
      String var10000 = this.name;
      return "Stock.StockBuilder(name=" + var10000 + ", code=" + this.code + ", apps=" + String.valueOf(this.apps) + ")";
    }
  }

  @Generated
  private static final class StockBuilderImpl extends Msock.StockBuilder<Msock, Msock.StockBuilderImpl> {
    @Generated
    private StockBuilderImpl() {
    }

    @Generated
    protected StockBuilderImpl self() {
      return this;
    }

    @Generated
    public Msock build() {
      return new Msock(this);
    }
  }
}


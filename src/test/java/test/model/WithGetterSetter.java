package test.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class WithGetterSetter extends BaseEntity {

	public transient List<String> log = new ArrayList<String>();
	
	String name;

	Integer count;

	boolean flag;

	long number;
	
	double salary;
	
	int age;

	@Override
	public void setId(Long id) {
		log.add("setId");
		super.setId(id);
	}
	
	public String getName() {
		log.add("getName");
		return name;
	}

	public WithGetterSetter setName(String name) {
		log.add("setName");
		this.name = name;
		return this;
	}

	public Integer getCount() {
		log.add("getCount");
		return count;
	}

	public WithGetterSetter setCount(Integer count) {
		log.add("setCount");
		this.count = count;
		return this;
	}

	public boolean isFlag() {
		log.add("isFlag");
		return flag;
	}

	public WithGetterSetter setFlag(boolean flag) {
		log.add("setFlag");
		this.flag = flag;
		return this;
	}

	public long getNumber() {
		log.add("getNumber");
		return number;
	}

	public long setNumber(long number) {
		log.add("setNumber");
		long oldValue = this.number;
		this.number = number;
		return oldValue;
	}
	
	public double setSalary(double salary) {
		double oldValue = this.salary;
		this.salary = salary;
		return oldValue;
	}
	
	public int setAge(int age) {
		int oldValue = this.age;
		this.age = age;
		return oldValue;
	}
	

	// some different setters to test the setter detection in fieldMeta
	public void setAge(double age) {
		this.age = (int) age;
	}
	
	public void setSalary(int salary) {
		this.salary = (double) salary;
	}
	public WithGetterSetter setNumber() { return this; }
	public void setNumber(int i, int j) { }
	public void setNumber(int i, int[] j) { }
	public void setNumber(Object o, int[] j) { }
	public void setArray1(Object[] o) { }
	public void setArray2(int[] i) { }
	
}

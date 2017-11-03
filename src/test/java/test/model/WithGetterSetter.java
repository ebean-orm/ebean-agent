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

	public void setNumber(long number) {
		log.add("setNumber");
		this.number = number;
	}
	
	// some different setters to test the setter detection in fieldMeta
	
	public WithGetterSetter setNumber() { return this; }
	public void setNumber(int i, int j) { }
	public void setNumber(int i, int[] j) { }
	public void setNumber(Object o, int[] j) { }
	public void setArray1(Object[] o) { }
	public void setArray2(int[] i) { }
	
}

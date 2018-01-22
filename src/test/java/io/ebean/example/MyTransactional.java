package io.ebean.example;

import io.ebean.TxScope;
//import io.ebean.annotation.PersistBatch;
import io.ebean.annotation.Transactional;
import io.ebean.bean.HelpTx;
import io.ebeaninternal.api.HelpScopeTrans;

import java.util.function.Supplier;

public class MyTransactional {

	String other;

//  private static ProfileLocation _$ebt1;
//  private static ProfileLocation _$ebt2;
//
//  private static void _$initProfileLocations() {
//    _$ebt1 = ProfileLocation.create();
//    _$ebt2 = ProfileLocation.create(12);
//  }
//
//  static {
//    _$initProfileLocations();
//  }

	public <T> T exe(TxScope scope, Supplier<T> producer) {

		return producer.get();
	}

	TxScope newTxScope() {
		return new TxScope();
	}

	@Transactional(batchSize = 100)
	public String doWithTransaction() {

//		TxScope scope = new TxScope();
//		scope.setType(TxType.REQUIRES_NEW);
//		scope.setBatchSize(100);
//		scope.setLabel("sd");
//		scope.setSkipCache(true);
//		scope.setProfileLocation(_$ebx1);
//		scope.setBatch(PersistBatch.ALL);

		HelpScopeTrans.enter(newTxScope());
		try {

			System.out.println("hj");
			String val = doSomething();
			if (val == null) {
				val = "some cont";
			}
			other = val;

			HelpScopeTrans.exit(null, 177);
			return val;

		} catch (Throwable e) {
			HelpScopeTrans.exit(e, 191);
			throw e;
		}
	}

	private void basic() {
		HelpTx.start(new TxScope().setBatchSize(100));
		try {
			System.out.println("--- do stuff in here");
			doSomething();
			HelpTx.end();
		} catch (Throwable var6) {
			HelpTx.end(var6);
			throw var6;
		}
	}

	private String doSomething() {
		return "stuff";
	}
}

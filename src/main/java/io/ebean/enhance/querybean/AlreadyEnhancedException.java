package io.ebean.enhance.querybean;

/**
 * Exception thrown during enhancement when it is detected that the enhancement has already taken place.
 */
public class AlreadyEnhancedException extends RuntimeException {

	private static final long serialVersionUID = -831705721822834774L;

	public AlreadyEnhancedException(String msg) {
    super(msg);
	}

}

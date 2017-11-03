package io.ebean.enhance.common;

import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class IsSetterPatternTest {

	private static final Pattern PATTERN = ClassMeta.HAS_SINGLE_ARGUMENT;
	@Test
	public void testPattern() throws Exception {
		assertThat("(Ljava/lang/Long;)V").matches(PATTERN);
		assertThat("(Ljava/lang/String;)Ltest/model/WithGetterSetter;").matches(PATTERN);
		assertThat("(Ljava/lang/Integer;)Ltest/model/WithGetterSetter;").matches(PATTERN);
		assertThat("(Z)Ltest/model/WithGetterSetter;").matches(PATTERN);
		assertThat("(J)V").matches(PATTERN);
		assertThat("()Ltest/model/WithGetterSetter;").doesNotMatch(PATTERN);
		assertThat("(II)V").doesNotMatch(PATTERN);
		assertThat("(I[I)V").doesNotMatch(PATTERN);
		assertThat("(Ljava/lang/Object;[I)V").doesNotMatch(PATTERN);
		assertThat("([Ljava/lang/Object;)V").matches(PATTERN);
		assertThat("(Ljava/lang/Object;[I)V").doesNotMatch(PATTERN);
		assertThat("([I)V").matches(PATTERN);

	}

}
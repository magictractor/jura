/**
 * Copyright 2015-2019 Ken Dobson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.magictractor.jura.suite.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.FilterResult;

import uk.co.magictractor.jura.suite.filter.IsSuiteFilter;

public class IsSuiteFilterTest {

	@Test
	public void testSuiteWithPrefix() {
		IsSuiteFilter filter = new IsSuiteFilter(() -> this::standardIsSuite);
		FilterResult filterResult = filter.apply("a.b.c.MySuite");
		assertIncluded(filterResult);
	}

	@Test
	public void testSuiteWithoutPrefix() {
		IsSuiteFilter filter = new IsSuiteFilter(() -> this::standardIsSuite);
		FilterResult filterResult = filter.apply("a.b.c.Suite");
		assertIncluded(filterResult);
	}

	@Test
	public void testNotSuite() {
		IsSuiteFilter filter = new IsSuiteFilter(() -> this::standardIsSuite);
		FilterResult filterResult = filter.apply("a.b.c.SomethingTest");
		assertExcluded(filterResult);
	}

	@Test
	public void testAlternativePredicateIncluded() {
		IsSuiteFilter filter = new IsSuiteFilter(() -> this::alternativeIsSuite);
		FilterResult filterResult = filter.apply("a.b.c.MyBunchOfTests");
		assertIncluded(filterResult);
	}

	@Test
	public void testAlternativePredicateExcluded() {
		IsSuiteFilter filter = new IsSuiteFilter(() -> this::alternativeIsSuite);
		FilterResult filterResult = filter.apply("a.b.c.SomethingSuite");
		assertExcluded(filterResult);
	}

	private boolean standardIsSuite(String testClassName) {
		return testClassName.endsWith("Suite");
	}

	private boolean alternativeIsSuite(String testClassName) {
		return testClassName.endsWith("BunchOfTests");
	}

	private void assertIncluded(FilterResult filterResult) {
		assertThat(filterResult.included()).isTrue();
		assertThat(filterResult.getReason().isPresent()).isTrue();
		assertThat(filterResult.getReason().get()).isEqualTo("is a suite");
	}

	private void assertExcluded(FilterResult filterResult) {
		assertThat(filterResult.included()).isFalse();
		assertThat(filterResult.getReason().isPresent()).isTrue();
		assertThat(filterResult.getReason().get()).isEqualTo("is not a suite");
	}

}

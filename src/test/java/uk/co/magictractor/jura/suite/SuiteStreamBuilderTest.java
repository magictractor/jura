/**
 * Copyright 2019 Ken Dobson
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

package uk.co.magictractor.jura.suite;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicNode;

public class SuiteStreamBuilderTest {

	@SuiteTest
	public void testNoChildPackages() {
		Assertions.assertThatThrownBy(() -> new SuiteStreamBuilder().selectSuitesInChildPackages()).isExactlyInstanceOf(
			IllegalStateException.class).hasMessage("No child packages to select");
	}

	private List<DynamicNode> build(SuiteStreamBuilder suiteStreamBuilder) {
		return suiteStreamBuilder.build().collect(Collectors.toList());
	}

	/**
	 * Check dynamic tests are for exactly the classes given. Does not test
	 * dynamic containers.
	 */
	private void assertTests(List<DynamicNode> nodes, Class... expectedTestClasses) {
		//        /nodes.stream().filter(Dynamic)
	}

}

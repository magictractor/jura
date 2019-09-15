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

package uk.co.magictractor.jura.extension;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.RegisterExtension;

import uk.co.magictractor.jura.extension.SystemPropertiesExtension;

/**
 * <p>
 * SystemPropertiesExtensionTestPerMethodLifecycle and
 * SystemPropertiesExtensionTestPerClassLifecycle are identical other than
 * the @TestInstance annotation value.
 * </p>
 * <p>
 * The static field is copied because each test should have a distinct and
 * freshly configured static field rather than the same field being used for
 * both tests.
 * </p>
 */
@TestInstance(Lifecycle.PER_METHOD)
public class SystemPropertiesExtensionTestPerMethodLifecycle extends AbstractSystemPropertiesExtensionTest {

	@RegisterExtension
	public static SystemPropertiesExtension STATIC_EXTENSION = new SystemPropertiesExtension().withProperty(
		STATIC_KEY_FIELD, INITIAL_VALUE);

	@BeforeAll
	public static void beforeAll() {
		// Note that extension beforeAll() has already been called at this point.
		STATIC_EXTENSION.withProperty(STATIC_KEY_BEFORE_ALL, INITIAL_VALUE);
	}

	@Override
	protected SystemPropertiesExtension getStaticExtension() {
		return STATIC_EXTENSION;
	}

}

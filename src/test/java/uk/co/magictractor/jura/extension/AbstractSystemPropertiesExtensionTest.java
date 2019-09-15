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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.jura.extension.SystemPropertiesExtension;

public abstract class AbstractSystemPropertiesExtensionTest {

	protected static final String STATIC_KEY_FIELD = "__unitTest__static_field";
	protected static final String STATIC_KEY_BEFORE_ALL = "__unitTest__static_before_all";
	private static final String STATIC_KEY_BEFORE_EACH = "__unitTest__static_before_each";
	private static final String INSTANCE_KEY_FIELD = "__unitTest__instance_field";
	private static final String INSTANCE_KEY_BEFORE_EACH = "__unitTest__instance_before_each";
	protected static final String INITIAL_VALUE = "999";

	// So a distinct copy is used for the PER_METHOD and PER_CLASS subclasses.
	protected abstract SystemPropertiesExtension getStaticExtension();

	@RegisterExtension
	public SystemPropertiesExtension _instanceExtension = new SystemPropertiesExtension().withProperty(
		INSTANCE_KEY_FIELD, INITIAL_VALUE);

	/*
	 * Hash @Order annotation so it is applied first. More importantly it is
	 * applied last for afterEach() and afterAll() callbacks.
	 */
	@Order(1)
	@RegisterExtension
	public static PostTestsCheck _postTestsCheck = new PostTestsCheck();

	@BeforeEach
	public void beforeEach() {
		getStaticExtension().withProperty(STATIC_KEY_BEFORE_EACH, INITIAL_VALUE);
		_instanceExtension.withProperty(INSTANCE_KEY_BEFORE_EACH, INITIAL_VALUE);
	}

	@Test
	public void test1() {
		checkInitialSystemPropertyValues();

		setExtensionValues("aaa");

		checkSystemPropertyValues("aaa");
	}

	// Looks like a duplicate of test1, but the important bit is that the initial
	// values haven't been mucked up by a previous test.
	@Test
	public void test2() {
		checkInitialSystemPropertyValues();

		setExtensionValues("ppp");

		checkSystemPropertyValues("ppp");
	}

	private void setExtensionValues(String value) {
		SystemPropertiesExtension staticExtension = getStaticExtension();
		staticExtension.withProperty(STATIC_KEY_FIELD, value);
		staticExtension.withProperty(STATIC_KEY_BEFORE_ALL, value);
		staticExtension.withProperty(STATIC_KEY_BEFORE_EACH, value);
		_instanceExtension.withProperty(INSTANCE_KEY_FIELD, value);
		_instanceExtension.withProperty(INSTANCE_KEY_BEFORE_EACH, value);
	}

	private static void checkInitialSystemPropertyValues() {
		checkSystemPropertyValues(INITIAL_VALUE);
	}

	private static void checkSystemPropertyValues(String expectedValue) {
		checkSystemPropertyValues(expectedValue, expectedValue, null);
	}

	private static void checkSystemPropertyValues(String classScopedValue, String methodScopedValue,
			Lifecycle lifecycle) {
		Assertions.assertAll(() -> checkSystemPropertyValue(STATIC_KEY_FIELD, classScopedValue),
			() -> checkSystemPropertyValue(STATIC_KEY_BEFORE_ALL, classScopedValue),
			// A static field, but the value was set just for the scope of the method in @BeforeEach.
			() -> checkSystemPropertyValue(STATIC_KEY_BEFORE_EACH, methodScopedValue),
			// TODO! add comment explaining this
			() -> checkSystemPropertyValue(INSTANCE_KEY_FIELD,
				PER_CLASS.equals(lifecycle) ? classScopedValue : methodScopedValue),
			() -> checkSystemPropertyValue(INSTANCE_KEY_BEFORE_EACH, methodScopedValue));
	}

	private static void checkSystemPropertyValue(String propertyName, String expectedValue) {
		String actualValue = System.getProperty(propertyName);
		assertThat(actualValue).withFailMessage("Unexpected value for system property %1s: expected %2s, was %3s",
			propertyName, expectedValue, actualValue).isEqualTo(expectedValue);
	}

	public static class PostTestsCheck implements BeforeAllCallback, AfterEachCallback, AfterAllCallback {

		private static final Logger LOGGER = LoggerFactory.getLogger(PostTestsCheck.class);

		@Override
		public void beforeAll(ExtensionContext context) {
			LOGGER.trace("beforeAll");
		}

		@Override
		public void afterEach(ExtensionContext context) {
			LOGGER.trace("afterEach");
			// Check that static scoped values remain, and method scoped values have been restored.
			checkSystemPropertyValues(INITIAL_VALUE, null, context.getTestInstanceLifecycle().get());
		}

		@Override
		public void afterAll(ExtensionContext context) {
			LOGGER.trace("afterAll");
			// Check that all properties have been restored.
			checkSystemPropertyValues(null);
		}
	}

}

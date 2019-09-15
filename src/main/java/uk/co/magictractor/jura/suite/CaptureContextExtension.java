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

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * <p>
 * Capture the test context which is then available for suite builders. This
 * allows builders to determine the base class for the suite when selecting
 * tests and nested suites relative to the suite.
 * </p>
 * <p>
 * Default visibility because this extension should not be used directly. Tests
 * should use the @Suite annotation which combines @TestFactory and this
 * extension.
 * </p>
 */
/* default */ class CaptureContextExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

	private static final ThreadLocal<ExtensionContext> CONTEXT = new ThreadLocal<>();

	public static ExtensionContext remove() {
		ExtensionContext context = CONTEXT.get();
		if (context != null) {
			CONTEXT.remove();
		}
		else {
			throw new IllegalStateException(SuiteStreamBuilder.class.getSimpleName()
					+ " should only be used in a method annoted by @" + Suite.class.getSimpleName());
		}
		return context;
	}

	@Override
	public void beforeTestExecution(ExtensionContext context) throws Exception {
		if (CONTEXT.get() != null) {
			// Just log an error/warning??
			throw new IllegalStateException("There is an existing ExtensionContext");
		}

		CONTEXT.set(context);
	}

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {

		if (CONTEXT.get() != null) {
			// Just log an error/warning??
			throw new IllegalStateException("The ExtensionContext was not removed by a "
					+ SuiteStreamBuilder.class.getSimpleName() + " (or similar)");
		}
	}

}

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

package uk.co.magictractor.jura.suite.filter;

import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.base.MoreObjects;

import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.discovery.ClassNameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsSuiteFilter implements ClassNameFilter {

	private final Logger _logger = LoggerFactory.getLogger(getClass());

	private final Supplier<Predicate<String>> _suitePredicateSupplier;
	private FilterResult _included = FilterResult.included("is a suite");
	private FilterResult _excluded = FilterResult.excluded("is not a suite");

	public IsSuiteFilter(Supplier<Predicate<String>> suitePredicateSupplier) {
		_suitePredicateSupplier = suitePredicateSupplier;
	}

	@Override
	public FilterResult apply(String testClassName) {
		return isIncluded(testClassName) ? _included : _excluded;
	}

	private boolean isIncluded(String testClassName) {
		boolean isIncluded = _suitePredicateSupplier.get().test(testClassName);
		_logger.debug("isIncluded({}) -> {}", testClassName, isIncluded);

		return isIncluded;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).toString();
	}

}

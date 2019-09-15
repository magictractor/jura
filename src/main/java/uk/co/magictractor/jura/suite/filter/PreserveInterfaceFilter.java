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

import org.junit.platform.engine.Filter;
import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.discovery.ClassNameFilter;
import org.junit.platform.engine.discovery.PackageNameFilter;
import org.junit.platform.launcher.PostDiscoveryFilter;

public final class PreserveInterfaceFilter {

	private PreserveInterfaceFilter() {
	}

	@SuppressWarnings("unchecked")
	public static <T> Filter<T> preserveInterface(Filter<T> baseFilter, Filter<T> wrappedFilter) {
		if (baseFilter instanceof ClassNameFilter) {
			return (Filter<T>) new PreserveClassNameFilter((Filter<String>) wrappedFilter);
		}
		else if (baseFilter instanceof PackageNameFilter) {
			return (Filter<T>) new PreservePackageNameFilter((Filter<String>) wrappedFilter);
		}
		else if (baseFilter instanceof PostDiscoveryFilter) {
			return (Filter<T>) new PreservePostDiscoveryFilter((Filter<TestDescriptor>) wrappedFilter);
		}
		else {
			throw new IllegalArgumentException(
				"Filter interface not found on " + baseFilter.getClass().getSimpleName());
		}
	}

	public abstract static class AbstractPreserveInterfaceFilter<T> implements Filter<T> {

		private Filter<T> _wrappedFilter;

		public AbstractPreserveInterfaceFilter(Filter<T> wrappedFilter) {
			_wrappedFilter = wrappedFilter;
		}

		@Override
		public FilterResult apply(T object) {
			return _wrappedFilter.apply(object);
		}
	}

	public static final class PreserveClassNameFilter extends AbstractPreserveInterfaceFilter<String>
			implements ClassNameFilter {

		public PreserveClassNameFilter(Filter<String> wrappedFilter) {
			super(wrappedFilter);
		}
	}

	public static final class PreservePackageNameFilter extends AbstractPreserveInterfaceFilter<String>
			implements PackageNameFilter {

		public PreservePackageNameFilter(Filter<String> wrappedFilter) {
			super(wrappedFilter);
		}
	}

	public static final class PreservePostDiscoveryFilter extends AbstractPreserveInterfaceFilter<TestDescriptor>
			implements PostDiscoveryFilter {

		public PreservePostDiscoveryFilter(Filter<TestDescriptor> wrappedFilter) {
			super(wrappedFilter);
		}
	}

}

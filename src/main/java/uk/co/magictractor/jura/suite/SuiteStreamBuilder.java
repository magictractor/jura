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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.Filter;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import uk.co.magictractor.jura.suite.annotations.JUnit4SuiteAnnotationReader;
import uk.co.magictractor.jura.suite.filter.ChildPackageFilter;
import uk.co.magictractor.jura.suite.filter.IsSuiteFilter;
import uk.co.magictractor.jura.suite.filter.SamePackageFilter;

/**
 * <p>
 * Runs a JUnit suite as dynamic tests without having to change the test engine.
 * Supports most JUnit suite associated annotations, although support is also
 * provided for creating suites without annotations.
 * </p>
 * <p>
 * Currently these annotations are supported
 * <ul>
 * <li>@SelectClasses</li>
 * <li>@SelectPackage</li>
 * <li>@IncludeClassNamePatterns</li>
 * <li>@ExcludeClassNamePatterns</li>
 * <li>@IncludePackages</li>
 * <li>@ExcludePackages</li>
 * <li>@IncludeTags</li>
 * <li>@ExcludeTags</li>
 * <li>@IncludeEngines</li>
 * <li>@ExcludeEngines</li>
 * </ul>
 * Standard annotations which are NOT currently supported are
 * <ul>
 * <li>TODO</li>
 * </ul>
 * <p>
 * This class will probably be removed once JUnit5 has better support for
 * suites. See https://github.com/junit-team/junit5/issues/744.
 * <ul>
 * <li>TODO! registered extensions should apply to nested suites too (see
 * extension in Attic)</li>
 * </ul>
 */
public class SuiteStreamBuilder {

	private final Class<?> _suiteBaseClass;

	private final Class<?> _suiteInstanceClass;

	private List<DiscoverySelector> _discoverySelectors = new ArrayList<>();

	/**
	 * Filters which are applied to test classes only. These filters are not
	 * applied to nested suites. Whether a test class represents a suite is
	 * determined by _suitePredicate.
	 */
	private List<Filter<?>> _testFilters = new ArrayList<>();

	private boolean _includeAnnotations = true;
	private Predicate<String> _isSuitePredicate = (s) -> s.endsWith("Suite");

	/**
	 * There is no <code>DynamicSuite(Object suite)</code> constructor because
	 * subclasses on the suite may be in a different package.
	 */
	public SuiteStreamBuilder() {
		ExtensionContext context = CaptureContextExtension.remove();
		_suiteBaseClass = context.getRequiredTestMethod().getDeclaringClass();
		_suiteInstanceClass = context.getTestInstance().get().getClass();
	}

	/**
	 * <p>
	 * The test class where this builder instance was created.
	 * </p>
	 * <p>
	 * Methods such as {@link #selectOthersInPackage()} and
	 * {@link #selectSuitesInChildPackages()} are relative to this class.
	 * </p>
	 */
	public Class<?> getSuiteBaseClass() {
		return _suiteBaseClass;
	}

	/**
	 * <p>
	 * The test class being executed. This may be a subclass of _suiteBaseClass.
	 * </p>
	 * <p>
	 * This class, and its ancestors, may have annotations which restrict the
	 * test classes included in this instance of the suite. These annotations
	 * can be useful during development, to focus only on one area of
	 * functionality. Annotations could also be used to permanently remove tests
	 * from an instance of a suite, although Assumptions could be better
	 * solution in that case.
	 * </p>
	 */
	public Class<?> getSuiteInstanceClass() {
		return _suiteInstanceClass;
	}

	public List<DiscoverySelector> getDiscoverySelectors() {
		return _discoverySelectors;
	}

	public List<Filter<?>> getTestFilters() {
		return _testFilters;
	}

	public Predicate<String> isSuitePredicate() {
		return _isSuitePredicate;
	}

	public SuiteStreamBuilder selectOthersInPackage() {
		Collection<Class<?>> selected = selectClasses(new SamePackageFilter(_suiteBaseClass));

		if (selected.isEmpty()) {
			throw new IllegalStateException("Nothing else in package to select");
		}

		return this;
	}

	public SuiteStreamBuilder selectSuitesInChildPackages() {
		Collection<Class<?>> selected = selectClasses(new ChildPackageFilter(_suiteBaseClass),
			new IsSuiteFilter(this::isSuitePredicate));

		if (selected.isEmpty()) {
			throw new IllegalStateException("No child packages to select");
		}

		return this;
	}

	private Collection<Class<?>> selectClasses(Filter<?>... filters) {
		Launcher launcher = LauncherFactory.create();
		LauncherDiscoveryRequest launcherDiscoveryRequest = LauncherDiscoveryRequestBuilder.request().selectors(
			DiscoverySelectors.selectPackage(getSuiteBasePackage())).filters(filters).build();
		TestPlan testPlan = launcher.discover(launcherDiscoveryRequest);
		Collection<Class<?>> selected = getTestClasses(testPlan);

		for (Class<?> testClass : selected) {
			_discoverySelectors.add(DiscoverySelectors.selectClass(testClass));
		}

		return selected;
	}

	private Collection<Class<?>> getTestClasses(TestPlan testPlan) {
		TestIdentifier root = Iterables.getOnlyElement(testPlan.getRoots());
		return testPlan.getChildren(root).stream().map(this::getTestSourceClass).collect(Collectors.toList());
	}

	private Class<?> getTestSourceClass(TestIdentifier testIdentifier) {
		TestSource source = testIdentifier.getSource().get();
		if (source instanceof ClassSource) {
			return ((ClassSource) source).getJavaClass();
		}
		throw new IllegalArgumentException("Code should be modified to handle source type " + source.getClass());
	}

	public SuiteStreamBuilder select(DiscoverySelector discoverySelector) {
		_discoverySelectors.add(discoverySelector);
		return this;
	}

	public SuiteStreamBuilder filter(Filter<?> filter) {
		_testFilters.add(filter);
		return this;
	}

	public SuiteStreamBuilder withoutAnnotations() {
		_includeAnnotations = false;
		return this;
	}

	public SuiteStreamBuilder withAnnotations() {
		_includeAnnotations = true;
		return this;
	}

	private String getSuiteBasePackage() {
		return _suiteBaseClass.getPackage().getName();
	}

	public Stream<DynamicNode> build() {
		// TODO!  guard against reading annotations multiple times
		// if stream() is called more than once
		if (_includeAnnotations) {
			// TODO! SPI? other readers, check whether api project is present
			new JUnit4SuiteAnnotationReader().readAnnotations(this);
		}

		return new DynamicSuiteExecutor().execute(this);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("_suiteBaseClass", _suiteBaseClass.getName()).add(
			"_suiteInstanceClass", _suiteInstanceClass.getName()).toString();
	}

}

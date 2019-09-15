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

package uk.co.magictractor.jura.suite.annotations;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.Filter;
import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.discovery.ClassNameFilter;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.discovery.PackageNameFilter;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.suite.api.ExcludeClassNamePatterns;
import org.junit.platform.suite.api.ExcludeEngines;
import org.junit.platform.suite.api.ExcludePackages;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludePackages;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;

import uk.co.magictractor.jura.suite.SuiteStreamBuilder;
import uk.co.magictractor.jura.suite.filter.IfElseFilter;
import uk.co.magictractor.jura.suite.filter.PreserveInterfaceFilter;

/**
 * <p>
 * Reads the JUnit 4 annotations such as @SelectClasses, @ExcludeTags provided
 * by the junit-platform-suite-api project.
 * </p>
 * <p>
 * The functionality provided by the legacy annotations can be provided by other
 * means, so the use of these annotations is optional and
 * junit-platform-suite-api is not a dependency of this project.
 * </p>
 * <p>
 * TODO! document "other means"
 * </p>
 */
public class JUnit4SuiteAnnotationReader implements SuiteAnnotationReader {

	@Override
	public void readAnnotations(SuiteStreamBuilder builder) {
		// Similar to JUnitPlatform.addFiltersFromAnnotations()
		handleFilterAnnotation(builder, ExcludeClassNamePatterns.class,
			a -> ClassNameFilter.excludeClassNamePatterns(a.value()));
		handleFilterAnnotation(builder, IncludeClassNamePatterns.class,
			a -> ClassNameFilter.includeClassNamePatterns(a.value()));
		handleFilterAnnotation(builder, ExcludePackages.class, a -> PackageNameFilter.excludePackageNames(a.value()));
		handleFilterAnnotation(builder, IncludePackages.class, a -> PackageNameFilter.includePackageNames(a.value()));
		handleFilterAnnotation(builder, ExcludeTags.class, a -> TagFilter.excludeTags(a.value()));
		handleFilterAnnotation(builder, IncludeTags.class, a -> TagFilter.includeTags(a.value()));
		handleFilterAnnotation(builder, ExcludeEngines.class, a -> EngineFilter.excludeEngines(a.value()));
		handleFilterAnnotation(builder, IncludeEngines.class, a -> EngineFilter.includeEngines(a.value()));

		Class<?> suiteInstanceClass = builder.getSuiteInstanceClass();
		handleAnnotation(suiteInstanceClass, SelectClasses.class,
			a -> addDiscoverySelectors(builder, a.value(), v -> DiscoverySelectors.selectClass(v)));
		handleAnnotation(suiteInstanceClass, SelectPackages.class,
			a -> addDiscoverySelectors(builder, a.value(), v -> DiscoverySelectors.selectPackage(v)));
	}

	private <A extends Annotation> void handleFilterAnnotation(SuiteStreamBuilder builder, Class<A> annotationClass,
			Function<A, Filter<?>> filterFunction) {
		handleAnnotation(builder.getSuiteInstanceClass(), annotationClass, (a) -> {
			Filter baseFilter = filterFunction.apply(a);
			// Ignore the filter for suites.
			Filter notSuiteFilter = new IfElseFilter(() -> builder.isSuitePredicate(),
				FilterResult.included("is a suite"), baseFilter);
			// But need to preserve PostDiscoveryFilter and DiscoveryFilter interfaces.
			Filter preserveInterfaceFilter = PreserveInterfaceFilter.preserveInterface(baseFilter, notSuiteFilter);
			builder.filter(preserveInterfaceFilter);
		});
	}

	private <V> void addDiscoverySelectors(SuiteStreamBuilder builder, V[] values,
			Function<V, DiscoverySelector> selectorFunction) {
		for (V value : values) {
			builder.select(selectorFunction.apply(value));
		}
	}

	private <A extends Annotation> void handleAnnotation(Class<?> suiteInstanceClass, Class<A> annotationClass,
			Consumer<A> annotationConsumer) {
		A annotation = suiteInstanceClass.getAnnotation(annotationClass);
		if (annotation != null) {
			annotationConsumer.accept(annotation);
		}
	}

}

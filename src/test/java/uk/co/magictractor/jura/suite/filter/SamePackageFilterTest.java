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

import uk.co.magictractor.jura.suite.filter.SamePackageFilter;

public class SamePackageFilterTest {

    @Test
    public void testSelf() {
        SamePackageFilter filter = new SamePackageFilter("a.b.c.Suite");
        FilterResult filterResult = filter.apply("a.b.c.Suite");
        assertExcluded(filterResult);
    }

    @Test
    public void testSamePackage() {
        SamePackageFilter filter = new SamePackageFilter("a.b.c.Suite");
        FilterResult filterResult = filter.apply("a.b.c.Test");
        assertIncluded(filterResult);
    }

    @Test
    public void testSuiteInSamePackage() {
        SamePackageFilter filter = new SamePackageFilter("a.b.c.Suite");
        FilterResult filterResult = filter.apply("a.b.c.OtherSuite");
        assertIncluded(filterResult);
    }

    @Test
    public void testChildPackage() {
        SamePackageFilter filter = new SamePackageFilter("a.b.c.Suite");
        FilterResult filterResult = filter.apply("a.b.c.d.Test");
        assertExcluded(filterResult);
    }

    @Test
    public void testParentPackage() {
        SamePackageFilter filter = new SamePackageFilter("a.b.c.Suite");
        FilterResult filterResult = filter.apply("a.b.Test");
        assertExcluded(filterResult);
    }

    @Test
    public void testSiblingPackage() {
        SamePackageFilter filter = new SamePackageFilter("a.b.c.Suite");
        FilterResult filterResult = filter.apply("a.b.z.Test");
        assertExcluded(filterResult);
    }

    private void assertIncluded(FilterResult filterResult) {
        assertThat(filterResult.included()).isTrue();
        assertThat(filterResult.getReason().isPresent()).isTrue();
        assertThat(filterResult.getReason().get()).isEqualTo("is in the same package as the suite class");
    }

    private void assertExcluded(FilterResult filterResult) {
        assertThat(filterResult.included()).isFalse();
        assertThat(filterResult.getReason().isPresent()).isTrue();
        assertThat(filterResult.getReason().get()).isEqualTo("is in a different package than the suite class");
    }

}

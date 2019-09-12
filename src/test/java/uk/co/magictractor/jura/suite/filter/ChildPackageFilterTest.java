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

import uk.co.magictractor.jura.suite.filter.ChildPackageFilter;

public class ChildPackageFilterTest {

    @Test
    public void testSamePackage() {
        ChildPackageFilter filter = new ChildPackageFilter("a.b.c.Suite");
        FilterResult filterResult = filter.apply("a.b.c.Test");
        assertExcluded(filterResult);
    }

    @Test
    public void testChildPackage() {
        ChildPackageFilter filter = new ChildPackageFilter("a.b.c.Suite");
        FilterResult filterResult = filter.apply("a.b.c.d.Test");
        assertIncluded(filterResult);
    }

    @Test
    public void testGrandchildPackage() {
        ChildPackageFilter filter = new ChildPackageFilter("a.b.c.Suite");
        FilterResult filterResult = filter.apply("a.b.c.d.e.Test");
        assertExcluded(filterResult);
    }

    @Test
    public void testParentPackage() {
        ChildPackageFilter filter = new ChildPackageFilter("a.b.c.Suite");
        FilterResult filterResult = filter.apply("a.b.Test");
        assertExcluded(filterResult);
    }

    @Test
    public void testSiblingPackage() {
        ChildPackageFilter filter = new ChildPackageFilter("a.b.c.Suite");
        FilterResult filterResult = filter.apply("a.b.z.Test");
        assertExcluded(filterResult);
    }

    private void assertIncluded(FilterResult filterResult) {
        assertThat(filterResult.included()).isTrue();
        assertThat(filterResult.getReason().isPresent()).isTrue();
        assertThat(filterResult.getReason().get()).isEqualTo("is in a child package of the suite class");
    }

    private void assertExcluded(FilterResult filterResult) {
        assertThat(filterResult.included()).isFalse();
        assertThat(filterResult.getReason().isPresent()).isTrue();
        assertThat(filterResult.getReason().get()).isEqualTo("is in a package which is not a child of the suite class");
    }

}

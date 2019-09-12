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

import org.junit.platform.engine.Filter;
import org.junit.platform.engine.FilterResult;

public class IfElseFilter<T> implements Filter<T> {

    private final Supplier<Predicate<T>> _ifPredicateSupplier;

    /** FilterResult returned when the _if Predicate is satisfied. */
    private final Filter<T> _thenFilter;

    /** Filter which is applied when the _if Predicate is not satisfied. */
    private final Filter<T> _elseFilter;

    public IfElseFilter(Supplier<Predicate<T>> ifPredicateSupplier, FilterResult thenResult, Filter<T> elseFilter) {
        _ifPredicateSupplier = ifPredicateSupplier;
        _thenFilter = (object) -> thenResult;
        _elseFilter = elseFilter;
    }

    public IfElseFilter(Supplier<Predicate<T>> ifPredicateSupplier, Filter<T> thenFilter, FilterResult elseResult) {
        _ifPredicateSupplier = ifPredicateSupplier;
        _thenFilter = thenFilter;
        _elseFilter = (object) -> elseResult;
    }

    public IfElseFilter(Supplier<Predicate<T>> ifPredicateSupplier, Filter<T> thenFilter, Filter<T> elseFilter) {
        _ifPredicateSupplier = ifPredicateSupplier;
        _thenFilter = thenFilter;
        _elseFilter = elseFilter;
    }

    @Override
    public FilterResult apply(T object) {
        if (_ifPredicateSupplier.get().test(object)) {
            return _thenFilter.apply(object);
        }
        else {
            return _elseFilter.apply(object);
        }
    }

}

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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionAddValueChange<T> implements ValueChange {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionAddValueChange.class);

    private final Collection<T> _collection;
    private final T _addValue;
    private boolean _alreadyContained;

    public CollectionAddValueChange(Collection<T> collection, T addValue) {
        _collection = collection;
        _addValue = addValue;
    }

    @Override
    public void apply() {
        _alreadyContained = _collection.contains(_addValue);
        if (_alreadyContained) {
            _collection.add(_addValue);
            LOGGER.debug("added value to collection: {}", _addValue);
        }
    }

    @Override
    public void revert() {
        if (!_alreadyContained) {
            _collection.remove(_addValue);
            LOGGER.debug("removed value from collection: {}", _addValue);
        }
    }

}

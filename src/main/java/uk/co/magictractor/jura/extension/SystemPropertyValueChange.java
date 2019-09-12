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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropertyValueChange implements ValueChange {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemPropertyValueChange.class);

    private final String _key;
    private final String _newValue;
    private String _originalValue;

    public SystemPropertyValueChange(String key, String newValue) {
        _key = key;
        _newValue = newValue;
    }

    @Override
    public void apply() {
        _originalValue = System.setProperty(_key, _newValue);
        LOGGER.debug("system property set: {} -> {}", _key, _newValue);
    }

    @Override
    public void revert() {

        if (_originalValue == null) {
            System.clearProperty(_key);
            LOGGER.debug("system property cleared: {}", _key);
        }
        else {
            System.setProperty(_key, _originalValue);
            LOGGER.debug("system property reverted: {} -> {}", _key, _originalValue);
        }
    }

}

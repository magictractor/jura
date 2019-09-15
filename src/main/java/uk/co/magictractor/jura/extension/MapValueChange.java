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

import java.util.Map;

public class MapValueChange<K, V> implements ValueChange {

	private final Map<K, V> _map;
	private final K _key;
	private final V _value;
	private boolean _containsKey;
	private V _originalValue;

	public MapValueChange(Map<K, V> map, K key, V value) {
		_map = map;
		_key = key;
		_value = value;
	}

	@Override
	public void apply() {
		_containsKey = _map.containsKey(_key);
		if (_containsKey) {
			_originalValue = _map.get(_key);
		}
		_map.put(_key, _value);
	}

	@Override
	public void revert() {
		if (_containsKey) {
			_map.put(_key, _originalValue);
		}
		else {
			_map.remove(_key);
		}
	}

}

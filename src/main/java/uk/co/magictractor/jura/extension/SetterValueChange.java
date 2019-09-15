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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SetterValueChange<T> implements ValueChange {

	private final Function<T, T> _setter;
	private final T _newValue;
	private T _originalValue;

	public SetterValueChange(Supplier<T> getter, Consumer<T> setter, T newValue) {
		_setter = value -> {
			T previousValue = getter.get();
			setter.accept(value);
			return previousValue;
		};
		_newValue = newValue;
	}

	public SetterValueChange(Function<T, T> setter, T newValue) {
		_setter = setter;
		_newValue = newValue;
	}

	@Override
	public void apply() {
		_originalValue = _setter.apply(_newValue);
	}

	@Override
	public void revert() {
		_setter.apply(_originalValue);
	}

}

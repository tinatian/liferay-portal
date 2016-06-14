/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.util.Assert;

/**
 * {@link ThreadLocal} subclass that exposes a specified name
 * as {@link #toString()} result (allowing for introspection).
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 * @see NamedInheritableThreadLocal
 */
public class NamedThreadLocal<T> extends ThreadLocal<T> {

	/**
	 * Create a new NamedThreadLocal with the given name.
	 * @param name a descriptive name for this ThreadLocal
	 */
	public NamedThreadLocal(String name) {
		_hashCode = _nextHasCode.getAndAdd(_HASH_INCREMENT);

		Assert.hasText(name, "Name must not be empty");
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		return false;
	}

	@Override
	public T get() {
		ThreadLocalMap threadLocalMap = _getThreadLocalMap();

		Entry entry = threadLocalMap.getEntry(this);

		if (entry == null) {
			T value = initialValue();

			threadLocalMap.putEntry(this, value);

			return value;
		}
		else {
			return (T)entry._value;
		}
	}

	@Override
	public int hashCode() {
		return _hashCode;
	}

	@Override
	public void remove() {
		ThreadLocalMap threadLocalMap = _getThreadLocalMap();

		threadLocalMap.removeEntry(this);
	}

	@Override
	public void set(T value) {
		ThreadLocalMap threadLocalMap = _getThreadLocalMap();

		threadLocalMap.putEntry(this, value);
	}

	@Override
	public String toString() {
		return this.name;
	}

	protected T copy(T value) {
		if (value != null) {
			Class<?> clazz = value.getClass();

			if (_immutableTypes.contains(clazz)) {
				return value;
			}
		}

		return null;
	}

	private static Map<NamedThreadLocal<?>, Object> _toMap(
		ThreadLocalMap threadLocalMap) {

		Map<NamedThreadLocal<?>, Object> map =
			new HashMap<NamedThreadLocal<?>, Object>(
				threadLocalMap._table.length);

		for (Entry entry : threadLocalMap._table) {
			if (entry != null) {
				NamedThreadLocal<Object> namedThreadLocal =
					(NamedThreadLocal<Object>)entry._key;

				Object value = namedThreadLocal.copy(entry._value);

				if (value != null) {
					map.put(namedThreadLocal, value);
				}
			}
		}

		return map;
	}

	private ThreadLocalMap _getThreadLocalMap() {
		return _threadLocals.get();
	}

	private static final int _HASH_INCREMENT = 0x61c88647;

	private static final Set<Class<?>> _immutableTypes = new HashSet<Class<?>>();
	private static final AtomicInteger _nextHasCode = new AtomicInteger();
	private static final ThreadLocal<ThreadLocalMap> _threadLocals =
		new ThreadLocalMapThreadLocal();

	private final String name;

	static {
		_immutableTypes.add(Boolean.class);
		_immutableTypes.add(Byte.class);
		_immutableTypes.add(Character.class);
		_immutableTypes.add(Short.class);
		_immutableTypes.add(Integer.class);
		_immutableTypes.add(Long.class);
		_immutableTypes.add(Float.class);
		_immutableTypes.add(Double.class);
		_immutableTypes.add(String.class);
	}

	private final int _hashCode;

	private static class Entry {

		public Entry(NamedThreadLocal<?> key, Object value, Entry next) {
			_key = key;
			_value = value;
			_next = next;
		}

		private NamedThreadLocal<?> _key;
		private Entry _next;
		private Object _value;

	}

	private static class ThreadLocalMap {

		public void expand(int newCapacity) {
			if (_table.length == _MAXIMUM_CAPACITY) {
				_threshold = Integer.MAX_VALUE;

				return;
			}

			Entry[] newTable = new Entry[newCapacity];

			for (int i = 0; i < _table.length; i++) {
				Entry entry = _table[i];

				if (entry == null) {
					continue;
				}

				_table[i] = null;

				do {
					Entry nextEntry = entry._next;

					int index = entry._key._hashCode & (newCapacity - 1);

					entry._next = newTable[index];

					newTable[index] = entry;

					entry = nextEntry;
				}
				while (entry != null);
			}

			_table = newTable;

			_threshold = newCapacity * 2 / 3;
		}

		public Entry getEntry(NamedThreadLocal<?> key) {
			int index = key._hashCode & (_table.length - 1);

			Entry entry = _table[index];

			if (entry == null) {
				return null;
			}

			if (entry._key == key) {
				return entry;
			}

			while ((entry = entry._next) != null) {
				if (entry._key == key) {
					return entry;
				}
			}

			return null;
		}

		public void putEntry(NamedThreadLocal<?> key, Object value) {
			int index = key._hashCode & (_table.length - 1);

			for (Entry entry = _table[index]; entry != null;
				entry = entry._next) {

				if (entry._key == key) {
					entry._value = value;

					return;
				}
			}

			_table[index] = new Entry(key, value, _table[index]);

			if (_size++ >= _threshold) {
				expand(2 * _table.length);
			}
		}

		public void removeEntry(NamedThreadLocal<?> key) {
			int index = key._hashCode & (_table.length - 1);

			Entry previousEntry = null;

			Entry entry = _table[index];

			while (entry != null) {
				Entry nextEntry = entry._next;

				if (entry._key == key) {
					_size--;

					if (previousEntry == null) {
						_table[index] = nextEntry;
					}
					else {
						previousEntry._next = nextEntry;
					}

					return;
				}

				previousEntry = entry;
				entry = nextEntry;
			}
		}

		private static final int _INITIAL_CAPACITY = 16;

		private static final int _MAXIMUM_CAPACITY = 1 << 30;

		private int _size;
		private Entry[] _table = new Entry[_INITIAL_CAPACITY];
		private int _threshold = _INITIAL_CAPACITY * 2 / 3;

	}

	private static class ThreadLocalMapThreadLocal
		extends ThreadLocal<ThreadLocalMap> {

		@Override
		protected ThreadLocalMap initialValue() {
			return new ThreadLocalMap();
		}

	}

}

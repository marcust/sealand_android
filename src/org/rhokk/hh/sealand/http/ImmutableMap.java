package org.rhokk.hh.sealand.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImmutableMap {

	public static <K,V> Map<K,V> of(K key, V value) {
		final Map<K,V> map = new HashMap<K, V>();
		map.put( key , value);
		return Collections.unmodifiableMap( map );
	}

}

package com.hand.bdss.dsmp.model;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

public class TupleMap<K, V> extends ForwardingMap<K, V> implements Serializable {

	private static final long serialVersionUID = 8411669255909283310L;

	private Map<K, V> map = null;

	public TupleMap() {
		this(Maps.<K, V> newHashMap());
	}

	public TupleMap(Map<K, V> map) {
		super();
		this.map = map;
	}

	@Override
	protected Map<K, V> delegate() {
		return map;
	}

	@Override
	public String toString() {
		return map + "";
	}

}

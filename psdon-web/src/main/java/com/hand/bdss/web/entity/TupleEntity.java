package com.hand.bdss.web.entity;

import java.io.Serializable;

public class TupleEntity<T,U> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2442490224832468418L;
	
	private T value_01;
	private U value_02;
	
	public TupleEntity(T value_01,U value_02) {
		this.value_01 = value_01;
		this.value_02 = value_02;
	}
	public T getValue_01() {
		return value_01;
	}
	public void setValue_01(T value_01) {
		this.value_01 = value_01;
	}
	public U getValue_02() {
		return value_02;
	}
	public void setValue_02(U value_02) {
		this.value_02 = value_02;
	}
	
	

}

package com.hand.bdss.dsmp.component;

import com.hand.bdss.dsmp.exception.DataServiceException;

public abstract class Component {

	public abstract void init() throws DataServiceException;

	public abstract Object getConnection() throws DataServiceException;

	public abstract void closeConnection() throws DataServiceException;

}

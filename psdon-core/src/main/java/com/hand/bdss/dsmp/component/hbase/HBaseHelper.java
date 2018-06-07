package com.hand.bdss.dsmp.component.hbase;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.dsmp.component.Component;
import com.hand.bdss.dsmp.exception.DataServiceException;

/**
 * HBase client
 * 
 * @author William
 *
 */
@SuppressWarnings("deprecation")
public class HBaseHelper extends Component {

	private static final Logger logger = LoggerFactory.getLogger(HBaseHelper.class);

	private HConnection connection = null;

	private Configuration conf = null;

	@Override
	public void init() throws DataServiceException {
		logger.info("start init HBasehelper...");
		conf = HBaseConfiguration.create();
		try {
			connection = HConnectionManager.createConnection(conf);
		} catch (IOException e) {
			throw new DataServiceException("init HBasehelper error!", e);
		}
		logger.info("init HBasehelper successed!");
	}

	@Override
	public synchronized HConnection getConnection() throws DataServiceException {
		try {
			if (connection == null) {
				connection = HConnectionManager.createConnection(conf);
			}
		} catch (IOException e) {
			throw new DataServiceException("init HBasehelper error!", e);
		}
		return connection;
	}

	@Override
	public synchronized void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (IOException e) {
			}
		}
		connection = null;
	}

	public void put(String tableName, Put put) throws IOException {
		if (put.isEmpty()) {
			return;
		}

		HTableInterface tableInterface = null;
		try {
			tableInterface = getConnection().getTable(tableName);
			tableInterface.put(put);
		} catch (Exception e) {
			closeConnection();
			logger.error("", e);
		} finally {
			if (tableInterface != null) {
				tableInterface.close();
			}
		}
	}

	public void put(String tableName, List<Put> puts) throws IOException {
		if (puts == null || puts.size() == 0) {
			return;
		}

		HTableInterface tableInterface = null;
		try {
			tableInterface = getConnection().getTable(tableName);
			tableInterface.put(puts);
		} catch (Exception e) {
			closeConnection();
			logger.error("", e);
		} finally {
			if (tableInterface != null) {
				tableInterface.close();
			}
		}
	}

	public Result getResult(String tableName, String rowKey, List<String> columns) throws IOException {
		Get get = new Get(Bytes.toBytes(rowKey));
		for (String s : columns) {
			get.addColumn(HTableManager.DEFAULT_FAMILY_NAME, Bytes.toBytes(s));
		}

		Result result = null;
		HTableInterface tableInterface = null;
		try {
			tableInterface = getConnection().getTable(tableName);
			result = tableInterface.get(get);
		} catch (Exception e) {
			closeConnection();
			logger.error("", e);
		} finally {
			if (tableInterface != null) {
				tableInterface.close();
			}
		}
		return result;
	}

}
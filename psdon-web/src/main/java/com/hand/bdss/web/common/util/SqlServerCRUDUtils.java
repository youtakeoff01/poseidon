package com.hand.bdss.web.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
/**
 * sqlServer 数据库操作类
 * @author liqifei
 * @DATA 2018年1月3日
 */
public class SqlServerCRUDUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(SqlServerCRUDUtils.class);
	
	private DruidDataSource dataSource;
	
	public DruidDataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DruidDataSource dataSource) {
		this.dataSource = dataSource;
	}
	/**
	 * 获取连接
	 * @return
	 */
	public Connection getConn() {
		Connection conn = null;
		try {
			conn =  dataSource.getConnection();
		} catch (SQLException e) {
			logger.error("get connection from sqlserver error，error msg is",e);
		}
		return conn;
	} 
	
	/**
	 * 插入powbi的sqlserver 权限表
	 * @param stuID 学生的ID
	 * @param teaAD 申请老师的AD
	 * @return
	 */
	public boolean insertJurisdictionTable(List<Map<String,String>> list) {
		Connection conn = getConn();
		 //设置事务属性  
		ResultSet set = null;
		PreparedStatement sta = null;
		boolean boo = true;
		try {
			conn.setAutoCommit(false);  
			String sql = "insert into seciet_controller(studentid,daaccount,windows_adaccount,create_time) values(?,?,?,?)";
			sta = conn.prepareStatement(sql);
			for (Map<String, String> map : list) {
				sta.setString(1, map.get("stuID"));
				sta.setString(2, map.get("teaAD"));
				sta.setString(3, "XJTLU\\"+map.get("teaAD"));
				sta.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				sta.addBatch();
			}
			sta.executeBatch();
			conn.commit();
		} catch (Exception e) {
			boo = false;
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error("conn.rollback() error，error msg is",e1);
			} 
			logger.error("insertJurisdictionTable error，error msg is",e);
		}finally {
			close(conn,sta,set);
		}
		return boo;
	}
	/**
	 * 刪除申請老師的權限
	 * @param teaAD 老师ad
	 * @param sid 学生学号
	 * @return
	 */
	public boolean updateJurisdictionTable(String teaAD,String sid) {
		Connection conn = getConn();
		ResultSet set = null;
		PreparedStatement sta = null;
		boolean boo = true;
		try {
			String sql = "delete from seciet_controller  where daaccount = ? and studentid = ?";
			sta = conn.prepareStatement(sql);
			sta.setString(1, teaAD);
			sta.setString(2, sid);
			set = sta.executeQuery();
			int count = 0;
			if(set.next()) {
				count = set.getInt(1);
			}
			if(count<1) {
				boo = false;
			}
		} catch (Exception e) {
			logger.error("updateJurisdictionTable error，error msg is",e);
		}finally {
			close(conn,sta,set);
		}
		return boo;
	}
	
	
	/**
	 * 根据条件查询学生信息（用来验证用户填写的 学生学号和学生ad或者学生学号和学生名称的对应关系是否正确）
	 * @param stuID
	 * @param stuAD
	 * @param stuName
	 * @return true 表示根据条件查找到对应的数据了，反之是没有查询到对应的数据
	 */
	public boolean getStuMsg(String stuID,String stuAD,String stuName) {
		Connection conn = getConn();
		ResultSet set = null;
		PreparedStatement sta = null;
		boolean boo = true;
		try {
			String sql = null;
			if(stuAD!=null) {
				sql = "select count(1) from M_STUDENT_SPR_ALL a where a.ID = "+stuID+" and a.ADACCOUNT='"+stuAD+"'";
			}
			if(stuName!=null) {
				sql = "select count(1) from M_STUDENT_SPR_ALL a where a.ID = "+stuID+" and (a.CHINESENAME=N'"+stuName+"' or a.ENGLISHNAME=N'"+stuName+"')";
			}
			sta = conn.prepareStatement(sql);
			set = sta.executeQuery();
			int count = 0;
			if(set.next()) {
				count = set.getInt(1);
			}
			if(count<1) {
				boo = false;
			}
		} catch (Exception e) {
			logger.error("getStuMsg error，error msg is",e);
		}finally {
			close(conn,sta,set);
		}
		return boo;
	}
	
	/**
	 * 关闭连接
	 * @param conn
	 */
	public void close(Connection conn,PreparedStatement sta,ResultSet set) {
		try {
			if(set!=null) {
				set.close();
			}
			if(sta!=null) {
				sta.close();
			}
			if(conn!=null) {
				conn.close();
			}
		} catch (Exception e) {
			logger.error("close sqlserver connection error，error msg is：",e);
		}
	}
}

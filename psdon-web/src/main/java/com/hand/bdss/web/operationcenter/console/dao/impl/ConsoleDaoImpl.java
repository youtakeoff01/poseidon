package com.hand.bdss.web.operationcenter.console.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hand.bdss.dsmp.model.ServiceStatus;
import com.hand.bdss.web.common.util.DataConnectionUtils;
import com.hand.bdss.web.common.util.DateUtil;
import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.common.vo.DBSrcVO;
import com.hand.bdss.web.entity.ComStateNumEntity;
import com.hand.bdss.web.entity.DatabaseConstant;
import com.hand.bdss.web.operationcenter.console.dao.ConsoleDao;
import com.hand.bdss.web.operationcenter.console.vo.TaskExecVO;
import com.hand.bdss.web.operationcenter.console.vo.TaskQuantityVO;

@Repository
public class ConsoleDaoImpl extends SupportDaoUtils implements ConsoleDao {
	
    private static DBSrcVO dbSrcVO = null;
        
	private static final String MAPPERURL = "com.hand.bdss.dsmp.model.ServiceStatus.";
	@Override
	public int insertServiceStatus(ServiceStatus service) throws Exception {
		int i =getSqlSession().insert(MAPPERURL+"insertServiceStatus",service);
		return i;
	}

	@Override
	public List<ServiceStatus> getServiceStatus(ServiceStatus service) throws Exception {
		List<ServiceStatus> list = getSqlSession().selectList(MAPPERURL+"getServiceStatus",service);
		return list;
	}

	@Override
	public List<ServiceStatus> listServiceStatusAll(ServiceStatus service) throws Exception {
		List<ServiceStatus> list = getSqlSession().selectList(MAPPERURL+"listServiceStatusAll",service);
		return list;
	}

	@Override
	public int updateServiceStatus(ServiceStatus service) throws Exception {
		int i = getSqlSession().update(MAPPERURL+"updateServiceStatus",service);
		return i;
	}

	@Override
	public List<ComStateNumEntity> getServiceComState(ServiceStatus service) throws Exception {
		List<ComStateNumEntity> list = getSqlSession().selectList(MAPPERURL+"getServiceComState",service);
		return list;
	}

	/**
	 * 任务执行情况
	 */
	@Override
	public Map<String, Integer> getTaskExec() throws Exception {
        Map<String,Integer> map = new HashMap<>();
	 	Connection conn = null;
	 	try{
	 		conn = this.getCon();
	 		//封装SQL
		 	String sql = this.parseSQL("exec");
	        PreparedStatement stat = conn.prepareStatement(sql);
	        ResultSet result = stat.executeQuery();
	        while (result.next()) {
	            String status = result.getString(1);
	            int count = result.getInt(2);
	            map.put(status, count);
	        }
	 	}catch(Exception e){
	 		e.printStackTrace();
	 	}finally{
	 		closeConn(conn);
	 	}
		return map;
	}
	
	/**
	 * 获取执行时间
	 * @return
	 */
	@Override
	public List<TaskExecVO> getExecTime() throws Exception{
        List<TaskExecVO> list = new ArrayList<TaskExecVO>();
		Connection conn = null;
		try{
			conn = this.getCon();
			 //封装SQL
		 	String sql = this.parseSQL("time");
	        PreparedStatement stat = conn.prepareStatement(sql);
	        ResultSet result = stat.executeQuery();
	        while (result.next()) {
	            String taskName = result.getString(1);
	            long execTime = result.getLong(2);
	            //将执行时长添加到list集合中
	        	TaskExecVO taskExec = new TaskExecVO();
	            taskExec.setTaskName(taskName);
	            taskExec.setExecTime(DateUtil.praseExcTime(execTime));
	            list.add(taskExec);
	        }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeConn(conn);
		}
		return list;
	}
	
	
	/**
	 * 获取执行错误次数
	 */
	@Override
	public List<TaskExecVO> getErrorCount() throws Exception{
        List<TaskExecVO> list = new ArrayList<TaskExecVO>();
		Connection conn = null;
		try{
			conn = this.getCon();
			 //封装SQL
		 	String sql = this.parseSQL("error");
	        PreparedStatement stat = conn.prepareStatement(sql);
	        ResultSet result = stat.executeQuery();
	        while (result.next()) {
	            String taskName = result.getString(1);
	            int count = result.getInt(2);
	            //将执行时长添加到list集合中
	        	TaskExecVO taskExec = new TaskExecVO();
	            taskExec.setTaskName(taskName);
	            taskExec.setErrorCount(count);
	            list.add(taskExec);
	        }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeConn(conn);
		}
		return list;
	}
	
	/**
	 * 获取当天任务数量
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<TaskQuantityVO> getTaskCount(List<String> typeList) throws Exception{
		List<TaskQuantityVO> taskList = new ArrayList<>();
		int [] args = new int[]{0,1};
		Connection conn = null;
		try{
			for (int i : args) {
				conn = this.getCon();
		        //封装SQL
			 	String sql = this.parseSQL("count",i,typeList);
		        PreparedStatement stat = conn.prepareStatement(sql);
		        ResultSet result = stat.executeQuery();
	        	//添加TimeQuantityVO
		        TaskQuantityVO taskQuantity = new TaskQuantityVO();
	        	Map<String, Integer> map = new HashMap<String, Integer>();
		        while (result.next()) {
		        	String date = result.getString(1);
		        	int count = result.getInt(2);
		        	map.put(date, count);
		        }
		        taskQuantity.setTimeType(String.valueOf(i));
		        taskQuantity.setMap(map);
		        taskList.add(taskQuantity);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeConn(conn);
		}
		return taskList;
	}
	
	
	 /**
     * 获取阿兹卡班con
     *
     * @return
     * @throws Exception
     */
    private synchronized Connection getCon() throws Exception {
        try {
            if (null == dbSrcVO) {
                dbSrcVO = new DBSrcVO();
                dbSrcVO.setDbName(DatabaseConstant.DBNAME);
                dbSrcVO.setDbUrl(DatabaseConstant.DBURL);
                dbSrcVO.setDbUser(DatabaseConstant.DBUSERNAME);
                dbSrcVO.setDbPwd(DatabaseConstant.DBPWD);
                dbSrcVO.setDbType(DatabaseConstant.DBTYPE);
            }
            return DataConnectionUtils.getConnection(dbSrcVO);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
    
    
    /**
	 * 关闭数据库连接
	 */
	public static void closeConn(Connection con){
		if(con!=null){
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		con = null;
	}
	
	
	/**
	 * 解析SQL语句
	 * @param type
	 * @return
	 */
	private String parseSQL(String type,Integer arg,List<String> typeList){
		StringBuffer buffer = new StringBuffer("");
		if("exec".equals(type)){
			buffer.append("select case status");
			buffer.append(" when 30 then 'running' when 50 then 'succeed' when 70 then 'failed' else 'unfinished' end as status,");
			buffer.append(" count(1) as count from execution_jobs group by status");
		}
		if("time".equals(type)){
			buffer.append("select flow_id,(end_time - start_time) as time from execution_jobs order by time desc limit 0,20");
		}
		if("error".equals(type)){
			buffer.append("select flow_id,count(1) as count from execution_jobs ");
			buffer.append(" where status = 70 and");
			buffer.append(" TO_DAYS(date_add(curdate(), interval - day(curdate()) + 1 month)) < TO_DAYS(from_unixtime(start_time/1000))");
			buffer.append(" group by flow_id order by count desc limit 0,20");
		}
		if("count".equals(type)){
			buffer.append("SELECT DATE_FORMAT(FROM_UNIXTIME(start_time/1000),'%H') as date,count(exec_id) as count FROM execution_jobs");
			buffer.append(" WHERE DATE_FORMAT(NOW(),'%y%m%d') - DATE_FORMAT(FROM_UNIXTIME(start_time/1000),'%y%m%d') = " + arg);
			buffer.append(" and substr(flow_id,position('-' IN flow_id) + 1,1) in (");
			for(String taskType : typeList){
				buffer.append("'"+taskType+"',");
			}
			buffer = new StringBuffer(buffer.substring(0, buffer.lastIndexOf(",")));
			buffer.append(")");		
			buffer.append(" GROUP BY date");
		}
		return buffer.toString();
	}
	
	private String parseSQL(String type){
		return parseSQL(type,null,null);
	}
	

}

package com.hand.bdss.web.operationcenter.history.dao.impl;

import com.hand.bdss.web.common.em.TaskStatus;
import com.hand.bdss.web.common.em.TaskType;
import com.hand.bdss.web.common.util.DataConnectionUtils;
import com.hand.bdss.web.common.vo.DBSrcVO;
import com.hand.bdss.web.entity.DataSyncHistoryEntity;
import com.hand.bdss.web.entity.DatabaseConstant;
import com.hand.bdss.web.operationcenter.history.dao.DataTaskHistoryDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class DataTaskHistoryDaoImpl implements DataTaskHistoryDao {

    private static DBSrcVO dbSrcVO = null;

    @Override
    public List<DataSyncHistoryEntity> selectList(Map<String, Object> parmMap) throws Exception {
        List<DataSyncHistoryEntity> lists = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet result = null;
        try {
            conn = this.getCon();
            //封装SQL
            String sql = this.processSQL(parmMap);
            stat = conn.prepareStatement(sql);
            result = stat.executeQuery();
            while (result.next()) {
                String execId = result.getString(1);
                String execJobName = result.getString(2);
                String jobState = result.getString(3);
                String startTime = result.getString(4);
                String endTime = result.getString(5);
                String excTime = "";
                if (jobState != null && "30".equalsIgnoreCase(jobState)) {
                    endTime = "--";
                } else {
                    excTime = this.praseExcTime(startTime, endTime);//分析执行时间
                }
                jobState = this.praseTaskState(jobState);//解析任务状态
                DataSyncHistoryEntity dataSyncHistory = new DataSyncHistoryEntity();
                dataSyncHistory.setExecJobName(execJobName);
                dataSyncHistory.setJobName(this.processJobName(execJobName));
                dataSyncHistory.setJobState(jobState);
                dataSyncHistory.setStartTime(Long2Date(startTime));
                dataSyncHistory.setEndTime("--".equals(endTime) ? endTime : Long2Date(endTime));
                dataSyncHistory.setExecId(execId);
                dataSyncHistory.setExecuteTime(excTime);
                dataSyncHistory.setJobType(this.praseTaskTypeName(execJobName));
                dataSyncHistory.setSqlType(this.praseTaskSqlName(execJobName));
                lists.add(dataSyncHistory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResultSet(result);
            JdbcUtils.closeStatement(stat);
            JdbcUtils.closeConnection(conn);
        }
        return lists;
    }


    @Override
    public int selectCount(Map<String, Object> parmMap) throws Exception {
        int count = 0;
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet result = null;
        try {
            conn = this.getCon();
            conn = this.getCon();
            //封装SQL
            parmMap.put("type", "count");
            String sql = this.processSQL(parmMap);
            stat = conn.prepareStatement(sql);
            result = stat.executeQuery();
            while (result.next()) {
                count = result.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResultSet(result);
            JdbcUtils.closeStatement(stat);
            JdbcUtils.closeConnection(conn);

        }
        return count;
    }

    @Override
    public DataSyncHistoryEntity selectRecordLast(String jobName) throws Exception {
        DataSyncHistoryEntity entity = new DataSyncHistoryEntity();
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet result = null;
        try {
            conn = this.getCon();
            //封装SQL
            String sql = this.processRecordLastSQL(jobName);
            stat = conn.prepareStatement(sql);
            result = stat.executeQuery();
            while (result.next()) {
                String execId = result.getString(1);
                String jobState = result.getString(2);
                entity.setJobName(jobName);
                entity.setJobState(jobState);
                entity.setExecId(execId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResultSet(result);
            JdbcUtils.closeStatement(stat);
            JdbcUtils.closeConnection(conn);
        }
        return entity;
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
     * 查询任务最近一次执行信息
     *
     * @param jobName
     * @return
     */
    private String processRecordLastSQL(String jobName) {
        if (StringUtils.isEmpty(jobName)) {
            return null;
        }
        StringBuffer sql = new StringBuffer("");
        sql.append("select job.job_id jobName, job.status jobState");
        sql.append(" from execution_jobs job ");
        sql.append(" where job.job_id = '" + jobName + "'");
        sql.append(" order by job.start_time desc limit 1");
        return sql.toString();
    }

    /**
     * 封装SQL
     *
     * @param parmMap
     * @return
     */
    private String processSQL(Map<String, Object> parmMap) {
        if (parmMap == null) {
            return null;
        }
        StringBuffer sql = new StringBuffer("");
        Integer startPage = Integer.parseInt(parmMap.get("startPage").toString()) - 1;
        Integer count = Integer.parseInt(parmMap.get("count").toString());
        String type = parmMap.get("type") + "";
        if ("count".equals(type)) {
            sql.append("select count(1)");
        } else {
            sql.append("select job.exec_id execId, job.job_id jobName, job.status jobState, job.start_time startTime,job.end_time endTime ");
        }
        sql.append(" from execution_jobs job ");
        Object jobName = parmMap.get("jobName");
        Object startTime = parmMap.get("startTime");
        Object endTime = parmMap.get("endTime");
        Object jobState = parmMap.get("jobState");
        String executeType = parmMap.get("executeType") + "";

        sql.append(" where 1=1");
        if (!StringUtils.isEmpty(executeType) && "0".equalsIgnoreCase(executeType)) {            //执行完成后记录
            if (null == startTime || "".equals(startTime)) {
                sql.append(" and job.start_time > " + Date2LongForToday(0, 0, 0));
            }
            if (null == jobState || "".equals(startTime)) {
                sql.append(" and job.status in ('50','60','70')");
            }
        }
        if (null != jobName && !"".equals(jobName)) {
            sql.append(" and SUBSTRING_INDEX(job.job_id,'-',1) COLLATE latin1_bin like \'%" + jobName + "%\'");
        }
        if (null != startTime && !"".equals(startTime)) {
            sql.append(" and job.start_time > " + startTime);
        }
        if (null != endTime && !"".equals(endTime)) {
            sql.append(" and job.end_time < " + endTime);
        }
        if (null != jobState && !"".equals(jobState)) {
            sql.append(" and job.status =" + jobState);
        }
        sql.append(" order by job.start_time desc");
        if (!"count".equals(type))
            sql.append(" limit " + startPage + "," + count);
        return sql.toString();
    }

    /**
     * 解析执行时间
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private String praseExcTime(String startTime, String endTime) {
        String retTime = "";
        try {
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            long ns = 1000;
            Long subTime = Long.valueOf(endTime) - Long.valueOf(startTime);
            // 计算差多少天
            long day = subTime / nd;
            if (day > 0) {
                retTime = day + "天";
            }
            // 计算差多少小时
            long hour = subTime % nd / nh;
            if (hour > 0) {
                retTime = retTime + hour + "小时";
            }
            // 计算差多少分钟
            long min = subTime % nd % nh / nm;
            if (min > 0) {
                retTime = retTime + min + "分";
            }
            // 计算差多少秒
            long ss = subTime % nd % nh / nm / ns;
            if (ss > 0) {
                retTime = retTime + ss + "秒";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retTime;
    }

    /**
     * 解析任务状态
     *
     * @param jobState
     * @return
     */
    private String praseTaskState(String jobState) {

        Integer jobStyle = Integer.parseInt(jobState);
        if (TaskStatus.success.getIndex() == jobStyle) {
            return TaskStatus.success.getName();
        }
        if (TaskStatus.fail.getIndex() == jobStyle) {
            return TaskStatus.fail.getName();
        }
        if (TaskStatus.run.getIndex() == jobStyle) {
            return TaskStatus.run.getName();
        }
        if (TaskStatus.cancel.getIndex() == jobStyle) {
            return TaskStatus.cancel.getName();
        }
        return null;
    }

    /**
     * 根据任务名称
     * 解析任务类型
     *
     * @param jobName
     * @return
     */
    private String praseTaskTypeName(String jobName) {
        Integer jobStyle = null;
        try {
            jobStyle = Integer.parseInt(jobName.split("-")[1]);
        } catch (Exception e) {
            return null;
        }

        if (TaskType.SCRIPT.getIndex() == jobStyle) {
            return TaskType.SCRIPT.getName();
        }
        if (TaskType.SYNC.getIndex() == jobStyle) {
            return TaskType.SYNC.getName();
        }
        if (TaskType.AI.getIndex() == jobStyle) {
            return TaskType.AI.getName();
        }
        return null;
    }

    /**
     * 解析任务名称
     *
     * @param jobName
     * @return
     */
    private String processJobName(String jobName) {
        if (null == jobName) {
            return null;
        }
        return jobName.split("-")[0];
    }

    /**
     * 根据任务名称
     * 解析SQL类型
     *
     * @param jobName
     * @return
     */
    private String praseTaskSqlName(String jobName) {
        try {
            return jobName.split("-")[2];

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当天特定时间
     *
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    private Long Date2LongForToday(int hour, int minute, int second) {
        Calendar c1 = new GregorianCalendar();
        c1.set(Calendar.HOUR_OF_DAY, hour);
        c1.set(Calendar.MINUTE, minute);
        c1.set(Calendar.SECOND, second);
        Date date = (Date) c1.getTime();
        long time = Long.valueOf(date.getTime());
        return time;
    }

    /**
     * 将时间字符串转化为Date类型
     *
     * @param str
     * @return
     */
    private String Long2Date(String str) {
        long time = Long.valueOf(str);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return sdf.format(date);
    }


    /**
     * 关闭数据库连接
     */
    public static void closeConn(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        con = null;
    }

    public static void main(String[] args) {
        DataTaskHistoryDaoImpl dd = new DataTaskHistoryDaoImpl();
        //Date转化为long类型
        System.out.println(dd.Date2LongForToday(0, 0, 0));

        //long转化为date类型
        System.out.println(dd.Long2Date("1503849650729"));
    }
}

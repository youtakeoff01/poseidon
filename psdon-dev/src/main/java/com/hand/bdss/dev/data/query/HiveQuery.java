package com.hand.bdss.dev.data.query;

import com.hand.bdss.dev.util.ScriptUtil;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dsmp.component.hive.HiveClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;
import  com.hand.bdss.dsmp.config.SystemConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @author : Koala
 * @version : v1.0
 * @description :
 */
public class HiveQuery {
    private static final Logger logger = LoggerFactory.getLogger(HiveQuery.class);


    /**
     * 获取查询结果
     *
     * @param task
     * @return
     */
    public static List<Map<String, String>> getQuery(Task task) {
        logger.info("HiveQuery.getQuery param= { " + task.getSqlStc() + " }");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Map<String, String>> listQuery = new ArrayList<>();
        try {
            String taskSql = ScriptUtil.CutOutSQL(task.getSqlStc());
            String sql = "SELECT * FROM ( " + taskSql + " ) as devtb LIMIT 10";

            HiveClient hiveClient = new HiveClient(SystemConfig.userName);
            conn = hiveClient.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columns = metaData.getColumnCount();
            Map<String, String> schameAndData = null;
            while (rs.next()) {
                schameAndData = new LinkedHashMap<>();
                for (int i = 1; i <= columns; i++) {
                    schameAndData.put(metaData.getColumnName(i).replace("devtb.", ""), rs.getString(i) + "");
                }
                listQuery.add(schameAndData);
            }

        } catch (Exception e) {
            Map<String, String> retMap = new LinkedHashMap<>();
            retMap.put("retCode", "0");
            listQuery.add(retMap);
            logger.error("HiveQuery.getQuery :", e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(stmt);
            JdbcUtils.closeConnection(conn);
        }
        logger.info("HiveQuery.getQuery end!");
        return listQuery;
    }

}

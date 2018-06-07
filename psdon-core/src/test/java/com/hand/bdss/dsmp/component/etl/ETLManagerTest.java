package com.hand.bdss.dsmp.component.etl;

import com.hand.bdss.dsmp.component.hive.HiveClient;
import com.hand.bdss.dsmp.service.etl.ETLManager;

/**
 * Created by hasee on 2017/8/29.
 */
public class ETLManagerTest {
    public static void main(String[] args) throws Exception{
        //数据同步方式 syncType 0全量 1增量

//        String json = "[{\"etlConfig\":{\"jobName\":\"sqooptest002\",\"syncDB\":\"bigdata_dw\",\"jobId\":\"71b07143-0f09-48fe-b3f4-c017c018c129\",\"syncSource\":\"sqooptest001\",\"partition\":\"updatetime\",\"is_partition\":\"1\",\"dataType\":\"MySQL\",\"jobType\":\"0\",\"syncType\":\"0\"},\"action\":\"0\",\"jobTask\":{\"shellTaskName\":\"script001fb\",\"shellSQL\":\"job-->script\",\"runtimes\":\"1\",\"errortimes\":\"1\",\"failedRolling\":\"flase\",\"executeBySuccess\":\"flase\",\"executeTime\":\"1\"},\"dataSource\":{\"dbPwd\":\"dsmp\",\"queryCondition\":\"1=1\",\"queryField\":\"servicename,comname,id,hostname,state,updatetime\",\"dbName\":\"dsmp\",\"dbUser\":\"dsmp\",\"id\":\"38\",\"dbUrl\":\"jdbc:mysql://192.168.11.189:3306/dsmp\",\"dbDriver\":\"com.mysql.jdbc.Driver\",\"tableName\":\"tb_service_status\"}}]";
//        new ETLManager().run(json);

//        String json ="{\"id\":1007,\"account\":1,\"taskName\":\"sits_men_mal\",\"taskType\":\"0\",\"sqlType\":\"sqoop\",\"targetDB\":\"bigdata_dw\",\"targetTable\":\"sits_men_mal\",\"fieldMapping\":\"MAL_OPSY mal_begd mal_comp MAL_USTP mal_ipad mal_pwtp mal_prac MAL_MST1 mal_endt mal_begt mal_srvr mal_stat mal_usrc mal_mstc mal_guid mal_muac mal_osun MAL_DEVT MAL_USAG mal_endd mal_wusr mal_dbid MAL_BROW mal_ucid\",\"createtime\":\"2017-12-21 02:44:45\",\"status\":\"0\",\"dataType\":\"MySQL\"}";
//        new ETLManager().taskRun(json);
//        HiveClient h = new HiveClient("hive");
//        System.out.println(h.getHiveTables("bigdata_dw"));

    }
}

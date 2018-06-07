package com.hand.bdss.dsmp.service.metadata;

import com.hand.bdss.dsmp.component.atlas.SearchMetaHelper;
import com.hand.bdss.dsmp.component.hbase.HTableUtil;
import com.hand.bdss.dsmp.component.hive.HiveClient;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.exception.DataServiceException;
import com.hand.bdss.dsmp.model.HiveMetaData;
import com.hand.bdss.dsmp.model.HiveMetaDesc;
import com.hand.bdss.dsmp.model.HiveMetaTableField;
import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.dsmp.service.privilege.RangerPolicyManage;
import com.hand.bdss.dsmp.util.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 提供hive和hbase元数据的创建服务
 * Created by xc on 2017/4/22.
 */
public class AtlasMetaManage {

    private static final Logger logger = LoggerFactory.getLogger(AtlasMetaManage.class);
    private HiveClient hiveClient = new HiveClient(SystemConfig.userName);
    private HTableUtil htableUtil = new HTableUtil();


    public List<HiveMetaDesc> searchMeta(String hiveMetaType, String hdfsMetaType) {
        SearchMetaHelper ams = new SearchMetaHelper();
        List<HiveMetaDesc> hiveMetaDescs = null;
        List<HiveMetaDesc> hdfsMetaDescs = null;

        if (StringUtils.isNotBlank(hiveMetaType))
            hiveMetaDescs = ams.searchByDSL(hiveMetaType);
        if (StringUtils.isNotBlank(hdfsMetaType))
            hdfsMetaDescs = ams.searchByDSL(hdfsMetaType);

        if (hiveMetaDescs != null && hdfsMetaDescs != null)
            hiveMetaDescs.addAll(hdfsMetaDescs);
        return hiveMetaDescs;
    }

    public List<HiveMetaDesc> searchMeta(String metaType) {
        SearchMetaHelper ams = new SearchMetaHelper();
        if (StringUtils.isNotBlank(metaType))
            return ams.searchByDSL(metaType);
        return null;
    }


    /**
     * hive表的元数据信息
     * @return
     */
    public Boolean createHiveMeta(HiveMetaData hmd,String username) throws Exception {
        Boolean flag = false;
        Boolean result = false;
        List<HiveMetaTableField> metaTableFields = hmd.getMetaTableField();
        if (metaTableFields != null && metaTableFields.size() > 0) {
            for (HiveMetaTableField hiveMetaField : metaTableFields) {
                if (hiveMetaField.getFlag() == 0) {
                    hmd.setBooUdp(1);
                    break;
                }
            }
        }
        if (hmd.getBooUdp() == 1) {
            flag = hiveClient.createHivePartitionTable(hmd);
        } else {
            flag = hiveClient.createHiveTable(hmd);
        }

        //新增成功后添加用户对表的权限管理
        if(flag){
            List<String> userList = new ArrayList<String>();
            List<String> dbList = new ArrayList<String>();
            List<String> tableList = new ArrayList<String>();
            List<String> typeList = new ArrayList<String>();
            HivePolicy policy = new HivePolicy();
            userList.add(username);
            policy.setUser(userList);
            dbList.add(hmd.getDbName());
            policy.setDatabases(dbList);
            tableList.add(hmd.getTabelName());
            policy.setTables(tableList);
            typeList.add("all");
            typeList.add("select");
            typeList.add("update");
            typeList.add("drop");
            typeList.add("alter");
            typeList.add("index");
            policy.setType(typeList);
            policy.setName(UUID.randomUUID().toString());
            policy.setServiceName(SystemConfig.HIVE_SERVER);
            result = new RangerPolicyManage().createPolicy(policy);
        }
        return result;
    }

    /**
     * 修改hive表
     *
     * @param hiveMetaData hive的元数据信息
     * @param oldTableName 要修改的hive的表名
     * @return
     * @throws DataServiceException
     */
    public Boolean updateHiveTable(HiveMetaData hiveMetaData, String oldTableName) throws DataServiceException {
        return hiveClient.updateHiveTable(hiveMetaData);
    }

    /**
     * 删除hive表
     *
     * @param lists 要删除的hive表的表名
     * @return
     * @throws DataServiceException
     */
    public Boolean dropHiveTable(List<HiveMetaData> lists) throws DataServiceException {
        return hiveClient.dropHiveTable(lists);
    }

    /**
     * hbase表名
     *
     * @param tableName
     * @throws Exception
     */
    public Boolean createHBaseMeta(String tableName) throws Exception {

        Connection con = ConnectionFactory.createConnection();
        Admin admin = con.getAdmin();
        ClusterStatus clusterStatus = admin.getClusterStatus();
        int partitionLimit = 0;
        int nodeNum = clusterStatus.getServersSize();
        if (nodeNum > 0 && nodeNum < 10)
            partitionLimit = 10;
        if (nodeNum > 10 && nodeNum < 50)
            partitionLimit = 50;
        return htableUtil.createHBaseTable(tableName, partitionLimit, false);
    }

    /**
     * 删除hbase表
     *
     * @param oldTableName 要删除的表名
     * @return
     * @throws Exception
     */
    public Boolean dropHBaseTable(String oldTableName) throws Exception {
        return htableUtil.renameHBaseTable(oldTableName);
    }

}

package com.hand.bdss.dsmp.component.hbase;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class HTableUtil {

    private static final Logger logger = LoggerFactory.getLogger(HTableUtil.class);

    private static final int MAX_FILE_SIZE = 1024 * 1024 * 256;

    public static void main(String[] args) throws Exception {
        if (args.length == 2 && StringUtils.isNotBlank(args[0]) && StringUtils.isNotBlank(args[1])) {
            String tableName = args[0];
            int regionNum = Integer.parseInt(args[1]);

            createHBaseTable(tableName, regionNum, false);
        }
    }

    public static HTableDescriptor getHTableDescriptor(String tableName) {
        HColumnDescriptor columnDescriptor = new HColumnDescriptor(HTableManager.DEFAULT_FAMILY_NAME);
        columnDescriptor.setCompressionType(Compression.Algorithm.SNAPPY);
        columnDescriptor.setCompactionCompressionType(Compression.Algorithm.SNAPPY);
        // columnDescriptor.setTimeToLive(60 * 60 * 24 * 365 * 1);
        columnDescriptor.setBlockCacheEnabled(true);
        columnDescriptor.setDataBlockEncoding(DataBlockEncoding.NONE);

        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
        desc.setCompactionEnabled(true);
        desc.setMaxFileSize(MAX_FILE_SIZE);
        desc.addFamily(columnDescriptor);
        return desc;
    }

    /**
     * 创建表
     *
     * @param tableName          表名
     * @param partitionSeedLimit 预分区数量，最大数量3844
     * @param dropIfExists
     * @throws Exception
     */
    public static boolean createHBaseTable(String tableName, int partitionSeedLimit, boolean dropIfExists)
            throws Exception {
        if (partitionSeedLimit > 3844 || partitionSeedLimit < 1) {
            throw new IllegalArgumentException("PartitionSeedLimit must be > 0 and < 3844.");
        }
        logger.info("init HBase admin...");
        Configuration conf = HBaseConfiguration.create();
        HBaseAdmin admin = new HBaseAdmin(conf);
        if (admin.tableExists(tableName) && dropIfExists) {
            if (!admin.isTableDisabled(tableName)) {
                admin.disableTable(tableName);
            }
            admin.deleteTable(tableName);
            logger.info(String.format("Table is exist, drop table %s successed.", tableName));
        }
        if (!admin.tableExists(tableName)) {
            logger.info(
                    String.format("Creating HBase table %s, partition seed limit %d.", tableName, partitionSeedLimit));
            admin.createTable(getHTableDescriptor(tableName),
                    Bytes.toByteArrays(HTableManager.generatPartitionSeed(partitionSeedLimit)));
            logger.info(String.format("HBase table %s is created.", tableName));
        } else {
            logger.info(String.format("HBase table %s is exist, create will be cancel.", tableName));
        }
        admin.close();
        logger.info("==============================================");
        return true;
    }

    public  Boolean renameHBaseTable(String oldTableName) throws Exception {
        Connection con = ConnectionFactory.createConnection();
        Admin admin = con.getAdmin();
        long currentTimeMillis = System.currentTimeMillis();
        String snapshotName = oldTableName + "_snap";
        String newTableName = oldTableName + "_ueser_delete"+currentTimeMillis;

        admin.disableTable(TableName.valueOf(oldTableName));
        admin.snapshot(snapshotName, TableName.valueOf(oldTableName));
        admin.cloneSnapshot(snapshotName, TableName.valueOf(newTableName));

        admin.deleteSnapshot(snapshotName);
        admin.deleteTable(TableName.valueOf(oldTableName));

        return true;
    }

}

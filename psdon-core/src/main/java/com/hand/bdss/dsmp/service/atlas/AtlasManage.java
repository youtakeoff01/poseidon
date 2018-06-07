package com.hand.bdss.dsmp.service.atlas;

import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.util.HttpUtils;

/**
 * Created by hand on 2018/1/8.
 */
public class AtlasManage {

    private HttpUtils httpClient = new HttpUtils();

    /**
     * @param name 查询名称
     * @return
     */
    public String getAtlasGuid(String name) throws Exception {
        String uri = SystemConfig.ATLAS_GUID_URL.replace("{name}", name);
        return httpClient.doGet(uri);
    }

    /**
     * @param guid 唯一标识
     * @return
     */
    public String getAtlasEntity(String guid) throws Exception {
        String uri = SystemConfig.ATLAS_ENTITY_URL.replace("{guid}", guid);
        return httpClient.doGet(uri);
    }

    /**
     * 获取血缘关系
     *
     * @param guid 唯一标识
     * @return
     */
    public String getLineage(String guid) throws Exception {
        String uri = SystemConfig.ATLAS_LINEAGE_URL.replace("{guid}", guid);
        return httpClient.doGet(uri);
    }
//
//    /**
//     * 获取table字段信息
//     *
//     * @param guid 唯一标识
//     * @return
//     */
//    public String getLineageSchema(String guid) throws Exception {
//        String uri = SystemConfig.ATLAS_ENTITY_URL.replace("{guid}", guid);
//        return httpClient.doGet(uri);
//    }
}

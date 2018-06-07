package com.hand.bdss.web.datamanage.datalineage.service;


import com.hand.bdss.web.datamanage.datalineage.vo.Column;

import java.util.List;
import java.util.Map;

public interface DataLineageService {

    /**
     * 查询Atlas关系族谱 整体
     *
     * @return
     * @throws Exception
     */
    Map<String, Object> getAtlasLineageIn(String guid) throws Exception;
    /**
     * 查询Atlas关系族谱 上游
     *
     * @return
     * @throws Exception
     */
    Map<String, Object> getAtlasLineageOut(String guid) throws Exception;
    /**
     * 查询Atlas关系族谱 下游
     *
     * @return
     * @throws Exception
     */
    Map<String, Object> getAtlasLineage(String guid) throws Exception;

    /**
     * 查询Atlas tabe Details
     *
     * @return
     * @throws Exception
     */
    Map<String, String> getTabDetails(String guid) throws Exception;

    /**
     * 查询Atlas guid
     *
     * @param name
     * @return
     * @throws Exception
     */
    String getAtlasGuid(String dbName, String name) throws Exception;

//    /**
//     * 查询Atlas元信息
//     *
//     * @param guid
//     * @return
//     * @throws Exception
//     */
//    String getAtlasEntity(String guid) throws Exception;

    /**
     * 查询Atlas schema
     *
     * @param guid
     * @return
     * @throws Exception
     */
    List<Column> getEntitySchema(String guid) throws Exception;

}

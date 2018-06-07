package com.hand.bdss.web.datamanage.metadata.service.impl;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.csvreader.CsvReader;
import com.hand.bdss.dsmp.component.hdfs.HDFSClient;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.DocumentModel;
import com.hand.bdss.dsmp.model.ReturnCode;
import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.SqlServerCRUDUtils;
import com.hand.bdss.web.datamanage.metadata.dao.MetaDataDao;
import com.hand.bdss.web.datamanage.metadata.service.InfoDocumentService;
import com.hand.bdss.web.entity.MetaDataEntity;
import com.hand.bdss.web.entity.UserEntity;

import jersey.repackaged.com.google.common.collect.Maps;

@Service
public class InfoDocumentServiceImpl implements InfoDocumentService {

	@Resource
	private HDFSClient hDFSClient;

	@Resource
	MetaDataDao metaDataDaoImpl;
	
	@Resource(name = "sqlServerCRUDUtils")
	SqlServerCRUDUtils sqlServerCRUDUtils;
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public Map<String, Object> uploadFile(InputStream in, String dstPath,HttpServletRequest request) throws Exception {
		//将上传到hdfs中的文件添加到元数据生命周期管理当中，便于后续使用
		MetaDataEntity metaDataEntity = new MetaDataEntity();
		metaDataEntity.setMetaName(dstPath);
		metaDataEntity.setMetaType("hdfs");
		metaDataEntity.setMetaLive(-1);
		metaDataEntity.setMetaLocation(HDFSClient.PATH_ROOT+dstPath);
		metaDataEntity.setMetaOwner(GetUserUtils.getUser(request).getId());
		metaDataDaoImpl.insertMetaData(metaDataEntity);
		return hDFSClient.uploadFile(in, dstPath);
	}

	@Override
	public Map<String, Object> downloadFile(String srcPath) throws Exception {
		return hDFSClient.downloadFile(srcPath);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public Map<String, String> delete(String srcPath) throws Exception {
		//删除文件的同时要删除这个文件的生命周期
		String[] strs = srcPath.split("//");
		String str = "";
		for (int i = 0; i < strs.length; i++) {
			if(i == strs.length-1){
				str = str + strs[i];
			}else{
				str = str + strs[i] +"/";
			}
		}
		MetaDataEntity metaDataEntity = new MetaDataEntity();
		metaDataEntity.setMetaLocation(HDFSClient.PATH_ROOT+str);
		List<MetaDataEntity> lists = new ArrayList<>();
		lists.add(metaDataEntity);
		metaDataDaoImpl.deleteMetaDataByLocation(lists);
		return hDFSClient.delete(srcPath);
	}
	
	@Override
	public boolean isDirectory(String srcPath) throws IOException {
		return hDFSClient.isDirectory(srcPath);
	}
	
	@Override
	public Map<String, String> createDirectoy(String srcPath) throws Exception {
		return hDFSClient.createDirectoy(srcPath);
	}

	@Override
	public List<DocumentModel> getDirectoyAndFileTree(String srcPath, String username) throws Exception {
		return hDFSClient.getDirectoyAndFileTree(srcPath, username);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public Map<String, String> copyFile(String srcPath, String dstPath, String fileName,HttpServletRequest request) throws Exception {
		MetaDataEntity metaDataEntity = new MetaDataEntity();
		metaDataEntity.setMetaName(dstPath + fileName);
		metaDataEntity.setMetaType("hdfs");
		metaDataEntity.setMetaLive(-1);
		metaDataEntity.setMetaLocation(HDFSClient.PATH_ROOT+dstPath + fileName);
		metaDataEntity.setMetaOwner(GetUserUtils.getUser(request).getId());
		metaDataDaoImpl.insertMetaData(metaDataEntity);
		return hDFSClient.copyFile(srcPath, dstPath, fileName);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public Map<String, String> moveFile(String srcPath, String dstPath, String fileName) throws Exception {
		Map<String, String> maps = new HashMap<>();
		maps.put("oldMetaName", srcPath);
		maps.put("newMetaName", dstPath + fileName);
		String newLocation = SystemConfig.HDFS_ROOT_PATH + dstPath + fileName;
		maps.put("newLoaction", newLocation);
		metaDataDaoImpl.updateRename(maps);
		return hDFSClient.renameFile(srcPath, dstPath, fileName);
	}

	@Override
	public DocumentModel getDocumentTree(String srcPath, String username) throws Exception {
		return hDFSClient.getAllDictoryTree(srcPath, username);
	}

	@Override
	public Map<String, Object> csvContentCheck(InputStream in, HttpServletRequest request) throws Exception{
		Map<String,Object> map = null;
		StringBuffer str = new StringBuffer();
		// 2.根据上传用户名称来判断文件类别（心理咨询的，还是宿舍的）。
		UserEntity user = GetUserUtils.getUser(request);
		String userName = user.getUserName();
		CsvReader r = null;
		try {
			r = new CsvReader(in,Charset.forName("GBK"));
			//心理咨询老师  or 学生事务办公室老师
			if(Global.MENTALHEALTH.equals(userName) || Global.SDAADMIN.equals(userName)) {
				int row = 0;
				while(r.readRecord()) {
					row++;
					if(row>1) {
						String stuID = r.get(0);
						String stuAD = r.get(1);
						boolean boo = this.checkStu(stuID,stuAD,null);
						if(!boo) {
							str.append(row).append(",");
						}
					}
				}
				if(str.toString().length()>0) {
					str.insert(0, "行号为：");
					str.append("的学生学号和学生的AD账号不匹配，请确认无误之后再上传。");
				}
			}
			//宿舍老师
			if(Global.ACCOMMODATION.equals(userName)) {
				int row = 0;
				while(r.readRecord()) {
					row++;
					if(row>1) {
						boolean boo_1 = true;
						boolean boo_2 = true;
						boolean boo_3 = true;
						boolean boo_4 = true;
						String stuID_1 = r.get(0);
						String stuName_1 = r.get(1);
						String stuID_2 = r.get(2);
						String stuName_2 = r.get(3);
						String stuID_3 = r.get(4);
						String stuName_3 = r.get(5);
						String stuID_4 = r.get(6);
						String stuName_4 = r.get(7);
						if(StringUtils.isNotEmpty(stuID_1)&&StringUtils.isNotEmpty(stuName_1)) {
							boo_1 = this.checkStu(stuID_1,null,stuName_1);
						}
						if(StringUtils.isNotEmpty(stuID_2)&&StringUtils.isNotEmpty(stuName_2)) {
							boo_2 = this.checkStu(stuID_2,null,stuName_2);
						}
						if(StringUtils.isNotEmpty(stuID_3)&&StringUtils.isNotEmpty(stuName_3)) {
							boo_3 = this.checkStu(stuID_3,null,stuName_3);
						}
						if(StringUtils.isNotEmpty(stuID_4)&&StringUtils.isNotEmpty(stuName_4)) {
							boo_4 = this.checkStu(stuID_4,null,stuName_4);
						}
						if(!boo_1 || !boo_2 || !boo_3 || !boo_4) {
							str.append(row).append(",");
						}
					}
				}
				if(str.toString().length()>0) {
					str.insert(0, "行号为：");
					str.append("的学生学号和学生的姓名不匹配，请确认无误之后再上传。");
				}
			}
			//一站式老师 或者其他上传
			if(Global.ONESTOP.equals(userName) || Global.OTHERUPLOAD.equals(userName)) {
				int row = 0;
				while(r.readRecord()) {
					row++;
					if(row>1) {
						boolean boo_1 = true;
						String stuID_1 = r.get(0);
						String stuName_1 = r.get(1);
						if(StringUtils.isNotEmpty(stuID_1)&&StringUtils.isNotEmpty(stuName_1)) {
							boo_1 = this.checkStu(stuID_1,null,stuName_1);
						}
						if(!boo_1) {
							str.append(row).append(",");
						}
					}
				}
				if(str.toString().length()>0) {
					str.insert(0, "行号为：");
					str.append("的学生学号和学生的AD账号不匹配，请确认无误之后再上传。");
				}
			}
			if(str.toString().length()>0) {
				map = Maps.newHashMap();
				map.put("returnMessage", str.toString());
				map.put("returnCode", ReturnCode.CSV_CONTENT_ERROR);
			}
		} finally {
			if(r!=null) {
				r.close();
			}
		}
		return map;
	}
	private boolean checkStu(String stuID, String stuAD, String stuName) {
		return sqlServerCRUDUtils.getStuMsg(stuID, stuAD, stuName);
	}
}

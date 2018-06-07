package com.hand.bdss.test;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hand.bdss.web.entity.LogEntity;
import com.hand.bdss.web.platform.management.service.LogService;
import com.hand.bdss.web.common.util.DataConnectionUtils;
import com.hand.bdss.web.common.vo.DBSrcVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "servlet-context.xml")
public class DataConnectionUtilsTest {
	
	@Resource
	private LogService logService;
	@Test
	public void test01() throws SQLException{
		
		
		DBSrcVO dbSrcVo = new DBSrcVO();
		dbSrcVo.setDbDriver("com.mysql.jdbc.Driver");
		dbSrcVo.setDbUrl("jdbc:mysql://localhost:3306/test");
		dbSrcVo.setDbUser("root");
		dbSrcVo.setDbPwd("qweasd");
		//System.out.println(DataConnectionUtils.getConnectionStatus(dbSrcVo));
		List<String> lists = null;
//		lists = DataConnectionUtils.getTablesName(dbSrcVo);
		
//		lists = DataConnectionUtils.getTablesDescript(dbSrcVo, "test_del");
		for(int i = 0 ;i<lists.size();i++){
			System.out.println(lists.get(i));
		}
	}
	@Test
	public void test02() {
		LogEntity log = new LogEntity();
		log.setId(4);
		log.setLogContent("输入惨呼");
		log.setLogType("这是日志");
//		log.setOperationId(424);
//		logService.insertLog(log);
	}
	@Test
	public void test03() {
		LogEntity log = new LogEntity();
		log.setLogContent("输入惨呼");
		log.setLogType("这是日志");
//		log.setOperationId(424);
//		List<LogEntity> lists = logService.selectLog(log,1,10);
//		for(int i = 0 ;i <lists.size();i++){
//			System.out.println(lists.get(i));
//		}
	}
}
 
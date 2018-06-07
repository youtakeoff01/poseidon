package com.hand.bdss.dsmp.component.ranger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hand.bdss.dsmp.model.RangerUser;

public class TestUserAPIs {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		//String user = new UserAPIs().searchUsers();
	    new UserAPIs().deleteUser(createVXUser());
		//System.out.println("---"+user.toString());
	}
	
	private static RangerUser createVXUser() {
		RangerUser testVXUser= new RangerUser();
		Collection<String>c = new ArrayList<String>();
//		testVXUser.setId(49L);
//		testVXUser.setCreateDate(new Date());
//		testVXUser.setUpdateDate(new Date());
////		testVXUser.setOwner("Admin");
//		testVXUser.setUpdatedBy("Admin");
		testVXUser.setName("lisi07");
		testVXUser.setFirstName("lisi06");
		testVXUser.setLastName("lisi06");
		testVXUser.setPassword("lisi06");
		testVXUser.setGroupIdList(null);
		testVXUser.setGroupNameList(null);
		testVXUser.setStatus(1);
		testVXUser.setIsVisible(1);
		testVXUser.setUserSource(0);
		c.add("role_user");  //"role_user,role_admin"
		testVXUser.setUserRoleList(c);
		
		return testVXUser;
		
	}
	
}

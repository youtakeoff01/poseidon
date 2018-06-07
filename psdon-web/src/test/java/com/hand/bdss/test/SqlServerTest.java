package com.hand.bdss.test;

import java.util.List;

import com.hand.bdss.web.entity.ApprovalEntity;

import jersey.repackaged.com.google.common.collect.Lists;

public class SqlServerTest {
	public static void main(String[] args) {
		List<ApprovalEntity> approvals = Lists.newArrayList();
		System.out.println(approvals.size());
	}

}

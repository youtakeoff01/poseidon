package com.hand.bdss.web.operationcenter.task.vo;

import com.hand.bdss.dev.vo.Task;

/**
 * Created by hand on 2017/8/14.
 */
public class AITaskInfo extends Task {

    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String createAccount;//创建人

    public String getCreateAccount() {
        return createAccount;
    }

    public void setCreateAccount(String createAccount) {
        this.createAccount = createAccount;
    }
}

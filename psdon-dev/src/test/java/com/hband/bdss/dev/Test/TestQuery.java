package com.hband.bdss.dev.Test;

import com.hand.bdss.dev.model.ScheduleType;
import com.hand.bdss.dev.service.IDevelopManager;
import com.hand.bdss.dev.service.impl.DevelopManager;
import com.hand.bdss.dev.util.ScriptUtil;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.task.service.impl.AzkabanManger;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestQuery {
    public static void main(String[] args) throws Exception {
        Task task = new Task();
        Long id = 16L;
        task.setId(id);
        Long account = 1001L;
        task.setAccount(account);
        task.setTaskName("job008");
        task.setTaskType("1");
        task.setSqlStc(" SELECT servicename,comname,updatetime,year,month,day FROM tb_service_status");
//        task.setSqlType("hive");
        task.setSqlType("spark");
        task.setTargetDB("default");
        task.setTargetTable("tb_service_status_job");
        task.setPartition("updatetime");
//        task.setStatus("1");
        task.setTimerAttribute("");
//
//        task.setStarttime(new Date(new Date().getTime() - 24 * 60 * 60 * 1000 * 8));
//        task.setEndtime(new Date(new Date().getTime() - 24 * 60 * 60 * 1000 * 2));
//        IDevelopManager developManager = new DevelopManager();
//        List<Map<String, String>> query = developManager.getQuery(task);
//        System.out.println(query);
//        System.out.println(developManager.createScript(task));
//        System.out.println(developManager.executeScript(task));
//        System.out.println(developManager.executeScheduleScript(task));


//        boolean b = developManager.deleteScript(task);
//        System.out.println(b);
//        boolean b = developManager.stopProject(task);
//        System.out.println(b);

    }


}

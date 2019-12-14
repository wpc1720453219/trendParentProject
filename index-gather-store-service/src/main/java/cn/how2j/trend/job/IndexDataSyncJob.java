package cn.how2j.trend.job;

import cn.how2j.trend.pojo.Index;
import cn.how2j.trend.service.IndexDataService;
import cn.how2j.trend.service.IndexService;
import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

/**
 * 任务类，同时刷新指数代码和指数数据
 * Author: xiekaikai
 * Date: 2019-09-16 22:01
 */
public class IndexDataSyncJob extends QuartzJobBean {

    @Autowired
    private IndexService indexService;
    @Autowired
    private IndexDataService indexDataService;

    /**
     *
     * @author: xiekaikai
     * @date: 2019-09-16 22:20
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("定时启动:" + DateUtil.now());
        List<Index> indexes = indexService.fresh();
        for (Index index: indexes) {
            indexDataService.fresh(index.getCode());
        }
        System.out.println("定时结束：" + DateUtil.now());
    }
}

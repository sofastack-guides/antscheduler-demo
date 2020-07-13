/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.alipay.sofa.endpoint.task;

import com.alipay.antschedulerclient.common.ClientCommonResult;
import com.alipay.antschedulerclient.handler.ISimpleJobHandler;
import com.alipay.antschedulerclient.model.JobExecuteContext;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 简单任务Demo
 * 简单RPC 任务，服务端通过RPC方式将 任务消息下发到客户端。 业务只要在 handle 里实现自己的业务逻辑即可。
 *
 * @author changwei.zjl
 * @version $Id: SimpleTask.java, v 0.1 2020-07-12 20:10 changwei.zjl Exp $$
 */
@Component
public class SimpleJobDemo implements ISimpleJobHandler {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(20,
            300, 1, TimeUnit.HOURS, new ArrayBlockingQueue<Runnable>(100) {
    });

    @Override
    public ClientCommonResult handle(JobExecuteContext context) throws Exception {
        // todo 处理业务逻辑
        // 获取自定义参数
        Integer num = (Integer) context.getCustomParam("num");

        List<String> list = Lists.newArrayList();
        for (int shard = 1; shard <= 5; shard++) {
            list.add(shard + "");
        }
        // 设置自定义参数
        context.putCustomParams("sharding", list);

        return ClientCommonResult.buildSuccessResult();
    }

    @Override
    public String getName() {
        // 任务处理器名字，和spring bean 名字不同
        return "SIMPLE_HANDLER";
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        // 使用自定义线程池，如果返回null 则使用默认线程池。
        // 推进使用自定义线程池
        return executor;
    }
}
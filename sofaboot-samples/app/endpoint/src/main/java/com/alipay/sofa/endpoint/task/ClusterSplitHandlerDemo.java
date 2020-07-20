/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.alipay.sofa.endpoint.task;

import com.alipay.antschedulerclient.common.SplitChunkDataResult;
import com.alipay.antschedulerclient.handler.IClusterJobSplitHandler;
import com.alipay.antschedulerclient.model.ClusterJobSplitContext;
import com.alipay.antschedulerclient.model.chunk.IChunkData;
import com.alipay.antschedulerclient.model.chunk.ShardingChunkData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 集群任务拆分层处理器 demo
 * 集群任务将一个任务分为多个任务，分发到业务集群中处理，提高任务处理效率。集群任务分拆分阶段和执行阶段，允许有多个拆分阶段，只有一个执行阶段。
 * 这个例子是展示集群拆分阶段的使用
 *
 * @author changwei.zjl
 * @version $Id: ClusterSplitHandlerDemo.java, v 0.1 2020-07-12 20:33 changwei.zjl Exp $$
 */
@Component
public class ClusterSplitHandlerDemo implements IClusterJobSplitHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterSplitHandlerDemo.class);

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(20,
            300, 1, TimeUnit.HOURS, new ArrayBlockingQueue<Runnable>(100) {
    });


    @Override
    public SplitChunkDataResult handle(ClusterJobSplitContext clusterJobSplitContext) {
        SplitChunkDataResult result = new SplitChunkDataResult();
        try {
            // 处理业务逻辑
            // 拆分多个分片返回
            List<IChunkData> chunkList = new ArrayList();
            for (int chunk = 0; chunk < 10; chunk++) {
                ShardingChunkData chunkData = new ShardingChunkData();
                chunkData.setShardingRule(String.valueOf(chunk));
                chunkList.add(chunkData);
            }
            result.setChunkDatum(chunkList);
            result.setSuccess(true);
            // 成功
            return result;
        } catch (Exception e) {
            // 处理失败
            LOGGER.error("split exception", e);
            result.setMsg(e.getMessage());
            result.setSuccess(false);
            return result;
        }
    }

    @Override
    public String getName() {
        return "CLUSTER_SPLIT_HANDLER_DEMO";
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return executor;
    }
}
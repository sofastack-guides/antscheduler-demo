/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.alipay.sofa.endpoint.task;

import com.alipay.antschedulerclient.common.ClientCommonResult;
import com.alipay.antschedulerclient.common.DataProcessResult;
import com.alipay.antschedulerclient.executor.data.item.IDataItem;
import com.alipay.antschedulerclient.executor.data.item.LoadData;
import com.alipay.antschedulerclient.executor.data.item.MultiDataItem;
import com.alipay.antschedulerclient.executor.data.item.SingleDataItem;
import com.alipay.antschedulerclient.executor.data.processor.IProcessor;
import com.alipay.antschedulerclient.executor.data.reader.IReader;
import com.alipay.antschedulerclient.executor.data.writer.IWriter;
import com.alipay.antschedulerclient.executor.limiter.DefaultLimiter;
import com.alipay.antschedulerclient.executor.limiter.ILimiter;
import com.alipay.antschedulerclient.handler.IClusterJobExecuteHandler;
import com.alipay.antschedulerclient.model.ClusterJobExecuteContext;
import com.alipay.antschedulerclient.model.Progress;
import com.alipay.antschedulerclient.model.chunk.IChunkData;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 集群任务执行层处理器 demo
 * 集群任务执行阶段分为三步 read / process / write
 *
 * @author changwei.zjl
 * @version $Id: ClusterExecuteHandlerDemo.java, v 0.1 2020-07-12 20:33 changwei.zjl Exp $$
 */
@Component
public class ClusterExecuteHandlerDemo implements IClusterJobExecuteHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterExecuteHandlerDemo.class);

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(20,
            300, 1, TimeUnit.HOURS, new ArrayBlockingQueue<Runnable>(100) {
    });


    @Override
    public void preExecute(ClusterJobExecuteContext clusterJobExecuteContext) {
        // 前置处理
    }

    /**
     * 读数据阶段
     *
     * @return
     */
    @Override
    public IReader getReader() {
        return new IReader() {
            @Override
            public Integer waitIntervalWhenNoData() {
                return null;
            }

            @Override
            public LoadData<String> read(ClusterJobExecuteContext context) throws Exception {
                List<String> stringList = Lists.newArrayList();
                IChunkData chunkData = context.getChunk();
                LOGGER.info(String.format(
                        "JobExecuteHandler getReader executeId:%s, param:%s, chunk:%s",
                        context.getExecuteId(), context.getCustomParams(), chunkData.parseToString()));

                // 读取数据
                int st = 0;
                int end = 10;
                for (int i = st; i < end; i++) {
                    stringList.add(String.valueOf(i));
                }

                LOGGER.info(
                        String.format("JobExecuteHandler loadData false executeId:%s, chunk:%s",
                                context.getExecuteId(), chunkData.parseToString()));

                // 返回数据。 如果没有数据 则 hasMore 设置为false；如果还有数据则 返回 true，后面还会继续调度 read 方法读取数据。
                return new LoadData<String>(stringList, false);
            }
        };
    }

    /**
     * 处理阶段
     *
     * @return
     */
    @Override
    public IProcessor getProcessor() {
        return new IProcessor() {
            @Override
            public DataProcessResult process(ClusterJobExecuteContext context,
                                             Object data) throws Exception {

                // todo 处理业务逻辑

                return new DataProcessResult(true, "", data);
            }

            @Override
            public ThreadPoolExecutor getProcessThreadPool() {
                return null;
            }
        };
    }

    /**
     * 写阶段
     *
     * @return
     */
    @Override
    public IWriter getWriter() {

        return new IWriter<String>() {
            @Override
            public int getCountPerWrite() {
                return 1;
            }

            @Override
            public ClientCommonResult write(ClusterJobExecuteContext context,
                                            IDataItem<String> dataItem) throws Exception {
                switch (dataItem.getType()) {
                    case SINGLE:
                        LOGGER.info(String.format("getWriter write single data:%s",
                                ((SingleDataItem<String>) dataItem).getItem()));
                        // 保存单条数据
                        break;
                    case MULTIPLE:
                        LOGGER.info(String.format("getWriter write multi data:%s",
                                ((MultiDataItem<String>) dataItem).getItemList()));
                        // 保存多条数据
                        break;
                    default:
                        break;
                }

                LOGGER.info(String.format(
                        "JobExecuteHandler write success executeId:%s, params:%s, chunk:%s",
                        context.getExecuteId(), context.getCustomParams(),
                        context.getChunk().parseToString()));
                return ClientCommonResult.buildSuccessResult();
            }

            @Override
            public ThreadPoolExecutor getWriteThreadPool() {
                return executor;
            }
        };
    }

    @Override
    public ILimiter getLimiter() {
        // 限流器，可以自定义，也可以使用框架自带的限流器
        return new DefaultLimiter(10);
    }

    @Override
    public void postExecute(ClusterJobExecuteContext clusterJobExecuteContext) {
        // 后置处理
    }

    /**
     * 集群分片在执行过程中，会每隔几秒调用一次。用来查询处理进度
     *
     * @param clusterJobExecuteContext
     * @return
     */
    @Override
    public Progress calProgress(ClusterJobExecuteContext clusterJobExecuteContext) {
        // 展示进度
        // 一般会查询当前分片的数据处理了多少
        Progress progress = new Progress();
        // 设置改分片下的数据总量
        int totalCount = 10;
        progress.setTotal(totalCount);
        // 设置已经处理成功的数量
        int successCount = 10;
        progress.setSucceed(successCount);
        return progress;
    }

    @Override
    public boolean isProcessAsync() {
        return false;
    }

    /**
     * 处理器名字
     *
     * @return
     */
    @Override
    public String getName() {
        return "CLUSTER_EXECUTOR_HANDLER_DEMO";
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return executor;
    }
}
/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.alipay.sofa.endpoint.task;

import com.alipay.common.event.UniformEvent;
import com.alipay.common.event.UniformEventContext;
import com.alipay.common.event.UniformEventMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 消息任务
 * 消息任务主要是以消息队列为通道下发调度事件，服务端会根据配置的cron表达式准点触发，并将事件放入队里里业务方去订阅。
 * 本例主要展示业务客户端如何从队列中获取到调度事件，并处理业务相关逻辑
 * <p>
 * 消息任务依赖 mq-enterprise-sofa-boot-starter
 *
 * @author changwei.zjl
 * @version $Id: MsgJobDemo.java, v 0.1 2020-07-12 19:38 changwei.zjl Exp $$
 */
@Component
public class MsgJobDemo implements UniformEventMessageListener {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgJobDemo.class);

    @Override
    public void onUniformEvent(UniformEvent uniformEvent, UniformEventContext uniformEventContext) throws Exception {
        // 所有消息定时任务的Topic 统一为  TP_F_SC
        final String topic = uniformEvent.getTopic();

        // 消息事件码 即是页面配置的 消息任务事件码
        final String eventCode = uniformEvent.getEventCode();

        // 接收触发后的定时业务处理
        LOGGER.info("[Receive an uniformEvent] topic {} eventcode {} eventId {} payload {}",
                new Object[]{topic, eventCode, uniformEvent.getId()});

        // todo  处理业务逻辑

    }
}
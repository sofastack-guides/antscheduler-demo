# 一. AntScheduler Demo 介绍
分布式定时任务调度 AntScheduler 提供了定时调度能力，根据业务配置的cron表达式定时调度。目前提供了多种类型的任务，包括：消息任务、简单任务、集群任务和拓扑任务。

- 消息任务。 主要是解决异构、异步处理问题。调度系统将每次调度事件发送到消息队列，业务方从队列中订阅处理。
- 简单任务。 是基于 RPC 触发机制的任务，能够感知任务执行结果。
- 集群任务。 主要用于跑批，充分业务集群，提高任务处理效率。
- 拓扑任务。 负责做任务编排，客户端demo 不涉及。

# 二. 使用说明
## 2.1 前提
## 1）创建sofaboot 工程
```bash
mvn archetype:generate -DarchetypeRepository=http://mvn.cloud.alipay.com/nexus/content/repositories/snapshots/ -DarchetypeGroupId=com.alipay.sofa -DarchetypeArtifactId=sofaboot-web-archetype -DarchetypeVersion=1.0-SNAPSHOT -DarchetypeCatalog=internal
```

### 2）引入 antscheduler starter 
```xml
<!-- RPC 任务必须 --> 
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>scheduler-enterprise-sofa-boot-starter</artifactId>
</dependency>
<!-- 消息任务必须 --> 
<dependency>
  <groupId>com.alipay.sofa</groupId>
  <artifactId>mq-enterprise-sofa-boot-starter</artifactId>
</dependency>
```
### 3) 引入中间件配置
```
com.alipay.env=shared
## 租户实例
com.alipay.instanceid=XXX
com.antcloud.antvip.endpoint=XXX
# 鉴权，从门户的安全中心获取
com.antcloud.mw.access=XXX
com.antcloud.mw.secret=XXX
```

## 2.2 消息任务创建
### 1) 代码编码
```java
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
```
### 2） 客户端订阅关系配置
在 SOFABoot 应用上下文文件中，配置 <sofa:consumer> 元素以定义消息订阅端。文件路径为：src/main/resources/META-INF/xxx/xxx-xxx.xml
消息订阅端配置示例如下：

```xml
<!-- consumer declaration, the id and group attribute are required and their value must be unique -->
<sofa:consumer id="uniformEventSubscriber" group="S_SCHEDULERTUTORIAL_MSG_DEMO">
    <sofa:listener ref="schedulerDemo"/>
    <sofa:channels>
        <!-- channel value is the involved topic -->
        <sofa:channel value="TP_F_SC">
            <!-- each event represents a subscription -->
            <sofa:event eventType="direct" eventCode="EC_TASK_SCHEDULERTUTORIAL_MSG_DEMO" persistence="false"/>
        </sofa:channel>
    </sofa:channels>
    <sofa:binding.msg_broker/>
</sofa:consumer>
<!-- messageListener listener bean declaration, implements com.alipay.common.event.UniformEventMessageListener -->
<bean id="msgJobDemo" class="com.alipay.sofa.endpoint.task.MsgJobDemo"/>
```

需要配置几个内容：
- 消息主题为 TP_F_SC。
- 消息事件码为 S_SCHEDULERTUTORIAL_MSG_DEMO。
- 消息订阅组为 S_SCHEDULERTUTORIAL_MSG_DEMO。
- <sofa:listener> 元素的 ref 属性值为实现消息监听器的类名。

### 
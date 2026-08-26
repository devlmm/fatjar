package com.workspace.fatjar.framework.mq.producer;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 生产者示例
 * <p>
 * 职责：演示如何通过 RocketMQTemplate 发送消息，业务方以此为模板扩展自有生产者。
 * <p>
 * 用法示例：
 *   demoMqProducer.send("order-topic", "订单已创建 orderId=123");
 * <p>
 * 装配说明：
 *   - @Component 由应用层组件扫描（base 包 com.workspace.fatjar）注册。
 *   - @ConditionalOnProperty 同时要求 rocketmq.name-server 与 rocketmq.producer.group，
 *     与 RocketMQAutoConfiguration.defaultMQProducer 的真实装配条件一致；
 *     仅当 starter 确实会创建 RocketMQTemplate（即已配置 producer.group）时才装配本生产者，
 *     避免只配 name-server 而未配 producer.group 时 RocketMQTemplate 缺失导致启动失败。
 *   - 注意：不要改用 @ConditionalOnBean(RocketMQTemplate.class)——组件扫描阶段早于
 *     自动装配 Bean 注册，@ConditionalOnBean 在 @Component 上不可靠（Spring Boot 官方不建议）。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "rocketmq", name = {"name-server", "producer.group"})
public class DemoMqProducer {

    /** RocketMQ 操作模板，由 rocketmq-spring-boot-starter 自动装配 */
    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 构造注入 RocketMQTemplate
     *
     * @param rocketMQTemplate RocketMQ 模板
     */
    public DemoMqProducer(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    /**
     * 同步发送字符串消息到指定 topic
     *
     * @param topic   目标主题
     * @param message 消息内容
     * @return RocketMQ 发送结果（含 msgId、sendStatus 等）
     */
    public SendResult send(String topic, String message) {
        return rocketMQTemplate.syncSend(topic, message);
    }
}

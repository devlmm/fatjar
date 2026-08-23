package com.workspace.fatjar.common.statemachine;

import com.workspace.fatjar.common.exception.BizException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 状态机测试
 *
 * @author fatjar
 * @since 1.0.0
 */
class StateMachineTest {

    /** 订单状态枚举（测试用） */
    enum OrderStatus { PENDING, PAID, SHIPPED, FINISHED, CANCELLED }

    /** 订单事件枚举（测试用） */
    enum OrderEvent { PAY, SHIP, CONFIRM, CANCEL }

    @Test
    void testTransition() {
        // 构建状态机：PENDING --PAY--> PAID --SHIP--> SHIPPED --CONFIRM--> FINISHED
        StateMachine<OrderStatus, OrderEvent> sm = new StateMachine<>(OrderStatus.class, OrderEvent.class);
        sm.addTransition(OrderStatus.PENDING, OrderEvent.PAY, OrderStatus.PAID)
          .addTransition(OrderStatus.PAID, OrderEvent.SHIP, OrderStatus.SHIPPED)
          .addTransition(OrderStatus.SHIPPED, OrderEvent.CONFIRM, OrderStatus.FINISHED)
          .addTransition(OrderStatus.PENDING, OrderEvent.CANCEL, OrderStatus.CANCELLED);

        // 验证正常流转
        assertEquals(OrderStatus.PAID, sm.fire(OrderStatus.PENDING, OrderEvent.PAY));
        assertEquals(OrderStatus.SHIPPED, sm.fire(OrderStatus.PAID, OrderEvent.SHIP));
        assertEquals(OrderStatus.FINISHED, sm.fire(OrderStatus.SHIPPED, OrderEvent.CONFIRM));
        assertEquals(OrderStatus.CANCELLED, sm.fire(OrderStatus.PENDING, OrderEvent.CANCEL));

        // 验证 canFire
        assertTrue(sm.canFire(OrderStatus.PENDING, OrderEvent.PAY));
    }

    @Test
    void testIllegalTransition() {
        StateMachine<OrderStatus, OrderEvent> sm = new StateMachine<>(OrderStatus.class, OrderEvent.class);
        sm.addTransition(OrderStatus.PENDING, OrderEvent.PAY, OrderStatus.PAID);

        // PAID 状态不允许 PAY 事件，应抛异常
        assertThrows(BizException.class, () -> sm.fire(OrderStatus.PAID, OrderEvent.PAY));
    }
}

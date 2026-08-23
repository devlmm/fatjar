package com.workspace.fatjar.common.statemachine;

import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;

import java.util.EnumMap;
import java.util.Map;

/**
 * 通用状态机（基于枚举的轻量级实现）
 * <p>
 * 设计说明：
 *   1. 以泛型 S（状态枚举）+ E（事件枚举）驱动状态流转
 *   2. 内部维护「状态 × 事件 -> 目标状态」转移表（EnumMap 高效）
 *   3. 转移前校验当前状态是否允许该事件，非法流转抛 BizException
 *   4. 无状态、线程安全（配置阶段非线程安全，使用前需配置完成）
 * <p>
 * 使用示例：
 *   StateMachine&lt;OrderStatus, OrderEvent&gt; sm = new StateMachine&lt;&gt;();
 *   sm.addTransition(OrderStatus.PENDING, OrderEvent.PAY, OrderStatus.PAID);
 *   OrderStatus next = sm.fire(current, OrderEvent.PAY);
 *
 * @param <S> 状态枚举类型
 * @param <E> 事件枚举类型
 * @author fatjar
 * @since 1.0.0
 */
public class StateMachine<S extends Enum<S>, E extends Enum<E>> {

    /** 状态枚举 Class（用于 EnumMap 实例化） */
    private final Class<S> stateType;
    /** 事件枚举 Class */
    private final Class<E> eventType;
    /** 转移表：外层 key=当前状态，内层 key=事件，value=目标状态 */
    private final Map<S, Map<E, S>> transitions;

    /**
     * 构造状态机
     *
     * @param stateType 状态枚举 Class
     * @param eventType 事件枚举 Class
     */
    public StateMachine(Class<S> stateType, Class<E> eventType) {
        this.stateType = stateType;
        this.eventType = eventType;
        this.transitions = new EnumMap<>(stateType);
    }

    /**
     * 注册一条状态转移
     *
     * @param from   当前状态
     * @param event  触发事件
     * @param to     目标状态
     * @return 当前状态机（链式调用）
     */
    public StateMachine<S, E> addTransition(S from, E event, S to) {
        transitions.computeIfAbsent(from, s -> new EnumMap<>(eventType)).put(event, to);
        return this;
    }

    /**
     * 触发事件，返回目标状态
     *
     * @param current 当前状态
     * @param event   触发事件
     * @return 目标状态（不修改原对象，调用方自行 set）
     * @throws BizException 当前状态不允许该事件时抛出
     */
    public S fire(S current, E event) {
        Map<E, S> eventMap = transitions.get(current);
        if (eventMap == null) {
            throw new BizException(ErrorCode.UNSUPPORTED_OPERATION,
                    "当前状态[" + current + "]不允许任何事件");
        }
        S next = eventMap.get(event);
        if (next == null) {
            throw new BizException(ErrorCode.UNSUPPORTED_OPERATION,
                    "当前状态[" + current + "]不允许事件[" + event + "]");
        }
        return next;
    }

    /**
     * 校验当前状态是否允许指定事件（不触发流转）
     *
     * @param current 当前状态
     * @param event   事件
     * @return true 表示允许
     */
    public boolean canFire(S current, E event) {
        Map<E, S> eventMap = transitions.get(current);
        return eventMap != null && eventMap.containsKey(event);
    }
}

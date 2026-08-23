package com.workspace.fatjar.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.entity.BaseEntity;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户实体（对应 crm.customer 表）
 * <p>
 * 字段说明：
 *   - customerName：客户名称，企业对外名称
 *   - contact：联系人姓名
 *   - phone：联系电话
 *   - email：联系邮箱
 *   - level：客户等级（0=普通，1=VIP，2=战略），用于差异化服务
 *   - status：客户状态（0=潜在，1=正式，2=流失），客户生命周期管理
 * <p>
 * 公共字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 BaseEntity，
 * 故本类不重复声明这些字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm.customer")
public class CrmCustomer extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 客户名称 */
    private String customerName;

    /** 联系人 */
    private String contact;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 客户等级：0=普通 1=VIP 2=战略 */
    private Integer level;

    /** 客户状态：0=潜在 1=正式 2=流失 */
    private Integer status;
}

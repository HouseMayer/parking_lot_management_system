package com.example.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Access_record implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 车牌号
     */
    private String licensePlate;

    /**
     * 进入时间
     */
    private LocalDateTime startTime;

    /**
     * 离场时间
     */
    private LocalDateTime endTime;

    /**
     * 车位号
     */
    private String carport;

    /**
     * 停车费用
     */
    private BigDecimal cost;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    private Long updateUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Long createUser;


}

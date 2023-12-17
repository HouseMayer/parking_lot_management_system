package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class Carport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车位id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 车位号
     */
    private String carport;

    /**
     * 区域
     */
    private String area;

    /**
     * 车位类型：1.露天2室内
     */
    private String type;

    /**
     * 车位分类：1.固定车位2.临时车位
     */
    private String classify;

    /**
     * 车牌号	
     */
    private String licensePlate;

    /**
     * 状态：1.空闲 2.占用3.维修
     */
    private String state;

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

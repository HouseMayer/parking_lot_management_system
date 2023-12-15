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
 * @since 2023-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 收入
     */
    private BigDecimal earning;

    /**
     * 时间
     */
    private LocalDateTime record_date;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 更新时间
     */
    private LocalDateTime update_time;

    /**
     * 创建时间
     */
    private LocalDateTime create_time;

    /**
     * 创建人
     */
    private Long create_user;

    /**
     * 修改人
     */
    private Long update_user;


}

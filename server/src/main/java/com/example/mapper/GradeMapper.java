package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.annotation.AutoFill;
import com.example.entity.Grade;
import com.example.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {

    @Select("select * from grade where license_plate = #{licensePlate}")
    Grade getLicensePlate(String licensePlate);

    @AutoFill(OperationType.INSERT)
    @Insert("insert into grade(license_plate,owner,grade,deadline,phone,comment) " +
            "values" +
            "(#{licensePlate},#{owner},#{grade},#{deadline},#{phone},#{comment})")
    void insertGrade(Grade grade);

    @Select("select license_plate from grade where license_plate = #{licensePlate}")
    String getByLicensePlate(String licensePlate);

    @Select("select grade from grade where license_plate = #{licensePlate}")
    String getGradeByLicensePlate(String licensePlate);
}

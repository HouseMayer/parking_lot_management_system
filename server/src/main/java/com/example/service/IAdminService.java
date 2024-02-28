package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.AdminDTO;
import com.example.dto.AdminLoginDTO;
import com.example.dto.AdminPageQueryDTO;
import com.example.entity.Admin;
import com.example.result.PageResult;
import com.example.vo.AdminInfoVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HouseMayer
 * @since 2023-12-15
 */
public interface IAdminService extends IService<Admin> {

    PageResult pageQuery(AdminPageQueryDTO adminPageQueryDTO);

    Admin login(AdminLoginDTO adminLoginDTO);

    void save(AdminDTO adminDTO);

    @Transactional
    void update(AdminDTO adminDTO);

    @Transactional
    void deleteBatch(List<Long> ids);


    AdminInfoVO info(String token);
}

package com.fm.mapper;

import POJO.Login.AdminLoginRequest;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PersonnelManagementMapper {

    int selectByAdminName(String adminName);

    void insertAdmin(AdminLoginRequest adminLoginRequest);

    List<AdminLoginRequest> selectAll(int offset, Integer size, String sortField, String sortOrder);

    @Select("select count(*) from administrator")
    int selectTotal();

    void updateAdmin(AdminLoginRequest adminLoginRequest);

    @Select("select count(*) from administrator where id = #{id}")
    int selectById(Integer id);

    @Delete("delete from administrator where id = #{id}")
    void deleteAdmin(Integer id);

    @Select("select permission from administrator where id = #{id}")
    int selectPermissionById(Integer id);
}

package com.fm.controller.Auth;

import POJO.Login.AdminLoginRequest;
import POJO.Result;
import POJO.DataList;
import com.fm.service.PersonnelManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/Management")
@RequiredArgsConstructor
public class PersonnelManagement {

    private final PersonnelManagementService personnelManagementService;

    @PostMapping("/Add")
    public Result addPersonnel(@RequestBody AdminLoginRequest adminLoginRequest){
        log.info("addPersonnel: {}", adminLoginRequest);
        if(adminLoginRequest.getModifyPermission() != 1){
            return Result.error(HttpStatus.FORBIDDEN, "权限不足,请联系最高级别管理员");
        }

        boolean isSuccess = personnelManagementService.addPersonnel(adminLoginRequest);
        if(!isSuccess){
            return Result.error(HttpStatus.BAD_REQUEST, "添加失败");
        }

        return Result.success(HttpStatus.OK, "添加成功");
    }

    @GetMapping("/GetAll")
    public Result getPersonnelByPermission(Integer permission, String sortBy, Integer page, Integer size){
        log.info("最高级管理员查询所有人员信息");
        if(permission != 1){
            return Result.error(HttpStatus.FORBIDDEN, "权限不足,无法查询");
        }

        DataList<AdminLoginRequest> adminPersonnelList = personnelManagementService.getPersonnel(sortBy, page, size);

        return Result.success(adminPersonnelList);
    }

    @PutMapping("/Update")
    public Result updatePersonnel(@RequestBody AdminLoginRequest adminLoginRequest) {
        log.info("updatePersonnel: {}", adminLoginRequest);
        if (adminLoginRequest.getModifyPermission() != 1) {
            return Result.error(HttpStatus.FORBIDDEN, "权限不足,请联系最高级别管理员");
        }
        boolean isSuccess = personnelManagementService.updatePersonnel(adminLoginRequest);
        if(!isSuccess){
            return Result.error(HttpStatus.BAD_REQUEST, "更新失败");
        }

        return Result.success(HttpStatus.OK, "更新成功");
    }

    @DeleteMapping("/Delete")
    public Result deletePersonnel(@RequestBody AdminLoginRequest adminLoginRequest){
        log.info("deletePersonnel: {}", adminLoginRequest);
        if (adminLoginRequest.getModifyPermission() != 1) {
            return Result.error(HttpStatus.FORBIDDEN, "权限不足,请联系最高级别管理员");
        }
        boolean isSuccess = personnelManagementService.deletePersonnel(adminLoginRequest);
        if(!isSuccess){
            return Result.error(HttpStatus.BAD_REQUEST, "删除失败");
        }

        return Result.success(HttpStatus.OK, "删除成功");
    }

}

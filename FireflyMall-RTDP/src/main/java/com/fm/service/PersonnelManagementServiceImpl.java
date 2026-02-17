package com.fm.service;

import POJO.DataList;
import POJO.Login.AdminLoginRequest;
import com.fm.mapper.PersonnelManagementMapper;
import com.fm.service.PersonnelManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonnelManagementServiceImpl implements PersonnelManagementService {

    private final PersonnelManagementMapper personnelManagementMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPersonnel(AdminLoginRequest adminLoginRequest) {

        String adminName = adminLoginRequest.getAdminName();

        int count = personnelManagementMapper.selectByAdminName(adminName);
        if(count > 0){
            return false;
        }
        String encryptedPassword = bCryptPasswordEncoder.encode(adminLoginRequest.getAdminPassword());
        adminLoginRequest.setAdminPassword(encryptedPassword);
        adminLoginRequest.setCreateTime(LocalDateTime.now());
        adminLoginRequest.setUpdateTime(LocalDateTime.now());
        personnelManagementMapper.insertAdmin(adminLoginRequest);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataList<AdminLoginRequest> getPersonnel(String sortBy, Integer page, Integer size) {
        // 分页查询
        int offset = (page - 1) * size;
        if(!sortBy.contains("_")){
            sortBy = "id_desc";
        }
        String[] sortByArray = sortBy.split("_");
        String sortField = sortByArray[0];
        String sortOrder = sortByArray[1];

        List<AdminLoginRequest> adminLoginRequests = personnelManagementMapper.selectAll(offset, size, sortField, sortOrder);
        // 总条数
        int total = personnelManagementMapper.selectTotal();
        // 封装返回结果
        DataList<AdminLoginRequest> dataList = new DataList<>();
        dataList.setData(adminLoginRequests);
        dataList.setTotal(total);
        dataList.setPage(page);
        dataList.setSize(size);

        return dataList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePersonnel(AdminLoginRequest adminLoginRequest) {
        int count = personnelManagementMapper.selectById(adminLoginRequest.getId());
        if(count == 0){
            return false;
        }
        adminLoginRequest.setUpdateTime(LocalDateTime.now());
        if (adminLoginRequest.getAdminPassword() != null) {
            String encryptedPassword = bCryptPasswordEncoder.encode(adminLoginRequest.getAdminPassword());
            adminLoginRequest.setAdminPassword(encryptedPassword);
        }
        personnelManagementMapper.updateAdmin(adminLoginRequest);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePersonnel(AdminLoginRequest adminLoginRequest) {
        int permission = personnelManagementMapper.selectPermissionById(adminLoginRequest.getId());
        if(permission == 1) {
            return false;
        }
        int count = personnelManagementMapper.selectById(adminLoginRequest.getId());
        if(count == 0){
            return false;
        }
        personnelManagementMapper.deleteAdmin(adminLoginRequest.getId());
        return true;
    }

}

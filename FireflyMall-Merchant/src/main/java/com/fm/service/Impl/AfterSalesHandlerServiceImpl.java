package com.fm.service.Impl;

import com.fm.POJO.AfterSalesEvidence;
import com.fm.POJO.AfterSalesExchange;
import com.fm.mapper.AfterSalesHandlerMapper;
import com.fm.service.AfterSalesHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AfterSalesHandlerServiceImpl implements AfterSalesHandlerService {

    private final AfterSalesHandlerMapper afterSalesEvidenceMapper;

    @Override
    public AfterSalesEvidence getEvidence(Long applyId, Integer type, int offset, int limit) {
        try{
            return afterSalesEvidenceMapper.selectEvidenceByApplyIdAndType(applyId, type, offset, limit);
        }catch (Exception e){
            log.error("查询售后证据失败", e);
            return null;
        }
    }

    @Override
    public Map<String, String> exchangeExchange(AfterSalesExchange afterSalesExchange) {
        Map<String, String> exchangeResultMap = new HashMap<>();
        try{
            afterSalesEvidenceMapper.updateEvidence(afterSalesExchange);
            exchangeResultMap.put("status", "ok");
            exchangeResultMap.put("msg", "售后换货信息更改成功");
            return exchangeResultMap;
        }catch (Exception e){
            log.error("售后换货信息更改失败", e);
            exchangeResultMap.put("status", "error");
            exchangeResultMap.put("msg", "售后换货信息更改失败: " + e.getMessage());
            return exchangeResultMap;
        }
    }

    @Override
    public AfterSalesExchange getExchange(Long applyId, int offset, int limit) {
        try{
            return afterSalesEvidenceMapper.selectExchangeByApplyIdAndType(applyId, offset, limit);
        }catch (Exception e){
            log.error("查询售后换货信息失败", e);
            return null;
        }
    }
}

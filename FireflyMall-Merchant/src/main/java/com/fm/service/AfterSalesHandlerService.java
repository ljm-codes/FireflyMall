package com.fm.service;

import com.fm.POJO.AfterSalesEvidence;
import com.fm.POJO.AfterSalesExchange;

import java.util.Map;

public interface AfterSalesHandlerService {
    AfterSalesEvidence getEvidence(Long applyId, Integer type, int offset, int limit);

    Map<String, String> exchangeExchange(AfterSalesExchange afterSalesExchange);

    AfterSalesExchange getExchange(Long applyId, int offset, int limit);
}

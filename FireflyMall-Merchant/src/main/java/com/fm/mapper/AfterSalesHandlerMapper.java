package com.fm.mapper;

import com.fm.POJO.AfterSalesEvidence;
import com.fm.POJO.AfterSalesExchange;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AfterSalesHandlerMapper {
    AfterSalesEvidence selectEvidenceByApplyIdAndType(@Param("applyId") Long applyId, @Param("type") Integer type, @Param("offset") int offset, @Param("limit") int limit);

    void updateEvidence(AfterSalesExchange afterSalesExchange);

    AfterSalesExchange selectExchangeByApplyIdAndType(@Param("applyId") Long applyId, @Param("offset") int offset, @Param("limit") int limit);
}
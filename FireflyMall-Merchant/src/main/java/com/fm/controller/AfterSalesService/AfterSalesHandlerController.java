package com.fm.controller.AfterSalesService;

import POJO.Result;
import com.fm.POJO.AfterSalesExchange;
import com.fm.service.AfterSalesHandlerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/afterSales")
@RequiredArgsConstructor
@Slf4j
public class AfterSalesHandlerController {
    // 当前执只正对于AI客服所申请的售后服务进行处理，未来将会扩展

    private final AfterSalesHandlerService  afterSalesHandlerService;

    @GetMapping("/evidence")
    public Result getEvidence(
            Long applyId,
            Integer type,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return Result.success(afterSalesHandlerService.getEvidence(applyId, type, offset, limit));
    }

    @PutMapping("/exchange")
    public Result exchangeExchange(@Valid @RequestBody AfterSalesExchange afterSalesExchange) {
        Map<String, String> exchangeResultMap = afterSalesHandlerService.exchangeExchange(afterSalesExchange);
        if (exchangeResultMap.get("status").equals("error")){
            return Result.error(HttpStatus.BAD_REQUEST, exchangeResultMap.get("msg"));
        }
        return Result.success(exchangeResultMap);
    }

    @GetMapping("/exchange/{applyId}")
    public Result getExchange(
            @PathVariable Long applyId, // 如果为-1，则为全选
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return Result.success(afterSalesHandlerService.getExchange(applyId, offset, limit));
    }

}

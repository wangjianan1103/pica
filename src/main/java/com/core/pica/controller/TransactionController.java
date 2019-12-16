package com.core.pica.controller;

import com.core.pica.model.params.QueryStatusParam;
import com.core.pica.model.params.QueryStatusResp;
import com.core.pica.model.params.TransactionDoParam;
import com.core.pica.model.params.TransactionDoResp;
import com.core.pica.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单交易接口
 */
@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * 发起订单交易
     */
    @RequestMapping(value = "do", method = RequestMethod.POST)
    public TransactionDoResp transactionDo(@RequestBody TransactionDoParam param) {
        return transactionService.transactionDo(param);
    }

    /**
     * 发起订单交易
     */
    @RequestMapping(value = "order/status", method = RequestMethod.POST)
    public QueryStatusResp queryStatus(@RequestBody QueryStatusParam param) {
        return transactionService.queryStatus(param);
    }

}

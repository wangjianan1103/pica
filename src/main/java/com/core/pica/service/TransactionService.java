package com.core.pica.service;

import com.core.pica.model.params.QueryStatusParam;
import com.core.pica.model.params.QueryStatusResp;
import com.core.pica.model.params.TransactionDoParam;
import com.core.pica.model.params.TransactionDoResp;

public interface TransactionService {

    /**
     * 交易业务 - 执行
     */
    TransactionDoResp transactionDo(TransactionDoParam param);

    /**
     * 查询订单状态
     */
    QueryStatusResp queryStatus(QueryStatusParam param);
}

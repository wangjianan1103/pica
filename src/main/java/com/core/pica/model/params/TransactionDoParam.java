package com.core.pica.model.params;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDoParam {
    private BigDecimal payAmount; // 支付金额
}

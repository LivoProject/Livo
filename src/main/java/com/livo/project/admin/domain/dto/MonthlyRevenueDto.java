package com.livo.project.admin.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueDto {

    private String ym;                // YYYY-MM
    private BigDecimal revenue;       // 매출 합계
    private long paidCount;           // 결제 완료 건수
    private long failOrCancelCount;   // 실패 + 취소 건수

    // 매출 성장률 계산용 (비교할 때)
    public double getSuccessRate() {
        long total = paidCount + failOrCancelCount;
        if (total == 0) return 0.0;
        return (double) paidCount / total * 100;
    }
}

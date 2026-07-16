package com.kangong.marketcycle.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CyclePhase {
    FINANCIAL("금융장세", "금리 인하 → 유동성 공급 → 주가 상승 시작",
            "#007bff", "spring"),
    EARNINGS("실적장세", "기업 실적 개선 → 경기 확장 → 본격 상승",
            "#28a745", "summer"),
    REVERSE_FINANCIAL("역금융장세", "금리 인상 → 유동성 축소 → 하락 시작",
            "#fd7e14", "autumn"),
    REVERSE_EARNINGS("역실적장세", "기업 실적 악화 → 경기 침체 → 본격 하락",
            "#dc3545", "winter");

    private final String label;
    private final String description;
    private final String color;
    private final String season;
}

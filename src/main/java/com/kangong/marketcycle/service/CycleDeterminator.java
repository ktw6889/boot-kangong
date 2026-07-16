package com.kangong.marketcycle.service;

import com.kangong.marketcycle.dto.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CycleDeterminator {

    private static final int FIN = 0, EARN = 1, R_FIN = 2, R_EARN = 3;

    public CycleDeterminationResult determine(MacroIndicatorData data) {
        double[] scores = new double[4];
        List<String> signals = new ArrayList<>();

        scoreRate(data, scores, signals);
        scoreLiquidity(data, scores, signals);
        scoreEarnings(data, scores, signals);
        scorePmi(data, scores, signals);
        scoreValuation(data, scores, signals);

        CyclePhase[] phases = CyclePhase.values();
        int maxIdx = 0;
        for (int i = 1; i < 4; i++) {
            if (scores[i] > scores[maxIdx]) maxIdx = i;
        }

        CyclePhase current = phases[maxIdx];
        CyclePhase next = phases[(maxIdx + 1) % 4];
        int progress = calcProgress(data, current);

        double total = 0;
        for (double s : scores) total += s;
        int confidence = total > 0 ? (int) (scores[maxIdx] / total * 100) : 25;

        Map<CyclePhase, Double> scoreMap = new EnumMap<>(CyclePhase.class);
        for (int i = 0; i < 4; i++) {
            scoreMap.put(phases[i], Math.round(scores[i] * 10.0) / 10.0);
        }

        return CycleDeterminationResult.builder()
                .phase(current)
                .nextPhase(next)
                .progressPercent(progress)
                .confidence(confidence)
                .phaseScores(scoreMap)
                .signals(signals)
                .summary(buildSummary(current, confidence, signals))
                .build();
    }

    private void scoreRate(MacroIndicatorData d, double[] s, List<String> sig) {
        double combined = d.getBaseRateChange6m() * 0.7 + d.getFedRateChange6m() * 0.3;

        if (combined <= -0.5) {
            s[FIN] += 30;
            sig.add("기준금리 인하 사이클 → 금융장세 강한 시그널");
        } else if (combined <= -0.15) {
            s[FIN] += 20;
            s[EARN] += 10;
            sig.add("소폭 금리 인하 → 금융장세/실적장세 전환기");
        } else if (combined <= 0.15) {
            s[EARN] += 15;
            s[FIN] += 8;
            s[R_FIN] += 7;
            sig.add("금리 동결 국면 → 실적장세 또는 전환기");
        } else if (combined <= 0.5) {
            s[R_FIN] += 25;
            s[R_EARN] += 5;
            sig.add("금리 인상 시작 → 역금융장세 시그널");
        } else {
            s[R_FIN] += 30;
            s[R_EARN] += 10;
            sig.add("급격한 금리 인상 → 역금융장세 강한 시그널");
        }
    }

    private void scoreLiquidity(MacroIndicatorData d, double[] s, List<String> sig) {
        double m2 = d.getM2GrowthRate();
        if (m2 > 7) {
            s[FIN] += 20;
            sig.add(String.format("M2 통화량 급증(+%.1f%%) → 유동성 확장, 금융장세 지지", m2));
        } else if (m2 > 5) {
            s[FIN] += 12;
            s[EARN] += 8;
            sig.add(String.format("M2 완만 증가(+%.1f%%) → 유동성 양호", m2));
        } else if (m2 > 3) {
            s[EARN] += 10;
            s[R_FIN] += 10;
            sig.add(String.format("M2 증가 둔화(+%.1f%%) → 유동성 축소 조짐", m2));
        } else if (m2 > 0) {
            s[R_FIN] += 12;
            s[R_EARN] += 8;
            sig.add(String.format("M2 저성장(+%.1f%%) → 유동성 위축", m2));
        } else {
            s[R_EARN] += 20;
            sig.add(String.format("M2 감소(%.1f%%) → 유동성 고갈, 역실적장세 시그널", m2));
        }
    }

    private void scoreEarnings(MacroIndicatorData d, double[] s, List<String> sig) {
        double eg = d.getEarningsGrowthRate();
        if (eg > 20) {
            s[EARN] += 25;
            sig.add(String.format("기업이익 고성장(+%.0f%%) → 실적장세 강한 시그널", eg));
        } else if (eg > 10) {
            s[EARN] += 18;
            s[FIN] += 7;
            sig.add(String.format("기업이익 견조한 회복(+%.0f%%) → 실적장세 진입 중", eg));
        } else if (eg > 0) {
            s[FIN] += 15;
            s[EARN] += 5;
            s[R_EARN] += 5;
            sig.add(String.format("기업이익 초기 회복(+%.0f%%) → 금융장세 내 실적 개선 시작", eg));
        } else if (eg > -10) {
            s[R_FIN] += 15;
            s[R_EARN] += 10;
            sig.add(String.format("기업이익 감소(%.0f%%) → 실적 악화 시그널", eg));
        } else {
            s[R_EARN] += 25;
            sig.add(String.format("기업이익 급감(%.0f%%) → 역실적장세 강한 시그널", eg));
        }
    }

    private void scorePmi(MacroIndicatorData d, double[] s, List<String> sig) {
        double pmi = d.getPmi();
        if (pmi > 55) {
            s[EARN] += 15;
            sig.add(String.format("PMI 강세(%.1f) → 제조업 확장, 실적장세 지지", pmi));
        } else if (pmi > 52) {
            s[EARN] += 12;
            s[FIN] += 3;
            sig.add(String.format("PMI 확장(%.1f) → 경기 회복 확인", pmi));
        } else if (pmi > 50) {
            s[FIN] += 8;
            s[EARN] += 7;
            sig.add(String.format("PMI 확장 경계(%.1f) → 경기 회복 초기", pmi));
        } else if (pmi > 48) {
            s[FIN] += 8;
            s[R_EARN] += 7;
            sig.add(String.format("PMI 수축(%.1f) → 경기 둔화, 바닥 탐색 중", pmi));
        } else {
            s[R_EARN] += 15;
            sig.add(String.format("PMI 급수축(%.1f) → 경기 침체, 역실적장세 시그널", pmi));
        }
    }

    private void scoreValuation(MacroIndicatorData d, double[] s, List<String> sig) {
        double ratio = d.getKospiPer() / d.getHistoricalPerAvg();
        if (ratio < 0.85) {
            s[FIN] += 5;
            s[R_EARN] += 5;
            sig.add("코스피 PER 역사적 저평가 → 바닥권 또는 금융장세 초기");
        } else if (ratio < 0.95) {
            s[FIN] += 8;
            s[EARN] += 2;
            sig.add("코스피 PER 평균 하회 → 밸류에이션 확장 여력");
        } else if (ratio < 1.05) {
            s[EARN] += 7;
            s[FIN] += 3;
            sig.add("코스피 PER 적정 수준 → 실적 기반 상승 국면");
        } else if (ratio < 1.15) {
            s[EARN] += 4;
            s[R_FIN] += 6;
            sig.add("코스피 PER 고평가 접근 → 역금융장세 초기 가능");
        } else {
            s[R_FIN] += 10;
            sig.add("코스피 PER 과열 → 역금융장세 시그널");
        }
    }

    private int calcProgress(MacroIndicatorData d, CyclePhase phase) {
        switch (phase) {
            case FINANCIAL: {
                double rateProg = Math.min(60, Math.abs(d.getBaseRateChange6m()) / 1.25 * 60);
                double earnProg = Math.min(40, Math.max(0, d.getEarningsGrowthRate()) / 12.0 * 40);
                return clamp((int) (rateProg + earnProg), 10, 90);
            }
            case EARNINGS: {
                double ep = Math.min(50, d.getEarningsGrowthRate() / 30.0 * 50);
                double pp = Math.min(50, Math.max(0, (d.getPmi() - 50)) / 8.0 * 50);
                return clamp((int) (ep + pp), 10, 90);
            }
            case REVERSE_FINANCIAL: {
                double rp = Math.min(50, d.getBaseRateChange6m() / 1.5 * 50);
                double mp = Math.min(50, Math.max(0, (5 - d.getM2GrowthRate())) / 5.0 * 50);
                return clamp((int) (rp + mp), 10, 90);
            }
            case REVERSE_EARNINGS: {
                double rep = Math.min(50, Math.max(0, -d.getEarningsGrowthRate()) / 20.0 * 50);
                double pmp = Math.min(50, Math.max(0, (50 - d.getPmi())) / 10.0 * 50);
                return clamp((int) (rep + pmp), 10, 90);
            }
            default:
                return 50;
        }
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private String buildSummary(CyclePhase phase, int confidence, List<String> signals) {
        String top = signals.stream().limit(3).collect(Collectors.joining(". "));
        return String.format("%s(%s) 국면으로 판단 (신뢰도 %d%%). %s",
                phase.getLabel(), phase.getSeason(), confidence, top);
    }
}

package com.kangong.marketcycle.service;

import com.kangong.marketcycle.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketCycleService {

    private final CycleDeterminator determinator;
    private final MacroDataFetcher macroDataFetcher;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    // === 섹터 타입 ===
    private static final int GROWTH = 0, CYCLICAL = 1, FINANCIAL_SEC = 2, DEFENSIVE = 3, COMMODITY = 4;

    // 섹터타입별 × 사이클국면별 매수확률 [type][phase]
    private static final int[][] SECTOR_BUY = {
            {80, 70, 30, 20},  // GROWTH
            {55, 80, 35, 25},  // CYCLICAL
            {75, 55, 35, 30},  // FINANCIAL_SEC
            {25, 20, 65, 70},  // DEFENSIVE
            {30, 50, 45, 60},  // COMMODITY
    };

    // 자산타입별 × 사이클국면별 매수확률 [type][phase]
    private static final int BOND = 0, GOLD = 1, CASH_RATE = 2, US_GROWTH = 3, US_DIV = 4, KR_INDEX = 5, KR_VALUE = 6;
    private static final int[][] ASSET_BUY = {
            {75, 50, 25, 80},  // BOND
            {65, 45, 55, 80},  // GOLD
            {30, 35, 70, 55},  // CASH_RATE
            {75, 80, 45, 30},  // US_GROWTH (S&P, NASDAQ)
            {50, 45, 55, 50},  // US_DIV
            {70, 75, 40, 25},  // KR_INDEX
            {70, 70, 45, 30},  // KR_VALUE
    };

    private static final String[][] SECTOR_REASONS = {
            // GROWTH
            {"금리 인하 → 성장주 밸류에이션 확장, AI/기술 투자 사이클 수혜",
                    "기업 실적 개선 확인, 기술 성장 모멘텀 지속",
                    "금리 인상 → 성장주 할인율 상승, 밸류에이션 압축",
                    "경기 침체 → 기술 투자 축소, 수요 급감"},
            // CYCLICAL
            {"경기 회복 초기, 본격 수요 회복 전 단계",
                    "경기 확장 → 수요 증가, 실적 개선 본격화",
                    "경기 고점 경과, 수요 둔화 시작",
                    "경기 침체 → 소비·투자 급감, 실적 악화"},
            // FINANCIAL_SEC
            {"금리 인하 → 대출 수요 증가, 자산건전성 개선, 고배당 매력",
                    "경기 확장 → 대출 증가, 수수료 수익 개선",
                    "금리 인상 → NIM 개선이나 대출 수요 둔화 시작",
                    "경기 침체 → 부실채권 증가, 수익성 악화"},
            // DEFENSIVE
            {"성장주 선호 구간, 방어주 매력 감소",
                    "경기 확장기 저성장 업종 소외",
                    "경기 둔화 → 안정적 현금흐름 방어주 선호",
                    "경기 침체 → 필수 수요 기반 실적 방어, 안전자산 선호"},
            // COMMODITY
            {"경기 회복 초기, 원자재 수요 본격 확대 전",
                    "경기 확장 → 원자재 수요 증가, 가격 상승",
                    "공급 과잉 vs 수요 둔화, 혼조세",
                    "경기 침체 → 수요 급감이나 안전자산(금) 수요는 증가"},
    };

    // =====================================================================
    // 대시보드 빌드 (핵심 진입점)
    // =====================================================================

    public MarketCycleDashboard buildDashboard() {
        MacroIndicatorData data = macroDataFetcher.fetch();
        CycleDeterminationResult result = determinator.determine(data);
        CyclePhase current = result.getPhase();
        CyclePhase next = result.getNextPhase();
        int progress = result.getProgressPercent();

        return MarketCycleDashboard.builder()
                .currentPhase(current)
                .progressPercent(progress)
                .diagnosisDate(LocalDate.now().format(DTF))
                .diagnosisSummary(result.getSummary())
                .nextPhaseOutlook(outlook(next))
                .cycleStartDate(cycleStart(current))
                .estimatedEndDate(cycleEnd(current))
                .remainingMonths(remainingMonths(progress))
                .prepareMonths(3)
                .durationComment(durationComment(current, progress))
                .prepareComment(prepareComment(current, next))
                .buyRecommendations(buyRecs(current))
                .sellRecommendations(sellRecs(current))
                .nextPhase(next)
                .nextBuyRecommendations(buyRecs(next))
                .nextSellRecommendations(sellRecs(next))
                .sectorOverviews(buildSectorOverviews(current))
                .assetOverviews(buildAssetOverviews(current, progress))
                .indicators(buildIndicators(data, result))
                .accountRebalances(buildAccountRebalances(current))
                .build();
    }

    // =====================================================================
    // 사이클 국면별 매수/매도 추천
    // =====================================================================

    private List<SectorRecommendation> buyRecs(CyclePhase phase) {
        switch (phase) {
            case FINANCIAL:
                return Arrays.asList(
                        rec("반도체/AI", "금리 인하 → 기술주 밸류에이션 확장 + AI 투자 사이클",
                                "삼성전자", "005930", "글로벌 메모리 1위, HBM 양산 본격화. PBR 1.0배 이하 저평가",
                                "SK하이닉스", "000660", "HBM3E 독점 공급, AI 서버 수요 직접 수혜"),
                        rec("금융/은행", "금리 인하 초기 → 대출 수요 증가, 자산건전성 개선. 고배당 매력",
                                "KB금융", "105560", "국내 1위 금융지주. 배당수익률 5%+, 자사주 매입 지속",
                                "하나금융지주", "086790", "ROE 10%+ 유지, 적극적 주주환원. 저PBR 밸류업 수혜"));
            case EARNINGS:
                return Arrays.asList(
                        rec("경기소비재/내수", "실적장세 전환 → 소비 회복 수혜. 내수 경기 반등",
                                "현대차", "005380", "글로벌 판매량 증가, 전동화 전환 가속. 밸류업 수혜",
                                "LG생활건강", "051900", "중국 소비 회복 시 화장품·생활용품 수요 반등 기대"),
                        rec("산업재/조선", "경기 확장 → 수주잔고 기반 실적 가시성 높은 업종",
                                "HD한국조선해양", "329180", "LNG선·컨테이너선 슈퍼사이클. 수주잔고 역대 최대",
                                "HD현대중공업", "329180", "방산·에너지 복합 수주 확대, 마진 개선 추세"));
            case REVERSE_FINANCIAL:
                return Arrays.asList(
                        rec("필수소비재/방어주", "금리 인상기 → 안정적 현금흐름 업종 선호",
                                "KT&G", "033780", "담배·건강기능식품 안정적 매출. 고배당 정책 유지",
                                "오리온", "271560", "해외(중국·베트남·러시아) 매출 비중 높아 내수 둔화 방어"),
                        rec("유틸리티/통신", "경기 둔화기 → 필수 서비스 기반 실적 안정성",
                                "SK텔레콤", "017670", "AI 인프라 투자 + 안정적 통신 매출. 배당수익률 4%+",
                                "KT", "030200", "AICT 전환 추진, 안정적 현금흐름. 저평가 구간"));
            case REVERSE_EARNINGS:
                return Arrays.asList(
                        rec("채권형/현금성", "경기 침체 → 안전자산 선호, 금리 인하 기대감",
                                "KODEX 국고채10년", "148070", "금리 인하 전환 시 장기채 가격 상승 수혜",
                                "TIGER CD금리투자KIS", "357870", "단기 금리 수익 + 원금 안정성"),
                        rec("금/방어자산", "경기 침체 + 불확실성 → 안전자산 프리미엄",
                                "KODEX 골드선물(H)", "132030", "달러 약세 전환 + 지정학 리스크 헤지",
                                "ACE KRX금현물", "411060", "원화 기반 금 투자, 환율 변동 추가 수혜"));
            default:
                return buyRecs(CyclePhase.FINANCIAL);
        }
    }

    private List<SectorRecommendation> sellRecs(CyclePhase phase) {
        switch (phase) {
            case FINANCIAL:
                return Arrays.asList(
                        rec("유틸리티/방어주", "금리 인하기 방어주 매력 감소, 성장주로 자금 이동",
                                "한국전력", "015760", "정책 리스크 상존, 전기요금 인상 한계. 성장성 부재",
                                "한국가스공사", "036460", "원가 연동 구조 실적 변동성 낮으나 상승 모멘텀 부재"),
                        rec("원자재/소재", "경기 회복 초기 원자재 수요 본격 확대 전. 재고 부담",
                                "POSCO홀딩스", "005490", "글로벌 철강 수요 부진, 중국발 공급 과잉 지속",
                                "롯데케미칼", "011170", "석유화학 다운사이클 장기화, 적자 지속"));
            case EARNINGS:
                return Arrays.asList(
                        rec("고배당 리츠/인프라", "경기 확장기 성장주 선호, 배당주 소외",
                                "맥쿼리인프라", "088980", "배당 매력 유지되나 성장주 대비 수익률 열위",
                                "SK리츠", "395400", "오피스 공실률 상승 우려, 자산가치 성장 둔화"),
                        rec("통신/필수소비재", "경기 확장기 저성장 업종 시장 대비 언더퍼폼",
                                "KT", "030200", "성숙 시장, 매출 성장 한계. AI 전환 아직 초기",
                                "KT&G", "033780", "담배 시장 축소 추세, 해외 성장 불확실성"));
            case REVERSE_FINANCIAL:
                return Arrays.asList(
                        rec("성장주/기술주", "금리 인상 → 할인율 상승, 고PER 종목 밸류에이션 압축",
                                "카카오", "035720", "성장 둔화 + 금리 부담, 밸류에이션 조정 불가피",
                                "네이버", "035420", "광고 매출 둔화, AI 투자 비용 증가"),
                        rec("건설/부동산", "금리 인상 → 부동산 시장 위축, PF 리스크 확대",
                                "현대건설", "000720", "수주 둔화 + PF 우발채무 리스크",
                                "DL이앤씨", "375500", "주택 시장 침체, 마진율 하락 추세"));
            case REVERSE_EARNINGS:
                return Arrays.asList(
                        rec("경기민감/산업재", "경기 침체 → 수요 급감, 실적 악화",
                                "현대차", "005380", "소비 위축으로 자동차 판매 급감 예상",
                                "HD한국조선해양", "329180", "신규 수주 감소, 수주잔고 소진 우려"),
                        rec("금융주", "경기 침체 → 부실채권 증가, 수익성 악화",
                                "KB금융", "105560", "대손충당금 적립 증가, 배당 축소 가능",
                                "하나금융지주", "086790", "기업 대출 부실화 리스크, NIM 하락"));
            default:
                return sellRecs(CyclePhase.FINANCIAL);
        }
    }

    // =====================================================================
    // 섹터별 개요 (국면에 따라 확률/시그널 동적 조정)
    // =====================================================================

    private static final Object[][] SECTOR_DEFS = {
            {"반도체/AI", GROWTH, "삼성전자", "005930", "SK하이닉스", "000660"},
            {"금융/은행", FINANCIAL_SEC, "KB금융", "105560", "하나금융지주", "086790"},
            {"2차전지", GROWTH, "LG에너지솔루션", "373220", "삼성SDI", "006400"},
            {"자동차", CYCLICAL, "현대차", "005380", "기아", "000270"},
            {"조선/해운", CYCLICAL, "HD한국조선해양", "329180", "삼성중공업", "010140"},
            {"바이오/헬스케어", GROWTH, "삼성바이오로직스", "207940", "셀트리온", "068270"},
            {"인터넷/플랫폼", GROWTH, "네이버", "035420", "카카오", "035720"},
            {"건설/부동산", CYCLICAL, "현대건설", "000720", "DL이앤씨", "375500"},
            {"철강/소재", COMMODITY, "POSCO홀딩스", "005490", "현대제철", "004020"},
            {"석유화학", COMMODITY, "롯데케미칼", "011170", "LG화학", "051910"},
            {"유틸리티", DEFENSIVE, "한국전력", "015760", "한국가스공사", "036460"},
            {"통신", DEFENSIVE, "KT", "030200", "SK텔레콤", "017670"},
            {"필수소비재", DEFENSIVE, "KT&G", "033780", "오리온", "271560"},
            {"리츠/인프라", DEFENSIVE, "맥쿼리인프라", "088980", "SK리츠", "395400"},
    };

    // 섹터별 세부 오프셋 (같은 타입 내 차별화)
    private static final int[] SECTOR_OFFSETS = {5, 0, -5, 0, 0, -5, -5, -10, -5, -10, 0, -5, 5, 0};

    private List<SectorOverview> buildSectorOverviews(CyclePhase phase) {
        int pi = phase.ordinal();
        return Arrays.stream(indexRange(SECTOR_DEFS.length))
                .mapToObj(i -> {
                    Object[] d = SECTOR_DEFS[i];
                    int type = (int) d[1];
                    int buy = clamp(SECTOR_BUY[type][pi] + SECTOR_OFFSETS[i], 10, 90);
                    int sell = 100 - buy;
                    String signal = buy >= 65 ? "BUY" : buy <= 35 ? "SELL" : "HOLD";
                    return SectorOverview.builder()
                            .sector((String) d[0])
                            .buyProbability(buy).sellProbability(sell).signal(signal)
                            .stock1((String) d[2]).stock1Code((String) d[3])
                            .stock2((String) d[4]).stock2Code((String) d[5])
                            .reason(SECTOR_REASONS[type][pi])
                            .build();
                })
                .collect(Collectors.toList());
    }

    // =====================================================================
    // 자산별 개요
    // =====================================================================

    private static final Object[][] ASSET_DEFS = {
            {"채권", BOND, "KODEX 국고채10년", "148070", "TIGER 미국채10년선물", "305080"},
            {"금", GOLD, "KODEX 골드선물(H)", "132030", "ACE KRX금현물", "411060"},
            {"CD금리", CASH_RATE, "TIGER CD금리투자KIS", "357870", "KODEX CD금리액티브", "459580"},
            {"S&P500", US_GROWTH, "TIGER 미국S&P500", "360750", "KODEX 미국S&P500TR", "379800"},
            {"나스닥100", US_GROWTH, "TIGER 미국나스닥100", "133690", "KODEX 미국나스닥100TR", "379810"},
            {"미국배당다우존스", US_DIV, "TIGER 미국배당다우존스", "458730", "ACE 미국배당다우존스", "402970"},
            {"KODEX200", KR_INDEX, "KODEX 200", "069500", "TIGER 200", "102110"},
            {"코리아밸류업", KR_VALUE, "KODEX 코리아밸류업", "490100", "TIGER 코리아밸류업", "490090"},
    };

    private static final String[][] ASSET_REASONS = {
            // BOND
            {"금리 인하 사이클 → 채권 가격 상승, 장기채 듀레이션 효과",
                    "금리 안정기 → 이자 수익 중심, 가격 상승 제한적",
                    "금리 인상 → 채권 가격 하락, 듀레이션 리스크",
                    "경기 침체 → 금리 인하 기대, 안전자산 수요 급증"},
            // GOLD
            {"달러 약세 + 금리 인하 → 금 강세, 지정학 리스크 헤지",
                    "경기 확장 → 위험자산 선호, 금 수요 상대적 감소",
                    "인플레이션 헤지 수요, 불확실성 프리미엄",
                    "경기 침체 + 불확실성 → 안전자산 금 수요 급증"},
            // CASH_RATE
            {"기준금리 인하로 CD금리 하락 추세, 수익률 매력 감소",
                    "금리 안정기 → 적정 수익률 유지",
                    "금리 인상 → CD금리 상승, 단기 수익 매력 증가",
                    "금리 고점 → CD금리 높으나 인하 전환 임박"},
            // US_GROWTH
            {"연준 인하 사이클 + AI 성장 → 미국 대형주 강세",
                    "기업 실적 호조 → 미국 시장 견조한 상승",
                    "연준 인상 → 미국 성장주 밸류에이션 압축",
                    "글로벌 경기 침체 → 미국 증시 하락, 그러나 상대적 안전"},
            // US_DIV
            {"성장주 선호 국면, 배당주 대비 수익률 열위",
                    "경기 확장이나 성장주 대비 매력 제한적",
                    "경기 둔화 방어 + 안정적 배당 수익",
                    "경기 침체 → 방어적 배당주 선호, 그러나 배당 삭감 리스크"},
            // KR_INDEX
            {"금리 인하 + 밸류업 정책 → 코스피 상승 기대",
                    "기업 실적 개선 → 코스피 본격 상승",
                    "금리 인상 + 유동성 축소 → 코스피 하락 압력",
                    "경기 침체 → 코스피 약세, 바닥 탐색"},
            // KR_VALUE
            {"밸류업 정책 수혜 + 저PBR 리레이팅, 정책 모멘텀",
                    "실적 개선 + 밸류업 → 저평가주 재평가",
                    "금리 인상기 밸류업 모멘텀 약화",
                    "경기 침체 → 밸류 트랩 리스크, 정책 효과 제한적"},
    };

    private static final int[] ASSET_OFFSETS = {0, -5, 0, 0, 5, 0, -5, 0};

    private List<SectorOverview> buildAssetOverviews(CyclePhase phase, int progress) {
        int pi = phase.ordinal();
        int ni = (pi + 1) % 4;
        return Arrays.stream(indexRange(ASSET_DEFS.length))
                .mapToObj(i -> {
                    Object[] d = ASSET_DEFS[i];
                    int type = (int) d[1];
                    int buy = clamp(ASSET_BUY[type][pi] + ASSET_OFFSETS[i], 10, 90);
                    int sell = 100 - buy;
                    String signal = buy >= 65 ? "BUY" : buy <= 35 ? "SELL" : "HOLD";

                    int buy3m = buy;
                    int nextBuy = clamp(ASSET_BUY[type][ni] + ASSET_OFFSETS[i], 10, 90);
                    int buy6m = blend(buy, nextBuy, progress);

                    return SectorOverview.builder()
                            .sector((String) d[0])
                            .buyProbability(buy).sellProbability(sell).signal(signal)
                            .stock1((String) d[2]).stock1Code((String) d[3])
                            .stock2((String) d[4]).stock2Code((String) d[5])
                            .reason(ASSET_REASONS[type][pi])
                            .buyProbability3m(buy3m).sellProbability3m(100 - buy3m)
                            .buyProbability6m(buy6m).sellProbability6m(100 - buy6m)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // =====================================================================
    // 지표 빌드
    // =====================================================================

    private List<CycleIndicator> buildIndicators(MacroIndicatorData data, CycleDeterminationResult result) {
        return Arrays.asList(
                CycleIndicator.builder()
                        .name("기준금리").value(data.getBaseRate() + "%")
                        .trend(data.getBaseRateChange6m() < -0.1 ? "DOWN" : data.getBaseRateChange6m() > 0.1 ? "UP" : "FLAT")
                        .signal(rateSignal(data.getBaseRateChange6m()))
                        .description(String.format("한국은행 기준금리 (6개월 변동: %+.2f%%p)", data.getBaseRateChange6m()))
                        .build(),
                CycleIndicator.builder()
                        .name("M2 통화량").value(String.format("전년비 +%.1f%%", data.getM2GrowthRate()))
                        .trend(data.getM2GrowthRate() > 5 ? "UP" : data.getM2GrowthRate() > 2 ? "FLAT" : "DOWN")
                        .signal(data.getM2GrowthRate() > 5 ? "POSITIVE" : data.getM2GrowthRate() > 2 ? "NEUTRAL" : "NEGATIVE")
                        .description("시중 유동성 증가율")
                        .build(),
                CycleIndicator.builder()
                        .name("코스피 PER").value(String.format("%.1f배", data.getKospiPer()))
                        .trend(data.getKospiPer() < data.getHistoricalPerAvg() ? "DOWN" : "UP")
                        .signal(data.getKospiPer() < data.getHistoricalPerAvg() * 0.95 ? "POSITIVE" : data.getKospiPer() > data.getHistoricalPerAvg() * 1.1 ? "NEGATIVE" : "NEUTRAL")
                        .description(String.format("역사적 평균 %.1f배 대비 %.0f%%",
                                data.getHistoricalPerAvg(), data.getKospiPer() / data.getHistoricalPerAvg() * 100))
                        .build(),
                CycleIndicator.builder()
                        .name("기업이익 증가율").value(String.format("전년비 %+.0f%%", data.getEarningsGrowthRate()))
                        .trend(data.getEarningsGrowthRate() > 5 ? "UP" : data.getEarningsGrowthRate() > -5 ? "FLAT" : "DOWN")
                        .signal(data.getEarningsGrowthRate() > 15 ? "POSITIVE" : data.getEarningsGrowthRate() > 0 ? "NEUTRAL" : "NEGATIVE")
                        .description("코스피 영업이익 증가율")
                        .build(),
                CycleIndicator.builder()
                        .name("제조업 PMI").value(String.format("%.1f", data.getPmi()))
                        .trend(data.getPmi() > 50 ? "UP" : "DOWN")
                        .signal(data.getPmi() > 52 ? "POSITIVE" : data.getPmi() > 48 ? "NEUTRAL" : "NEGATIVE")
                        .description(data.getPmi() >= 50 ? "확장 국면" : "수축 국면")
                        .build(),
                CycleIndicator.builder()
                        .name("미국 Fed 금리").value(data.getFedRate() + "%")
                        .trend(data.getFedRateChange6m() < -0.1 ? "DOWN" : data.getFedRateChange6m() > 0.1 ? "UP" : "FLAT")
                        .signal(rateSignal(data.getFedRateChange6m()))
                        .description(String.format("연준 기준금리 (6개월 변동: %+.2f%%p)", data.getFedRateChange6m()))
                        .build(),
                CycleIndicator.builder()
                        .name("원/달러 환율").value(String.format("%.0f원", data.getExchangeRate()))
                        .trend(data.getExchangeRate() > 1350 ? "UP" : data.getExchangeRate() > 1250 ? "FLAT" : "DOWN")
                        .signal(data.getExchangeRate() < 1300 ? "POSITIVE" : data.getExchangeRate() < 1400 ? "NEUTRAL" : "NEGATIVE")
                        .description("원/달러 환율")
                        .build()
        );
    }

    // =====================================================================
    // 국면별 텍스트 생성
    // =====================================================================

    private String outlook(CyclePhase next) {
        switch (next) {
            case EARNINGS:
                return "기업 실적 턴어라운드 확인 시 실적장세 전환 예상. 반도체·수출주 실적 개선 여부가 전환 시그널.";
            case REVERSE_FINANCIAL:
                return "중앙은행 긴축 전환 시 역금융장세 진입 예상. 물가 반등, 금리 인상 시그널 주시 필요.";
            case REVERSE_EARNINGS:
                return "기업 실적 악화 가시화 시 역실적장세 진입. 주요 기업 어닝쇼크, PMI 급락 모니터링 필요.";
            case FINANCIAL:
                return "경기 바닥 확인 + 금리 인하 전환 시 새로운 금융장세 시작 예상. 정책 전환 시그널 주시.";
            default:
                return "";
        }
    }

    private String cycleStart(CyclePhase phase) {
        switch (phase) {
            case FINANCIAL: return "2025년 10월";
            case EARNINGS: return "추정: 2027년 상반기";
            case REVERSE_FINANCIAL: return "미정";
            case REVERSE_EARNINGS: return "미정";
            default: return "미정";
        }
    }

    private String cycleEnd(CyclePhase phase) {
        switch (phase) {
            case FINANCIAL: return "2026년 말 ~ 2027년 1분기";
            case EARNINGS: return "추정: 2028년 하반기";
            case REVERSE_FINANCIAL: return "미정";
            case REVERSE_EARNINGS: return "미정";
            default: return "미정";
        }
    }

    private int remainingMonths(int progress) {
        int totalEstimate = 15;
        return Math.max(1, (int) ((100 - progress) / 100.0 * totalEstimate));
    }

    private String durationComment(CyclePhase phase, int progress) {
        String phaseName = phase.getLabel();
        return String.format("%s는 통상 12~18개월 지속. 현재 진행률 %d%%로 추정. "
                        + "핵심 지표 변화(금리 방향 전환, 기업이익 추세 변경, PMI 기조 전환) 시 조기 종료 가능.",
                phaseName, progress);
    }

    private String prepareComment(CyclePhase current, CyclePhase next) {
        return String.format("%s → %s 전환 약 3개월 전부터 포트폴리오 리밸런싱 준비 필요. "
                        + "핵심 전환 시그널: ①금리 방향 전환, ②분기 실적 추세 변화, ③PMI 기조 전환.",
                current.getLabel(), next.getLabel());
    }

    // =====================================================================
    // 유틸리티
    // =====================================================================

    private SectorRecommendation rec(String sector, String reason,
                                      String s1, String c1, String r1,
                                      String s2, String c2, String r2) {
        return SectorRecommendation.builder()
                .sector(sector).reason(reason)
                .stock1(s1).stock1Code(c1).stock1Reason(r1)
                .stock2(s2).stock2Code(c2).stock2Reason(r2)
                .build();
    }

    private String rateSignal(double rateChange) {
        if (rateChange < -0.25) return "POSITIVE";
        if (rateChange > 0.25) return "NEGATIVE";
        return "NEUTRAL";
    }

    private int blend(int current, int next, int progress) {
        double weight = Math.max(0, (progress - 40.0) / 50.0);
        return clamp((int) (current * (1 - weight) + next * weight), 10, 90);
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private int[] indexRange(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) arr[i] = i;
        return arr;
    }

    // =====================================================================
    // 계좌별 리밸런싱
    // =====================================================================

    private static final int H_GROWTH = 0, H_CYCLICAL = 1, H_FINANCIAL = 2, H_DEFENSIVE = 3,
                              H_COMMODITY = 4, H_BOND = 5, H_GOLD = 6, H_CASH = 7;

    // [sectorType][phaseIndex] 비중 변화량 (FINANCIAL=0, EARNINGS=1, REV_FIN=2, REV_EARN=3)
    private static final int[][] HOLDING_DELTAS = {
        { +8,  +5,  -8, -12},  // H_GROWTH
        { +3,  +8,  -5, -10},  // H_CYCLICAL
        { +8,  +3,  -5,  -8},  // H_FINANCIAL
        { -8,  -5,  +8, +10},  // H_DEFENSIVE
        { -5,  +3,   0,  -5},  // H_COMMODITY
        {  0,  -5,  -5, +12},  // H_BOND
        { +3,  -3,  +5,  +8},  // H_GOLD
        { -8,  -5,  +8,  +5},  // H_CASH
    };

    private static final String[][] HOLDING_REASONS = {
        {"금리 인하→성장주 밸류에이션 확장, 비중 확대",   "실적 모멘텀 지속, 현 비중 유지/소폭 확대",  "금리 상승→할인율 압박, 비중 축소",       "경기 침체→수요 급감, 비중 대폭 축소"},
        {"경기 회복 초입 선제 편입",                     "경기 확장→실적 개선 본격화, 비중 확대",      "경기 둔화→비중 축소",                    "경기 침체→실적 악화, 비중 대폭 축소"},
        {"금리 인하→대출 수요·건전성 개선, 비중 확대",    "실적 확장 수혜, 비중 유지",                 "금리 상승 NIM 혼조, 비중 축소",          "부실채권 리스크 확대, 비중 축소"},
        {"성장주 선호 구간→방어주 비중 축소",              "경기 확장→저성장 업종 언더퍼폼, 비중 축소", "경기 둔화→안정 현금흐름 선호, 비중 확대", "침체기→필수 수요 방어, 비중 확대"},
        {"수요 확대 전 단계, 비중 축소",                  "경기 확장→원자재 수요 증가, 비중 확대",      "수요·공급 혼조, 비중 유지",               "수요 급감(금 제외), 비중 축소"},
        {"금리 인하 기대→장기채 보유 유지",               "금리 안정→이자 수익 중심, 소폭 축소",        "금리 상승→채권 가격 하락, 비중 축소",     "침체→금리 인하 기대, 안전자산 비중 확대"},
        {"달러 약세+지정학 헤지, 비중 소폭 확대",          "위험자산 선호 구간, 비중 소폭 축소",         "인플레이션 헤지 매력, 비중 확대",          "침체+불확실성→금 수요 급증, 비중 확대"},
        {"저금리 구간→현금 기회비용 증가, 비중 축소",      "저금리 지속→현금 비중 최소화",               "금리 상승→단기금리 매력 증가, 비중 확대",  "고금리+침체 대비 현금 확보, 비중 확대"},
    };

    // {종목명, 코드, 분류, sectorType, 현재비중%}
    private static final Object[][] PENSION_DEFS = {
        {"TIGER 미국S&P500",    "360750", "미국 대형주", H_GROWTH,   30},
        {"KODEX 국고채10년",    "148070", "장기채권",    H_BOND,     25},
        {"KODEX 200",           "069500", "국내 대형주", H_CYCLICAL, 20},
        {"TIGER CD금리투자KIS", "357870", "단기금리",    H_CASH,     15},
        {"KODEX 골드선물(H)",   "132030", "금",          H_GOLD,     10},
    };

    private static final Object[][] ISA_DEFS = {
        {"삼성전자",            "005930", "반도체/AI",   H_GROWTH,    20},
        {"SK하이닉스",          "000660", "반도체/AI",   H_GROWTH,    15},
        {"KB금융",              "105560", "금융/은행",   H_FINANCIAL, 10},
        {"KT&G",                "033780", "필수소비재",  H_DEFENSIVE, 15},
        {"한국전력",            "015760", "유틸리티",    H_DEFENSIVE, 15},
        {"TIGER 미국S&P500",    "360750", "미국 대형주", H_GROWTH,    25},
    };

    private static final Object[][] GENERAL_DEFS = {
        {"삼성전자",            "005930", "반도체/AI",    H_GROWTH,    25},
        {"SK하이닉스",          "000660", "반도체/AI",    H_GROWTH,    20},
        {"카카오",              "035720", "인터넷/플랫폼", H_GROWTH,   15},
        {"현대차",              "005380", "자동차",       H_CYCLICAL,  15},
        {"POSCO홀딩스",         "005490", "철강/소재",    H_COMMODITY, 15},
        {"KODEX 골드선물(H)",   "132030", "금",           H_GOLD,      10},
    };

    private List<AccountRebalance> buildAccountRebalances(CyclePhase phase) {
        return Arrays.asList(
            buildAccount("연금저축펀드", "연금저축", PENSION_DEFS, phase),
            buildAccount("ISA 계좌",    "ISA",      ISA_DEFS,     phase),
            buildAccount("일반 계좌",   "일반",     GENERAL_DEFS, phase)
        );
    }

    private AccountRebalance buildAccount(String name, String type, Object[][] defs, CyclePhase phase) {
        int pi = phase.ordinal();
        List<HoldingItem> holdings = new java.util.ArrayList<>();
        for (Object[] d : defs) {
            int sType  = (int) d[3];
            int cur    = (int) d[4];
            int delta  = HOLDING_DELTAS[sType][pi];
            int tgt    = clamp(cur + delta, 5, 60);
            int diff   = tgt - cur;
            String action  = diff >= 5 ? "BUY" : diff <= -5 ? "SELL" : "HOLD";
            String diffStr = diff > 0 ? "+" + diff + "%" : diff < 0 ? diff + "%" : "±0%";
            holdings.add(HoldingItem.builder()
                .name((String) d[0]).code((String) d[1]).category((String) d[2])
                .currentWeight(cur).targetWeight(tgt)
                .weightDiffStr(diffStr).action(action)
                .reason(HOLDING_REASONS[sType][pi])
                .build());
        }
        return AccountRebalance.builder()
            .accountName(name).accountType(type)
            .holdings(holdings)
            .rebalanceSummary(accountSummary(type, phase))
            .build();
    }

    private String accountSummary(String type, CyclePhase phase) {
        if ("연금저축".equals(type)) {
            if (phase == CyclePhase.FINANCIAL)         return "금융장세: S&P500·국내대형주 비중 확대, CD금리 축소로 장기 수익성 개선 권장";
            if (phase == CyclePhase.EARNINGS)          return "실적장세: 주식 비중 극대화, 채권·현금 최소화하여 성장 모멘텀 극대화";
            if (phase == CyclePhase.REVERSE_FINANCIAL) return "역금융장세: 단기금리·금 비중 확대, 주식 비중 단계적 축소 권장";
            return "역실적장세: 장기채·금 중심 안전자산 비중 확대, 주식 전반 비중 축소";
        }
        if ("ISA".equals(type)) {
            if (phase == CyclePhase.FINANCIAL)         return "금융장세: 반도체·금융주 비중 확대, 방어주(KT&G·한전) 비중 단계적 축소";
            if (phase == CyclePhase.EARNINGS)          return "실적장세: 경기민감 업종 추가 편입, 해외 성장 ETF 비중 유지";
            if (phase == CyclePhase.REVERSE_FINANCIAL) return "역금융장세: 방어 종목 비중 확대, 성장주·기술주 비중 축소";
            return "역실적장세: 주식 비중 대폭 축소, 안전자산·채권 ETF 편입 검토";
        }
        // 일반
        if (phase == CyclePhase.FINANCIAL)         return "금융장세: 반도체·AI 집중 편입, 철강·플랫폼 비중 최적화, 금 헤지 유지";
        if (phase == CyclePhase.EARNINGS)          return "실적장세: 자동차·경기소비재 비중 확대, 성장주 차익실현 후 재편입";
        if (phase == CyclePhase.REVERSE_FINANCIAL) return "역금융장세: 성장주 비중 대폭 축소, 방어주·금 편입으로 리스크 감소";
        return "역실적장세: 전 종목 비중 축소, 현금화 우선, 시장 바닥 확인 후 재진입";
    }
}

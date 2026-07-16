package com.kangong.retire.service;

import com.kangong.retire.dto.RealEstateItem;
import com.kangong.retire.dto.RetireInputDto;
import com.kangong.retire.dto.RetireYearResult;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
public class RetireSimulationService {

    private static final int TARGET_AGE = 100;
    private static final int CURRENT_YEAR = Year.now().getValue();
    private static final double HEALTH_INS_RATE = 0.0709;
    private static final double LONG_TERM_CARE_RATE = 0.1295;
    private static final long DEPENDENT_INCOME_LIMIT = 2000;
    private static final long DEPENDENT_PROPERTY_LIMIT = 54000;

    static class Assets {
        double deposit, stocks, pensionSaving, irp, isa;
        double depositCost, stocksCost;

        double total() {
            return v(deposit) + v(stocks) + v(pensionSaving) + v(irp) + v(isa);
        }

        void applyReturn(double rate) {
            deposit = v(deposit) * rate;
            stocks = v(stocks) * rate;
            pensionSaving = v(pensionSaving) * rate;
            irp = v(irp) * rate;
            isa = v(isa) * rate;
        }

        private double v(double x) { return Math.max(0, x); }
    }

    static class Property {
        double value, cost, officialPrice, growthRate;
        int saleYear;
        String owner;
        boolean sold;
    }

    public List<RetireYearResult> simulate(RetireInputDto input) {
        List<RetireYearResult> results = new ArrayList<>();

        Assets self = new Assets();
        self.deposit = input.getDepositSelf() + input.getInsuranceSelf() - input.getLoanSelf();
        self.stocks = input.getStocksSelf();
        self.pensionSaving = input.getPensionSavingSelf();
        self.irp = input.getIrpSelf();
        self.isa = input.getIsaSelf();
        self.depositCost = Math.max(0, self.deposit);
        self.stocksCost = self.stocks;

        Assets spouse = new Assets();
        spouse.deposit = input.getDepositSpouse() + input.getInsuranceSpouse() - input.getLoanSpouse();
        spouse.stocks = input.getStocksSpouse();
        spouse.pensionSaving = input.getPensionSavingSpouse();
        spouse.irp = input.getIrpSpouse();
        spouse.isa = input.getIsaSpouse();
        spouse.depositCost = Math.max(0, spouse.deposit);
        spouse.stocksCost = spouse.stocks;

        List<Property> properties = buildProperties(input);

        int legalPensionAge = input.getPensionStartAge() > 0 ? input.getPensionStartAge() : 65;
        int retireAge = input.getRetireYear() - input.getBirthYear();
        int pensionStartAge = Math.max(legalPensionAge, retireAge);
        int endYear = input.getBirthYear() + TARGET_AGE;

        for (int year = CURRENT_YEAR; year <= endYear; year++) {
            int age = year - input.getBirthYear();
            int spouseAge = input.getSpouseBirthYear() > 0
                    ? year - input.getSpouseBirthYear() : 0;

            // 0. 부동산 매도
            double saleProceeds = 0;
            double saleTax = 0;
            int activeProps = (int) properties.stream().filter(p -> !p.sold).count();
            for (Property p : properties) {
                if (!p.sold && p.saleYear > 0 && year == p.saleYear) {
                    double gain = Math.max(0, p.value - p.cost);
                    double taxRate = getRealEstateTaxRate(activeProps);
                    double tax = gain * taxRate;
                    double net = p.value - tax;
                    saleTax += tax;
                    saleProceeds += net;

                    if ("SPOUSE".equals(p.owner)) {
                        spouse.pensionSaving += net;
                    } else if ("JOINT".equals(p.owner)) {
                        self.pensionSaving += net / 2;
                        spouse.pensionSaving += net / 2;
                    } else {
                        self.pensionSaving += net;
                    }
                    p.sold = true;
                    activeProps--;
                }
            }

            for (Property p : properties) {
                if (!p.sold) {
                    double gr = 1 + p.growthRate / 100;
                    p.value *= gr;
                    p.officialPrice *= gr;
                }
            }

            // 1. 연금
            long selfPensionAmt = age >= pensionStartAge ? input.getNationalPension() * 12 : 0;
            long spousePensionAmt = (spouseAge >= pensionStartAge
                    && input.getSpouseNationalPension() > 0)
                    ? input.getSpouseNationalPension() * 12 : 0;
            long pensionIncome = selfPensionAmt + spousePensionAmt;

            // 2. 월적립
            long investmentIncome = year <= input.getRetireYear()
                    ? input.getMonthlyInvestment() * 12 : 0;

            // 3. 생활비
            int yearsFromNow = year - CURRENT_YEAR;
            double livingExpense = 0;
            if (year > input.getRetireYear()) {
                livingExpense = input.getMonthlyExpense() * 12
                        * Math.pow(1 + input.getInflationRate() / 100, yearsFromNow);
            }

            // 4. 건강보험 (세대 합산, 공시지가 기준)
            double selfTotal = self.total();
            double spouseTotal = spouse.total();
            double reTotal = properties.stream()
                    .filter(p -> !p.sold).mapToDouble(p -> p.value).sum();
            double reOfficialTotal = properties.stream()
                    .filter(p -> !p.sold).mapToDouble(p -> p.officialPrice).sum();
            double healthIns = 0;
            if (year > input.getRetireYear()) {
                healthIns = calculateHealthInsurance(
                        pensionIncome, selfTotal + spouseTotal, reOfficialTotal);
            }

            // 5. 연금소득세 (개인별)
            double pensionTax = calculatePensionTax(selfPensionAmt, age)
                    + calculatePensionTax(spousePensionAmt, spouseAge);

            // 6. 부족분
            double totalExpense = livingExpense + healthIns;
            double netIncome = pensionIncome - pensionTax + investmentIncome;
            double shortfall = totalExpense - netIncome;

            // 7. 인출 또는 적립
            StringBuilder source = new StringBuilder();
            double[] acc = {0, 0}; // [0]=tax, [1]=gross

            if (shortfall > 0) {
                double rem = shortfall;
                rem = withdrawType(self, spouse, "stocks", age, rem, source, acc);
                rem = withdrawType(self, spouse, "deposit", age, rem, source, acc);
                rem = withdrawType(self, spouse, "isa", age, rem, source, acc);
                rem = withdrawType(self, spouse, "pensionSaving", age, rem, source, acc);
                rem = withdrawType(self, spouse, "irp", age, rem, source, acc);
            } else if (shortfall < 0) {
                distributeSurplus(self, Math.abs(shortfall));
            }

            double withdrawalTax = acc[0];
            double totalWithdrawal = acc[1];
            double totalTax = pensionTax + withdrawalTax + saleTax;

            // 8. 수익률
            double returnRate = 1 + input.getAnnualReturn() / 100;
            self.applyReturn(returnRate);
            spouse.applyReturn(returnRate);

            selfTotal = self.total();
            spouseTotal = spouse.total();
            reTotal = properties.stream()
                    .filter(p -> !p.sold).mapToDouble(p -> p.value).sum();

            results.add(RetireYearResult.builder()
                    .year(year).age(age).spouseAge(spouseAge)
                    .deposit(Math.round(self.deposit + spouse.deposit))
                    .stocks(Math.round(self.stocks + spouse.stocks))
                    .pensionSaving(Math.round(self.pensionSaving + spouse.pensionSaving))
                    .irp(Math.round(self.irp + spouse.irp))
                    .isa(Math.round(self.isa + spouse.isa))
                    .selfFinancialTotal(Math.round(selfTotal))
                    .spouseFinancialTotal(Math.round(spouseTotal))
                    .financialTotal(Math.round(selfTotal + spouseTotal))
                    .realEstate(Math.round(reTotal))
                    .totalAssets(Math.round(selfTotal + spouseTotal + reTotal))
                    .pensionIncome(pensionIncome)
                    .investmentIncome(investmentIncome)
                    .livingExpense(Math.round(livingExpense))
                    .tax(Math.round(totalTax))
                    .healthInsurance(Math.round(healthIns))
                    .totalExpense(Math.round(livingExpense + healthIns + totalTax))
                    .withdrawal(Math.round(totalWithdrawal))
                    .withdrawalSource(source.toString())
                    .realEstateSaleProceeds(Math.round(saleProceeds))
                    .depleted(selfTotal + spouseTotal + reTotal <= 0)
                    .build());
        }
        return results;
    }

    private List<Property> buildProperties(RetireInputDto input) {
        List<Property> list = new ArrayList<>();
        if (input.getRealEstateList() == null) return list;
        for (RealEstateItem item : input.getRealEstateList()) {
            if (item == null || item.getValue() <= 0) continue;
            Property p = new Property();
            p.value = item.getValue();
            p.cost = item.getValue();
            p.officialPrice = item.getOfficialPrice() > 0 ? item.getOfficialPrice() : item.getValue() * 0.6;
            p.growthRate = item.getGrowthRate() > 0 ? item.getGrowthRate() : 2.0;
            p.saleYear = item.getSaleYear();
            p.owner = item.getOwner() != null ? item.getOwner() : "SELF";
            list.add(p);
        }
        return list;
    }

    private double withdrawType(Assets self, Assets spouse, String type,
                                int age, double rem, StringBuilder src, double[] acc) {
        if (rem <= 0) return 0;
        rem = withdrawSingle(self, type, age, rem, src, acc, "");
        if (rem > 0) {
            rem = withdrawSingle(spouse, type, age, rem, src, acc, "(배)");
        }
        return rem;
    }

    private double withdrawSingle(Assets a, String type, int age,
                                  double rem, StringBuilder src, double[] acc, String suffix) {
        if (rem <= 0) return 0;

        double balance;
        double taxRate;
        String label;

        switch (type) {
            case "stocks":
                balance = a.stocks;
                if (balance <= 0) return rem;
                taxRate = gainTaxRate(a.stocks, a.stocksCost);
                label = "주식";
                break;
            case "deposit":
                balance = a.deposit;
                if (balance <= 0) return rem;
                taxRate = gainTaxRate(a.deposit, a.depositCost);
                label = "예금";
                break;
            case "isa":
                balance = a.isa;
                if (balance <= 0) return rem;
                taxRate = 0;
                label = "ISA";
                break;
            case "pensionSaving":
                balance = a.pensionSaving;
                if (balance <= 0) return rem;
                taxRate = getPensionWithdrawTaxRate(age);
                label = "연금저축";
                break;
            case "irp":
                balance = a.irp;
                if (balance <= 0) return rem;
                taxRate = getPensionWithdrawTaxRate(age);
                label = "IRP";
                break;
            default:
                return rem;
        }

        double denom = Math.max(1 - taxRate, 0.5);
        double gross = Math.min(rem / denom, balance);
        double tax = gross * taxRate;
        double net = gross - tax;

        switch (type) {
            case "stocks":
                double sr = a.stocks > 0 ? a.stocksCost / a.stocks : 1;
                a.stocksCost = Math.max(0, a.stocksCost - gross * sr);
                a.stocks -= gross;
                break;
            case "deposit":
                double dr = a.deposit > 0 ? a.depositCost / a.deposit : 1;
                a.depositCost = Math.max(0, a.depositCost - gross * dr);
                a.deposit -= gross;
                break;
            case "isa": a.isa -= gross; break;
            case "pensionSaving": a.pensionSaving -= gross; break;
            case "irp": a.irp -= gross; break;
        }

        acc[0] += tax;
        acc[1] += gross;
        appendSource(src, label + suffix);
        return rem - net;
    }

    private double gainTaxRate(double value, double cost) {
        if (value <= 0 || value <= cost) return 0;
        return ((value - cost) / value) * 0.154;
    }

    private void distributeSurplus(Assets a, double surplus) {
        double p1 = Math.min(60 * 12, surplus); surplus -= p1; a.pensionSaving += p1;
        double ir = Math.min(30 * 12, surplus); surplus -= ir; a.irp += ir;
        double is = Math.min(165 * 12, surplus); surplus -= is; a.isa += is;
        double p2 = Math.min(90 * 12, surplus); surplus -= p2; a.pensionSaving += p2;
        double half = surplus / 2;
        a.stocks += half; a.stocksCost += half;
        a.deposit += (surplus - half); a.depositCost += (surplus - half);
    }

    private double getPensionWithdrawTaxRate(int age) {
        if (age < 55) return 0.165;  // 만 55세 미만 중도인출: 기타소득세 16.5%
        if (age >= 80) return 0.033;
        if (age >= 70) return 0.044;
        return 0.055;
    }

    private double calculatePensionTax(long annualPension, int age) {
        if (annualPension <= 0 || age <= 0) return 0;
        double rate = age >= 80 ? 0.03 : (age >= 70 ? 0.04 : 0.05);
        return annualPension * rate;
    }

    private double calculateHealthInsurance(long pensionIncome,
                                            double financialAssets, double realEstate) {
        double financialIncome = financialAssets * 0.04;
        double totalIncome = pensionIncome + financialIncome;
        double propTaxBase = realEstate * 0.6;

        if (totalIncome <= DEPENDENT_INCOME_LIMIT
                && propTaxBase <= DEPENDENT_PROPERTY_LIMIT) {
            return 0;
        }

        double incIns = totalIncome * HEALTH_INS_RATE;
        double propIns = propTaxBase * 0.003;
        return (incIns + propIns) * (1 + LONG_TERM_CARE_RATE);
    }

    private double getRealEstateTaxRate(int numProperties) {
        if (numProperties <= 1) return 0.06;
        if (numProperties == 2) return 0.20;
        return 0.30;
    }

    private void appendSource(StringBuilder sb, String name) {
        if (sb.length() > 0) sb.append(" → ");
        sb.append(name);
    }
}

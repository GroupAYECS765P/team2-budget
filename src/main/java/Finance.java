import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.DAYS;

public class Finance {

    private IBudgetRepo repo;

    public Finance(IBudgetRepo repo) {
        this.repo = repo;
    }

    public double queryBudget(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            return 0;
        }
        double amount = 0;

        DateTimeFormatter formatter = ofPattern("yyyyMM");
        String formattedStart = start.format(formatter);
        String formattedEnd = end.format(formatter);

        if (formattedStart.equals(formattedEnd)) {
            long days = start.until(end, DAYS) + 1;
            amount = overlappingAmount(days, formattedStart);
        } else {
            LocalDate currentDate = LocalDate.of(start.getYear(), start.getMonthValue(), 1);
            while (currentDate.isBefore(end.withDayOfMonth(1).plusMonths(1))) {
                double result = 0;
                for (Budget budget : repo.getAll()) {
                    if (currentDate.format(formatter).equals(budget.yearMonth)) {
                        long overlappingDays = getOverlappingDays(start, end, budget);
                        result = budget.dailyAmount() * overlappingDays;
                        break;
                    }
                }
                amount += result;
                currentDate = currentDate.plusMonths(1);
            }
        }

        return amount;
    }

    private long getOverlappingDays(LocalDate start, LocalDate end, Budget budget) {
        long overlappingDays;
        LocalDate overlappingStart;
        LocalDate overlappingEnd;
        if (budget.yearMonth.equals(start.format(ofPattern("yyyyMM")))) {
            overlappingStart = start;
            overlappingEnd = budget.lastDay();
//            overlappingDays = DAYS.between(overlappingStart, overlappingEnd) + 1;
        } else if (budget.yearMonth.equals(end.format(ofPattern("yyyyMM")))) {
            overlappingStart = budget.firstDay();
            overlappingEnd = end;
//            overlappingDays = DAYS.between(overlappingStart, overlappingEnd) + 1;
        } else {
            overlappingStart = budget.firstDay();
            overlappingEnd = budget.lastDay();
//            overlappingDays = DAYS.between(overlappingStart, overlappingEnd) + 1;
        }
        overlappingDays = DAYS.between(overlappingStart, overlappingEnd) + 1;
        return overlappingDays;
    }

    private double overlappingAmount(long days, String formattedDate) {
        for (Budget budget : repo.getAll()) {
            if (formattedDate.equals(budget.yearMonth)) {
                return budget.dailyAmount() * days;
            }
        }
        return 0;
    }
}

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
            Period period = new Period(start, end);
//            while (currentDate.isBefore(end.withDayOfMonth(1).plusMonths(1))) {
            for (Budget budget : repo.getAll()) {
                amount += budget.overlappingAmount(period);
//                    if (currentDate.format(formatter).equals(budget.yearMonth)) {
//                        amount += budget.overlappingAmount(period);
//                        break;
//                    }
            }
            currentDate = currentDate.plusMonths(1);
//            }
        }

        return amount;
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

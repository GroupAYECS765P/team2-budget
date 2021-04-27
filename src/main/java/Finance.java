import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String formattedStart = start.format(formatter);
        String formattedEnd = end.format(formatter);

        if (formattedStart.equals(formattedEnd)) {
            long days = start.until(end, ChronoUnit.DAYS) + 1;
            amount = overlappingAmount(days, formattedStart);
        } else {
            LocalDate currentDate = LocalDate.of(start.getYear(), start.getMonthValue(), 1);
            while (currentDate.isBefore(end.withDayOfMonth(1).plusMonths(1))) {
                double result = 0;
                for (Budget budget : repo.getAll()) {
                    if (currentDate.format(formatter).equals(budget.yearMonth)) {
                        int overlappingDays;
                        if (currentDate.format(formatter).equals(formattedStart)) {
                            overlappingDays = start.lengthOfMonth() - start.getDayOfMonth() + 1;
                        } else if (currentDate.format(formatter).equals(formattedEnd)) {
                            overlappingDays = end.getDayOfMonth();
                        } else {
                            overlappingDays = currentDate.lengthOfMonth();
                        }
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

    private double overlappingAmount(long days, String formattedDate) {
        for (Budget budget : repo.getAll()) {
            if (formattedDate.equals(budget.yearMonth)) {
                return budget.dailyAmount() * days;
            }
        }
        return 0;
    }
}

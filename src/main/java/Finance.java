import java.time.LocalDate;

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

        Period period = new Period(start, end);
        for (Budget budget : repo.getAll()) {
            amount += budget.overlappingAmount(period);
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

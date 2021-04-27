import java.time.LocalDate;

public class Finance {

    private final IBudgetRepo repo;

    public Finance(IBudgetRepo repo) {
        this.repo = repo;
    }

    public double queryBudget(LocalDate start, LocalDate end) {

        Period period = new Period(start, end);

        return repo.getAll().stream()
                .mapToDouble(budget -> budget.overlappingAmount(period))
                .sum();
    }
}

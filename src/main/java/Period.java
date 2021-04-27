import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.DAYS;

public class Period {
    private final LocalDate start;
    private final LocalDate end;

    public Period(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    long getOverlappingDays(Budget budget) {
        LocalDate overlappingStart = start.isAfter(budget.firstDay()) ? start : budget.firstDay();
        LocalDate overlappingEnd = end.isBefore(budget.lastDay()) ? end : budget.lastDay();
        return DAYS.between(overlappingStart, overlappingEnd) + 1;
    }
}

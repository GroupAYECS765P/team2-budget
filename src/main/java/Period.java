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
        Period another = new Period(budget.firstDay(), budget.lastDay());
        LocalDate firstDay = another.start;
        LocalDate lastDay = another.end;
        LocalDate overlappingStart = start.isAfter(firstDay) ? start : firstDay;
        LocalDate overlappingEnd = end.isBefore(lastDay) ? end : lastDay;
        return DAYS.between(overlappingStart, overlappingEnd) + 1;
    }
}

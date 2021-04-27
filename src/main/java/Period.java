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

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    long getOverlappingDays(Budget budget) {
        LocalDate overlappingStart;
        LocalDate overlappingEnd;
        if (budget.yearMonth.equals(getStart().format(ofPattern("yyyyMM")))) {
            overlappingStart = getStart();
            overlappingEnd = budget.lastDay();
        } else if (budget.yearMonth.equals(getEnd().format(ofPattern("yyyyMM")))) {
            overlappingStart = budget.firstDay();
            overlappingEnd = getEnd();
        } else {
            overlappingStart = budget.firstDay();
            overlappingEnd = budget.lastDay();
        }
        return DAYS.between(overlappingStart, overlappingEnd) + 1;
    }
}

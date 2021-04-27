import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class Budget {
    String yearMonth;
    Integer amount;

    public Budget(String yearMonth, Integer amount) {
        this.yearMonth = yearMonth;
        this.amount = amount;
    }

    public double overlappingAmount(Period period) {
        return dailyAmount() * period.getOverlappingDays(createPeriod());
    }

    private int days() {
        return getMonth().lengthOfMonth();
    }

    private LocalDate lastDay() {
        return getMonth().atEndOfMonth();
    }

    private LocalDate firstDay() {
        return getMonth().atDay(1);
    }

    private double dailyAmount() {
        return (double) amount / days();
    }

    private YearMonth getMonth() {
        return YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyyMM"));
    }

    private Period createPeriod() {
        return new Period(firstDay(), lastDay());
    }
}

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class Budget {
    String yearMonth;
    Integer amount;

    public Budget(String yearMonth, Integer amount) {
        this.yearMonth = yearMonth;
        this.amount = amount;
    }

    public int days() {
        YearMonth myYearMonth = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyyMM"));
        return myYearMonth.lengthOfMonth();
    }
}

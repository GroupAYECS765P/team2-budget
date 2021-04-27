import org.mockito.InjectMocks;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Finance {

	private IBudgetRepo repo;

	public Finance(IBudgetRepo repo) {
		this.repo = repo;
	}

	public double queryBudget(LocalDate start, LocalDate end) {
		if(end.isBefore(start)){
			return 0;
		}
		List<Budget> data = repo.getAll();
		double amount = 0;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
		String formattedStart = start.format(formatter);
		String formattedEnd = end.format(formatter);

		if(formattedStart.equals(formattedEnd)){
			long days = start.until(end, ChronoUnit.DAYS) + 1;
			amount = notFunnAmount(days, start.lengthOfMonth(),data,formattedStart);
		} else{
			LocalDate temp =  LocalDate.of(start.getYear(), start.getMonthValue(), 1); // 20211001
			while (true) {
				String tempFormat = temp.format(formatter);
				if(tempFormat.equals(formattedStart)) {
					int days = start.lengthOfMonth() - start.getDayOfMonth() +1;
					amount += notFunnAmount(days, start.lengthOfMonth(),data, tempFormat);
					temp = temp.plusMonths(1);
				} else if(tempFormat.equals(formattedEnd)){
					int days = end.getDayOfMonth();
					amount += notFunnAmount(days, end.lengthOfMonth(),data, tempFormat);
					break;
				}else {
					int days = temp.lengthOfMonth();
					amount += notFunnAmount(days, temp.lengthOfMonth(),data, tempFormat);
					temp = temp.plusMonths(1);
				}
			}
		}

		return amount;

	}

	private double notFunnAmount(long days,  int lengthOfMonth, List<Budget> data, String formattedDate) {
		for(Budget budget: data) {
			if(formattedDate.equals(budget.yearMonth)) {
				return  budget.amount/lengthOfMonth * days;
			}
		}
		return 0;
	}
}

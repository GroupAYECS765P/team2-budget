import org.mockito.InjectMocks;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Finance {

//	@InjectMocks
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
					continue;
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



//			long month = start.until(end, ChronoUnit.MONTHS);
//			long between = ChronoUnit.MONTHS.between(start, end);
//			if(between == 1){
//
//				long start_days = start.lengthOfMonth() - start.getDayOfMonth() +1;
//				long end_days = end.getDayOfMonth();
//
//				amount += notFunnAmount(start_days, start.lengthOfMonth(), data, formattedStart);
//				amount += notFunnAmount(end_days , end.lengthOfMonth(), data,formattedEnd);
//			} else{
//
//			}
		}

//		data.stream().filter()
return amount;

//		return 0;
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

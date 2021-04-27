import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


public class BudgetTest {
	@Test
	public void test3(){
		Finance finance = new Finance();
		LocalDate start = LocalDate.of();
		LocalDate end = LocalDate.of();
		Assertions.assertEquals(0  ,finance.queryBudget(start, end));

	}
}

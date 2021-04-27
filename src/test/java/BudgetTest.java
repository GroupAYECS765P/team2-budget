import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

public class BudgetTest {

    private Finance finance;
    private IBudgetRepo repo;

    @Test
    public void fullMonth() {
        when(repo.getAll()).thenReturn(asList(new Budget("202110", 31)));

        LocalDate start = LocalDate.of(2021, 10, 1);
        LocalDate end = LocalDate.of(2021, 10, 31);

        Assertions.assertEquals(31, finance.queryBudget(start, end));
    }

    @Test
    public void notfullMonth() {
        when(repo.getAll()).thenReturn(asList(new Budget("202110", 31)));

        LocalDate start = LocalDate.of(2021, 10, 3);
        LocalDate end = LocalDate.of(2021, 10, 10);

        Assertions.assertEquals(8, finance.queryBudget(start, end));
    }

    @Test
    public void twoMonth() {
        when(repo.getAll()).thenReturn(asList(
                new Budget("202110", 31),
                new Budget("202111", 30)
        ));

        LocalDate start = LocalDate.of(2021, 10, 30);
        LocalDate end = LocalDate.of(2021, 11, 10);

        Assertions.assertEquals(12, finance.queryBudget(start, end));
    }

    @Test
    public void twoMonth2() {
        when(repo.getAll()).thenReturn(asList(
                new Budget("202111", 30),
                new Budget("202112", 31)
        ));

        LocalDate start = LocalDate.of(2021, 10, 30);
        LocalDate end = LocalDate.of(2021, 12, 10);

        Assertions.assertEquals(40, finance.queryBudget(start, end));
    }

    @Test
    public void year2() {
        when(repo.getAll()).thenReturn(asList(
                new Budget("202112", 31),
                new Budget("202202", 28)
        ));

        LocalDate start = LocalDate.of(2021, 12, 30);
        LocalDate end = LocalDate.of(2022, 2, 3);

        Assertions.assertEquals(5, finance.queryBudget(start, end));
    }

    @Test
    public void dateError() {
        when(repo.getAll()).thenReturn(asList(
                new Budget("202112", 31),
                new Budget("202202", 28)
        ));

        LocalDate start = LocalDate.of(2021, 12, 30);
        LocalDate end = LocalDate.of(2021, 11, 3);

        Assertions.assertEquals(0, finance.queryBudget(start, end));
    }

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(IBudgetRepo.class);
        finance = new Finance(repo);
    }
}

package br.finax.services;

import br.finax.dto.InterfacesSQL.HomeAccountsList;
import br.finax.dto.InterfacesSQL.HomeRevenueExpense;
import br.finax.dto.InterfacesSQL.HomeUpcomingReleases;
import br.finax.dto.SpendByCategory;
import br.finax.models.Category;
import br.finax.models.Release;
import br.finax.utils.UtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final ReleaseService releaseService;
    private final CategoryService categoryService;
    private final AccountService accountService;

    private final UtilsService utils;

    @Transactional(readOnly = true)
    public HomeRevenueExpense getRevenueExpense() {
        return releaseService.getHomeBalances(
                utils.getAuthUser().getId(),
                getFirstDayOfMonth(),
                getLastDayOfMonth()
        );
    }

    @Transactional(readOnly = true)
    public List<HomeAccountsList> getAccountsList() {
        return accountService.getHomeAccountsList(
                utils.getAuthUser().getId()
        );
    }

    @Transactional(readOnly = true)
    public List<HomeUpcomingReleases> getUpcomingReleases() {
        final long userId = utils.getAuthUser().getId();

        return releaseService.getUpcomingReleases(userId);
    }

    @Transactional(readOnly = true)
    public List<SpendByCategory> getSpendsByCategory() {
        final List<Release> expenses = releaseService.findReleasesForHomeSpendsCategory(
                utils.getAuthUser().getId(),
                getFirstDayOfMonth(),
                getLastDayOfMonth()
        );

        final Map<Long, Category> categoryMap = new HashMap<>();
        final List<Long> categoryIds = expenses.stream().map(Release::getCategoryId).toList();
        final List<Category> categories = categoryService.findByIdIn(categoryIds);
        categories.forEach(category -> categoryMap.put(category.getId(), category));

        final BigDecimal totalExpense = expenses.stream()
                .map(Release::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final Map<Long, BigDecimal> categoryExpenseMap = new HashMap<>();
        expenses.forEach(expense -> {
            final BigDecimal categoryExpense = categoryExpenseMap.getOrDefault(expense.getCategoryId(), BigDecimal.ZERO)
                    .add(expense.getAmount());
            categoryExpenseMap.put(expense.getCategoryId(), categoryExpense);
        });

        final List<SpendByCategory> spendByCategories = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : categoryExpenseMap.entrySet()) {
            final Long categoryId = entry.getKey();
            final BigDecimal categoryExpense = entry.getValue();
            final Category category = categoryMap.get(categoryId);

            final double percent = Double.parseDouble(
                    String.valueOf(
                            categoryExpense.divide(totalExpense, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100))
                    )
            );
            spendByCategories.add(new SpendByCategory(category, percent, categoryExpense));
        }

        spendByCategories.sort(Comparator.comparing(SpendByCategory::value).reversed());

        return spendByCategories;
    }

    private LocalDate getFirstDayOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    private LocalDate getLastDayOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }
}

package br.finax;

import br.finax.models.Category;
import br.finax.repository.CategoryRepository;
import br.finax.security.SecurityFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Schedule {

    private final CategoryRepository categoryRepository;
    private final SecurityFilter securityFilter;

    @PersistenceContext
    private final EntityManager entityManager;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    // Method for not leaving the machine idle
    @Scheduled(cron = "0 * * * * *") //every minute
    public void stayActive() {
        List<Category> categories = categoryRepository.findAll();

        for (var j = 0; j < categories.size(); j++) {
            categories.remove(categories.get(j));
        }
    }

    @Transactional
    @Scheduled(cron = "0 30 3 * * *") //every day at 3:30AM
    public void optimizeDatabase() {
        final String dbName = databaseUrl.split("//")[1].split("/")[1];

        entityManager.createNativeQuery("commit; vacuum full analyze; commit;").executeUpdate();
        entityManager.createNativeQuery("commit; reindex database " + dbName + "; commit;").executeUpdate();
        System.out.println("Database optimized: " + LocalDateTime.now().format(dateFormat));
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void clearUsersCache() { //every day at 4:00AM
        securityFilter.usersCache.clear();
        System.out.println("Cleared user cache in security filter: " + LocalDateTime.now().format(dateFormat));
    }

//    @Scheduled(cron = "0 0 3 * * *") //every day at 3:00 AM
//    public void calculateYields() {
//         logic to analyze all investments and calculate returns based on liquidity and profitability
//    }
}

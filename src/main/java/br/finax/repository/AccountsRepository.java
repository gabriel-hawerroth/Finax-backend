package br.finax.repository;

import br.finax.dto.InterfacesSQL.AccountBasicList;
import br.finax.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountsRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByUserIdOrderByIdAsc(long userId);

    @Query(value = """
            SELECT
                *
            FROM
                bank_account ba
            WHERE
                ba.user_id = :userId
                AND ba.active = true
                AND ba.add_overall_balance = true
            ORDER BY
                ba.id
            """, nativeQuery = true)
    List<Account> getHomeAccountsList(long userId);

    @Query(value = """
            SELECT
                ba.id,
                ba.name,
                ba.image,
                ba.balance
            FROM
                bank_account ba
            WHERE
                ba.user_id = :userId
                AND ba.active = true
            ORDER BY
                ba.id
            """, nativeQuery = true)
    List<AccountBasicList> getBasicList(long userId);
}

package br.finax.services;

import br.finax.dto.InterfacesSQL.AccountBasicList;
import br.finax.exceptions.NotFoundException;
import br.finax.exceptions.WithoutPermissionException;
import br.finax.models.Account;
import br.finax.models.CashFlow;
import br.finax.repository.AccountsRepository;
import br.finax.utils.UtilsService;
import lombok.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AccountService {

    private final AccountsRepository accountsRepository;

    private final CashFlowService cashFlowService;
    private final UtilsService utils;

    @Lazy
    public AccountService(AccountsRepository accountsRepository, CashFlowService cashFlowService, UtilsService utils) {
        this.accountsRepository = accountsRepository;
        this.cashFlowService = cashFlowService;
        this.utils = utils;
    }

    @Transactional(readOnly = true)
    public Account findById(long id) {
        return accountsRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Account> getByUser() {
        return accountsRepository.findAllByUserIdOrderByIdAsc(utils.getAuthUser().getId());
    }

    @Transactional(readOnly = true)
    public List<AccountBasicList> getBasicList() {
        return accountsRepository.getBasicList(utils.getAuthUser().getId());
    }

    @Transactional
    public Account save(Account account) {
        account.setUserId(utils.getAuthUser().getId());
        return accountsRepository.save(account);
    }

    @Transactional
    public Account adjustBalance(long accountId, @NonNull BigDecimal newBalance) {
        final Account account = findById(accountId);

        if (account.getUserId() != utils.getAuthUser().getId())
            throw new WithoutPermissionException();

        createNewCashFlowRelease(account, newBalance);

        account.setBalance(newBalance);

        return accountsRepository.save(account);
    }

    private void createNewCashFlowRelease(Account account, BigDecimal newBalance) {
        final CashFlow release = new CashFlow();
        release.setUserId(account.getUserId());
        release.setDescription("");
        release.setAccountId(account.getId());
        release.setAmount(
                newBalance.compareTo(account.getBalance()) > 0
                        ? newBalance.subtract(account.getBalance())
                        : account.getBalance().subtract(newBalance)
        );
        release.setType(newBalance.compareTo(account.getBalance()) > 0 ? "R" : "E");
        release.setDone(true);
        release.setCategoryId(1L);
        release.setDate(LocalDate.now());
        release.setRepeat("");

        cashFlowService.addRelease(release, 0);
    }

    @Transactional(readOnly = true)
    public List<Account> getHomeAccountsList(long userId) {
        return accountsRepository.getHomeAccountsList(userId);
    }
}

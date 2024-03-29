package br.finax.services;

import br.finax.models.Account;
import br.finax.models.CashFlow;
import br.finax.repository.AccountsRepository;
import br.finax.repository.CashFlowRepository;
import br.finax.dto.InterfacesSQL.AccountBasicList;
import br.finax.utils.UtilsService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountsRepository accountRepository;
    private final CashFlowRepository cashFlowRepository;
    private final UtilsService utilsService;

    public List<Account> getByUser() {
        return accountRepository.findAllByUserIdOrderByIdAsc(utilsService.getAuthUser().getId());
    }

    public Account getById(long id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account not found"));
    }

    public ResponseEntity<Account> save(Account account) {
        return ResponseEntity.ok().body(accountRepository.save(account));
    }

    public ResponseEntity<Account> adjustBalance(long accountId, double newBalance) {
        try {
            final Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

            final CashFlow release = new CashFlow();
            release.setUserId(account.getUserId());
            release.setDescription("");
            release.setAccountId(account.getId());
            release.setAmount(newBalance > account.getBalance() ? newBalance - account.getBalance() : account.getBalance() - newBalance);
            release.setType(newBalance > account.getBalance() ? "R" : "E");
            release.setDone(true);
            release.setCategoryId(21L);
            release.setDate(LocalDate.now());

            cashFlowRepository.save(release);

            account.setBalance(newBalance);

            return ResponseEntity.ok().body(accountRepository.save(account));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public List<AccountBasicList> getBasicList() {
        return accountRepository.getBasicList(utilsService.getAuthUser().getId());
    }
}

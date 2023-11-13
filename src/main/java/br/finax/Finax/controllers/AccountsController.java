package br.finax.finax.controllers;

import br.finax.finax.models.Account;
import br.finax.finax.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    @Autowired
    private AccountsRepository accountsRepository;

    @GetMapping("/get-by-user/{userId}")
    private List<Account> getByUser(@PathVariable Long userId) {
        return accountsRepository.findAllByUserIdOrderByPresentationSequenceAsc(userId);
    }

    @GetMapping("/{id}")
    private Account getById(@PathVariable Long id) {
        return accountsRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta não localizada")
        );
    }

    @PostMapping
    private ResponseEntity<Account> save(@RequestBody Account account) {
        try {
            if (account.getId() == null) {
                account.setPresentationSequence(
                        accountsRepository.findAllByUserIdOrderByPresentationSequenceAsc(account.getUserId()).size()
                );
            }

            return ResponseEntity.ok().body(accountsRepository.save(account));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao salvar a conta");
        }
    }

    @PutMapping("save-sequence")
    private ResponseEntity<List<Account>> saveSequence(@RequestBody List<Account> accountsList) {
        try {
            for (int i = 0; i < accountsList.size(); i++) {
                accountsList.get(i).setPresentationSequence(i);
            }

            return ResponseEntity.ok().body(accountsRepository.saveAll(accountsList));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao salvar a sequência de apresentação");
        }
    }

    @PutMapping("/{id}")
    private ResponseEntity<Account> archiveOrUnarchive(@PathVariable Long id) {
        try {
            Account account = accountsRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Conta não localizada")
            );

            account.setArchived(!account.isArchived());

            return ResponseEntity.ok().body(accountsRepository.save(account));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

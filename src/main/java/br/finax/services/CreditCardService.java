package br.finax.services;

import br.finax.dto.InterfacesSQL.BasicCard;
import br.finax.dto.InterfacesSQL.UserCreditCard;
import br.finax.exceptions.NotFoundException;
import br.finax.models.CreditCard;
import br.finax.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.finax.utils.UtilsService.getAuthUser;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;

    @Transactional(readOnly = true)
    public CreditCard findById(long id) {
        final CreditCard card = creditCardRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        checkPermission(card);

        return card;
    }

    @Transactional(readOnly = true)
    public List<UserCreditCard> getByUser() {
        return creditCardRepository.getAllByUser(getAuthUser().getId());
    }

    @Transactional
    public CreditCard createNew(CreditCard card) {
        card.setId(null);
        card.setUserId(getAuthUser().getId());
        return creditCardRepository.save(card);
    }

    @Transactional
    public CreditCard edit(CreditCard card) {
        checkPermission(card);

        return creditCardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public List<BasicCard> getBasicList() {
        return creditCardRepository.getBasicList(getAuthUser().getId());
    }

    @Transactional(readOnly = true)
    public long findUserIdById(long id) {
        return creditCardRepository.findUserIdById(id);
    }

    @Transactional(readOnly = true)
    public List<CreditCard> findAllByUserId(long userId) {
        return creditCardRepository.findAllByUserId(userId);
    }

    private void checkPermission(CreditCard card) {
        if (!card.getUserId().equals(getAuthUser().getId()))
            throw new NotFoundException();
    }
}

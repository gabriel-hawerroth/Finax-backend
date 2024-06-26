package br.finax.services;

import br.finax.dto.InterfacesSQL.CardBasicList;
import br.finax.dto.InterfacesSQL.UserCreditCards;
import br.finax.exceptions.NotFoundException;
import br.finax.models.CreditCard;
import br.finax.repository.CreditCardRepository;
import br.finax.utils.UtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;

    private final UtilsService utils;

    @Transactional(readOnly = true)
    public CreditCard findById(long id) {
        return creditCardRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<UserCreditCards> getByUser() {
        return creditCardRepository.getAllByUser(utils.getAuthUser().getId());
    }

    @Transactional
    public CreditCard save(CreditCard card) {
        card.setUser_id(utils.getAuthUser().getId());
        return creditCardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public List<CardBasicList> getBasicList() {
        return creditCardRepository.getBasicList(utils.getAuthUser().getId());
    }
}

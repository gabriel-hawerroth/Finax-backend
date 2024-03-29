package br.finax.services;

import br.finax.enums.EmailType;
import br.finax.models.Token;
import br.finax.models.User;
import br.finax.dto.EmailRecord;
import br.finax.repository.CategoryRepository;
import br.finax.repository.TokenRepository;
import br.finax.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.mail.MessagingException;
import java.net.URI;
import java.util.Optional;

import static br.finax.utils.UtilsService.generateHash;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder bCrypt;
    private final EmailService emailService;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<User> newUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "User already exists");

        user.setPassword(bCrypt.encode(user.getPassword()));
        user.setActive(false);
        user.setAccess("premium");
        user.setCanChangePassword(false);
        user.setSignature("month");

        userRepository.save(user);

        final Token token = new Token();
        token.setUserId(user.getId());
        token.setToken(generateHash(user.getEmail()));
        tokenRepository.save(token);

        sendActivateAccountEmail(user.getEmail(), user.getId(), token.getToken());

        return ResponseEntity.ok().body(user);
    }

    public ResponseEntity<Void> activateUser(Long userId, String token) {
        final String savedToken = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST)).getToken();

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        if (savedToken.equals(token)) {
            user.setActive(true);
            userRepository.save(user);
        }

        categoryRepository.insertNewUserCategories(userId);

        final URI uri = URI.create(UriComponentsBuilder
                .fromUriString("https://hawetec.com.br/finax/ativacao-da-conta").build().toUriString());

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(uri)
                .build();
    }

    public void sendChangePasswordMail(String email) {
        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        final Optional<Token> tok = tokenRepository.findByUserId(user.getId());

        final Token token = new Token();
        tok.ifPresent(value -> token.setId(value.getId()));
        token.setUserId(user.getId());
        token.setToken(generateHash(user.getEmail()));
        tokenRepository.save(token);

        sendChangePasswordEmail(user.getEmail(), user.getId(), token.getToken());
    }

    public ResponseEntity<Void> permitChangePassword(Long userId, String token) {
        final String savedToken = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token not found")).getToken();

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        if (savedToken.equals(token)) {
            user.setCanChangePassword(true);
            userRepository.save(user);
        }

        final URI uri = URI.create(UriComponentsBuilder
                .fromUriString("https://hawetec.com.br/finax/recuperacao-da-senha/" + user.getId()).build().toUriString());

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(uri)
                .build();
    }

    private void sendActivateAccountEmail(String userMail, Long userId, String token) {
        try {
            emailService.sendMail(
                    new EmailRecord(
                            userMail,
                            "Ativação da conta Finax",
                            emailService.buildEmailTemplate(EmailType.ACTIVATE_ACCOUNT, userId, token)
                    )
            );
        } catch (MessagingException messagingException) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendChangePasswordEmail(String userMail, Long userId, String token) {
        try {
            emailService.sendMail(
                    new EmailRecord(
                            userMail,
                            "Alteração da senha Finax",
                            emailService.buildEmailTemplate(EmailType.CHANGE_PASSWORD, userId, token)
                    )
            );
        } catch (MessagingException messagingException) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

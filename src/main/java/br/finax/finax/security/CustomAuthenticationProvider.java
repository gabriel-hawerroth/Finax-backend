package br.finax.finax.security;

import br.finax.finax.models.AccessLog;
import br.finax.finax.models.User;
import br.finax.finax.repository.AccessLogRepository;
import br.finax.finax.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Primary
@Component
public class CustomAuthenticationProvider implements AuthenticationManager {

    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccessLogRepository accessLogRepository;

    @Override
    public Authentication authenticate(Authentication sendedCredentials) throws AuthenticationException {
        String username = sendedCredentials.getPrincipal().toString();
        String password = sendedCredentials.getCredentials().toString();

        Authentication authentication = this.fazerLogin(username, password);
        if (authentication == null) {
            throw new BadCredentialsException("Bad credentials");
        }

        ((AbstractAuthenticationToken) authentication).setDetails(authentication.getDetails());



        return authentication;
    }

    private Authentication fazerLogin(String username, String password) {
        User existentLogin = userRepository.findByEmail(username);
        if (existentLogin == null) {
            return null;
        }
        if (!encoder.matches(password, existentLogin.getPassword())){
            return null;
        }

        if (!existentLogin.isActivate()) {
            throw new BadCredentialsException("Inactive user");
        }

        if (!username.equals("gabrielhawerroth04@gmail.com") && !username.equals("dhawerroth@gmail.com")) {
            AccessLog log = new AccessLog(existentLogin.getId(), LocalDateTime.now());
            accessLogRepository.save(log);
        }

        return new UsernamePasswordAuthenticationToken(existentLogin, null, null);
    }
}

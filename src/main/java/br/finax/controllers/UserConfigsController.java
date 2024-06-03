package br.finax.controllers;

import br.finax.models.UserConfigs;
import br.finax.services.UserConfigsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user-configs")
public class UserConfigsController {

    private final UserConfigsService userConfigsService;

    @GetMapping("/get-by-user")
    public ResponseEntity<UserConfigs> getByUser() {
        return ResponseEntity.ok(
                userConfigsService.getByUser()
        );
    }

    @PostMapping("/save")
    public ResponseEntity<UserConfigs> save(@RequestBody @Valid UserConfigs userConfigs) {
        final UserConfigs savedUserConfigs = userConfigsService.save(userConfigs);

        if (userConfigs.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUserConfigs);
        }

        return ResponseEntity.ok(savedUserConfigs);
    }
}

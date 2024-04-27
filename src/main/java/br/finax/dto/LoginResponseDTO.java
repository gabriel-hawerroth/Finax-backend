package br.finax.dto;

import br.finax.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginResponseDTO(
        @NotNull
        User user,

        @NotNull
        @NotBlank
        String token
) {
}

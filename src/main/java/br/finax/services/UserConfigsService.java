package br.finax.services;

import br.finax.enums.user_configs.UserConfigsReleasesViewMode;
import br.finax.enums.user_configs.UserConfigsTheme;
import br.finax.exceptions.WithoutPermissionException;
import br.finax.models.UserConfigs;
import br.finax.repository.UserConfigsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.finax.utils.UtilsService.getAuthUser;

@Service
@RequiredArgsConstructor
public class UserConfigsService {

    private final UserConfigsRepository userConfigsRepository;

    @Transactional
    public UserConfigs getByUser() {
        final long userId = getAuthUser().getId();

        return userConfigsRepository.findByUserId(userId)
                .orElseGet(() -> userConfigsRepository.save(getDefaultUserConfigs(userId)));
    }

    @Transactional
    public UserConfigs save(UserConfigs userConfigs) {
        if (userConfigs.getId() == null)
            userConfigs.setUserId(getAuthUser().getId());
        else
            checkPermission(userConfigs);

        return userConfigsRepository.save(userConfigs);
    }

    @Transactional
    public void insertNewUserConfigs(long userId) {
        userConfigsRepository.save(getDefaultUserConfigs(userId));
    }

    private void checkPermission(UserConfigs userConfigs) {
        if (!userConfigs.getUserId().equals(getAuthUser().getId()))
            throw new WithoutPermissionException();
    }

    private UserConfigs getDefaultUserConfigs(long userId) {
        var configs = new UserConfigs();

        configs.setUserId(userId);
        configs.setTheme(UserConfigsTheme.light);
        configs.setAddingMaterialGoodsToPatrimony(false);
        configs.setLanguage("pt-BR");
        configs.setCurrency("R$");
        configs.setReleasesViewMode(UserConfigsReleasesViewMode.RELEASES);
        configs.setEmailNotifications(true);

        return configs;
    }
}

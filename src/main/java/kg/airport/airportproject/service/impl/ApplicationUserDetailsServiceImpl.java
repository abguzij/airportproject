package kg.airport.airportproject.service.impl;

import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.service.ApplicationUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ApplicationUserDetailsServiceImpl implements ApplicationUserDetailsService {
    private final ApplicationUsersEntityRepository applicationUsersEntityRepository;

    @Autowired
    public ApplicationUserDetailsServiceImpl(
            ApplicationUsersEntityRepository applicationUsersEntityRepository
    ) {
        this.applicationUsersEntityRepository = applicationUsersEntityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(Objects.isNull(username) || username.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ошибка: Имя пользователя не может быть null или пустым!"
            );
        }
        Optional<ApplicationUsersEntity> applicationUsersEntityOptional
                = this.applicationUsersEntityRepository.getApplicationUsersEntityByUsernameAndIsEnabledTrue(username);
        if(applicationUsersEntityOptional.isEmpty()){
            throw new UsernameNotFoundException(
                    "Пользователь с таким именем не найден или был удален!"
            );
        }
        ApplicationUsersEntity applicationUsersEntity = applicationUsersEntityOptional.get();
        return applicationUsersEntityOptional.get();
    }
}

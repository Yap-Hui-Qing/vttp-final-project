package vttpb_final_project.travelplanner.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vttpb_final_project.travelplanner.models.UserEntity;
import vttpb_final_project.travelplanner.repositories.UserRepository;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger logger = Logger.getLogger(UserDetailsServiceImpl.class.getName());

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        logger.info("received username: %s".formatted(username));

        if (userEntity.isEmpty()) {
            logger.info("User not found");
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return userEntity.get();
    }
}

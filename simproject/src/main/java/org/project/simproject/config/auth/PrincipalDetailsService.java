package org.project.simproject.config.auth;

import lombok.RequiredArgsConstructor;
import org.project.simproject.domain.User;
import org.project.simproject.repository.entityRepo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElse(null);
        if(!(user == null)) return new PrincipalDetails(user);
        return null;
    }
}

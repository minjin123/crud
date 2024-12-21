package com.site.Services;

import com.site.DTO.SiteUserDTO;
import com.site.Exception.DataNotFoundException;
import com.site.Services.Mapper.SiteUserMapper;
import com.site.models.SiteUser;
import com.site.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(String email, String username, String password) {
        SiteUserDTO userDTO = new SiteUserDTO();
        userDTO.setEmail(email);
        userDTO.setUsername(username);
        userDTO.setPassword(passwordEncoder.encode(password));
        SiteUser user = SiteUserMapper.INSTANCE.toSiteUser(userDTO);
        this.userRepository.save(user);
    }
    public SiteUserDTO getUser(String username) {
        Optional<SiteUser> user = this.userRepository.findByUsername(username);
        if(user.isPresent()) {
            return SiteUserMapper.INSTANCE.toSiteUserDTO(user.get());
        } else {
            throw new DataNotFoundException("user not found");
        }
    }
    public boolean isEmailRegistered(String email) {
        return this.userRepository.existsByEmail(email);
    }
    public boolean isUsernameRegistered(String username) {
        return this.userRepository.existsByUsername(username);
    }
    public String getUserByEmail(String email) {
        Optional<SiteUser> user = this.userRepository.findByEmail(email);
        if(user.isPresent()) {
            return user.get().getUsername();
        }else {
            throw new DataNotFoundException("user not found");
        }
    }
    public void updatePassword(String email, String newPassword) {
        Optional<SiteUser> user = this.userRepository.findByEmail(email);
        if(user.isPresent()) {
            user.get().setPassword(passwordEncoder.encode(newPassword));
            this.userRepository.save(user.get());
        }else{
            throw new DataNotFoundException("user not found");
        }

    }
}

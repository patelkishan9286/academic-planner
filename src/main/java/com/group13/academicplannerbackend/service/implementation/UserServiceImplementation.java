package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.exception.UnAuthorizedUserException;
import com.group13.academicplannerbackend.exception.UserNotFoundException;
import com.group13.academicplannerbackend.exception.VerificationException;
import com.group13.academicplannerbackend.model.JwtResponse;
import com.group13.academicplannerbackend.model.ProfileStatus;
import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.model.UserMeta;
import com.group13.academicplannerbackend.repository.UserMetaRepository;
import com.group13.academicplannerbackend.repository.UserRepository;
import com.group13.academicplannerbackend.service.UserService;
import com.group13.academicplannerbackend.service.VerificationService;
import com.group13.academicplannerbackend.util.Constants;
import com.group13.academicplannerbackend.util.JWTUtil;
import javax.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplementation implements UserService, UserDetailsService {
    private UserRepository userRepository;
    private UserMetaRepository userMetaRepository;
    private VerificationService verificationService;
    private JWTUtil jwtUtil;

    @Autowired
    public UserServiceImplementation(
            UserRepository userRepository,
            UserMetaRepository userMetaRepository,
            VerificationService verificationService,
            JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userMetaRepository = userMetaRepository;
        this.verificationService = verificationService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * @param user
     */
    @Override
    @Transactional
    public void register(User user) {
        String password = user.getPasswordHash();
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPasswordHash(passwordHash);
        userRepository.save(user);

        UserMeta userMeta = new UserMeta();
        userMeta.setProfileStatus(ProfileStatus.UNSET);
        userMeta.setUser(user);
        userMetaRepository.save(userMeta);

        verificationService.sendVerificationEmail(user.getEmail());
    }

    @Override
    public JwtResponse loginProcess(User user) throws UnAuthorizedUserException {
        UserMeta userMeta;
        User tempUser;
        tempUser = userRepository.findByEmail(user.getEmail());

        if (user.getEmail() != "") {
            Boolean password = BCrypt.checkpw(user.getPasswordHash(), tempUser.getPasswordHash());

            if (password) {
                userMeta = userMetaRepository.findByUser(tempUser);

                if (userMeta.isVerified() == true) {
                    UserDetails userDetails = (UserDetails) tempUser;
                    String jwtToken = Constants.getJwtTokenPrefix() + jwtUtil.generateToken(userDetails);
                    return new JwtResponse(jwtToken, userMeta.getProfileStatus());
                } else {
                    throw new VerificationException("please varify your email");
                }
            } else {
                throw new UnAuthorizedUserException("Wrong username or password");
            }
        } else {
            throw new UnAuthorizedUserException("Wrong username or password");
        }
    }

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    @Override
    public void updateProfileStatus(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email " + email);
        }

        UserMeta userMeta = user.getUserMeta();
        if (userMeta == null) {
            throw new UserNotFoundException("UserMeta not found for the user with email " + email);
        }

        userMeta.setProfileStatus(ProfileStatus.SET);
        userMetaRepository.save(userMeta);
    }
}

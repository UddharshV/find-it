package com.uddharsh.findit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uddharsh.findit.entity.User;
import com.uddharsh.findit.exception.ConflictException;
import com.uddharsh.findit.exception.NotFoundException;
import com.uddharsh.findit.repository.UserRepository;

import java.util.List;
import org.springframework.data.domain.Sort;

@Service                                   // tells Spring to create and manage this class
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {   // Spring passes the repository in
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(String name, String email) {
        if (userRepository.existsByEmail(User.normalizeEmail(email))) {
            throw new ConflictException("Email already registered: " + email);
        }
        return userRepository.save(new User(name, email));
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> listUsers() {
        return userRepository.findAll(Sort.by(User::getName));
}
}
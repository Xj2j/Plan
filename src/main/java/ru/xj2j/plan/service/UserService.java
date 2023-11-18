package ru.xj2j.plan.service;

import org.springframework.stereotype.Service;
import ru.xj2j.plan.repository.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get
}

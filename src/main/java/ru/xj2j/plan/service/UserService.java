package ru.xj2j.plan.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.xj2j.plan.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;


}

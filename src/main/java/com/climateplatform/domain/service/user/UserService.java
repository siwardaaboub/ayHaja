package com.climateplatform.domain.service.user;

import com.climateplatform.domain.model.User;
import com.climateplatform.domain.repository.UserRepository;
import com.climateplatform.dto.request.user.UpdateUserRequest;
import com.climateplatform.dto.response.user.UserResponse;
import com.climateplatform.exception.ResourceNotFoundException;
import com.climateplatform.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;

    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return UserResponse.from(user);
    }

    public UserResponse getUserProfile(UUID userId) {
        User user = userRepository.findByIdWithHealthProfile(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateUserProfile(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        userProfileMapper.updateUserFromDto(request, user);
        User updatedUser = userRepository.save(user);

        return UserResponse.from(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        userRepository.delete(user);
    }
}

package com.house.hunter.service;

import com.house.hunter.model.dto.user.CreateAdminDTO;
import com.house.hunter.model.dto.user.GetAllUsersResponse;
import com.house.hunter.model.dto.user.RequestFormDTO;
import com.house.hunter.model.dto.user.UserGetResponse;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public interface UserService {

    void registerUser(UserRegistrationDto userRegistrationDto);

    UserGetResponse getUserByEmail(String email);

    UserGetResponse getUserById(UUID id);

    Page<GetAllUsersResponse> getAllUsers(Pageable pageable);

    void updatePassword(String oldPassword, String newPassword, String email);

    void deleteUser(String email);

    List<String> getUserDocuments(String email);

    Resource downloadFile(String filename);

    UUID uploadDocument(String documentType, MultipartFile file) throws IOException;

    void deleteDocument(String documentName);

    void verifyUser(String userId);

    void createAdminUser(CreateAdminDTO createAdminDTO);

    void confirmEmail(String confirmationToken);

    void activateUser(String email);

    void forgotPassword(String email);

    void resetPassword(String resetToken, String newPassword);

    void extendDataRetention(String email);

    void blockUser(String email);

    void markUserAsNotVerified(String email);

    void submitRequest(RequestFormDTO requestFormDTO);

    UUID uploadOwnershipDocument(MultipartFile file, UUID propertyId) throws IOException;

    String getUserDocumentsByProperty(UUID userId, UUID propertyId);

}


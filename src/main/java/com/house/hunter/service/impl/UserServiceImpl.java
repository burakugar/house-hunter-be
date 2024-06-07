package com.house.hunter.service.impl;

import com.house.hunter.constant.DocumentType;
import com.house.hunter.constant.PropertyStatus;
import com.house.hunter.constant.UserAccountStatus;
import com.house.hunter.constant.UserRole;
import com.house.hunter.constant.UserVerificationStatus;
import com.house.hunter.event.UserActivationEvent;
import com.house.hunter.event.UserBlockedEvent;
import com.house.hunter.event.UserNotVerifiedEvent;
import com.house.hunter.exception.DocumentNotFoundException;
import com.house.hunter.exception.FileOperationException;
import com.house.hunter.exception.IllegalRequestException;
import com.house.hunter.exception.InvalidDocumentTypeException;
import com.house.hunter.exception.InvalidTokenException;
import com.house.hunter.exception.InvalidVerificationTokenException;
import com.house.hunter.exception.MailServiceException;
import com.house.hunter.exception.PropertyNotFoundException;
import com.house.hunter.exception.UserAlreadyExistsException;
import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.model.dto.property.GetPropertyDTO;
import com.house.hunter.model.dto.user.CreateAdminDTO;
import com.house.hunter.model.dto.user.GetAllUsersResponse;
import com.house.hunter.model.dto.user.RequestFormDTO;
import com.house.hunter.model.dto.user.UserGetResponse;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import com.house.hunter.model.entity.ConfirmationToken;
import com.house.hunter.model.entity.Document;
import com.house.hunter.model.entity.Property;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.ConfirmationTokenRepository;
import com.house.hunter.repository.DocumentRepository;
import com.house.hunter.repository.PropertyRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.service.EmailService;
import com.house.hunter.service.UserService;
import com.house.hunter.util.DocumentUtil;
import com.house.hunter.util.MailUtil;
import com.house.hunter.util.PasswordEncoder;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final DocumentRepository documentRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    private final ApplicationEventPublisher applicationEventPublisher;


    public UserServiceImpl(UserRepository userRepository, PropertyRepository propertyRepository, DocumentRepository documentRepository, ModelMapper modelMapper,
                           ConfirmationTokenRepository confirmationTokenRepository, EmailService emailService, ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.propertyRepository = propertyRepository;
        this.modelMapper = modelMapper;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailService = emailService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);

    private final DocumentUtil documentUtil = DocumentUtil.getInstance();

    @Transactional()
    @Override
    public void registerUser(@Valid final UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByEmail(userRegistrationDto.getEmail())) {
            throw new UserAlreadyExistsException(userRegistrationDto.getEmail());
        }
        final User user = modelMapper.map(userRegistrationDto, User.class);
        user.setVerificationStatus(UserVerificationStatus.PENDING_VERIFICATION);
        user.setAccountStatus(UserAccountStatus.NOT_ACTIVATED);
        // Encrypting the password with automatic salting
        final String encryptedPassword = PasswordEncoder.getPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setCreatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);
        try {
            MimeMessagePreparator registrationEmail = MailUtil.buildRegistrationEmail(user.getEmail(), confirmationToken.getConfirmationToken());
            emailService.sendEmail(registrationEmail);
            LOGGER.info("User created: {}", user.getEmail());
        } catch (Exception e) {
            throw new MailServiceException(e.getMessage());
        }

    }

    @Override
    public Page<GetAllUsersResponse> getAllUsers(Pageable pageable) {
        Page users = userRepository.findAll(pageable);
        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found");
        }
        return users.map(user -> modelMapper.map(user, GetAllUsersResponse.class));
    }

    @Override
    public UserGetResponse getUserByEmail(@Valid final String email) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String requestMakerEmail = authentication.getName();
        // Check if the currently authenticated user is an admin or the same user being retrieved
        if (hasRole(authentication, UserRole.ADMIN) || requestMakerEmail.equals(email)) {
            return userRepository.findByEmail(email)
                    .map(user -> {
                        LOGGER.info("User found: {}", user.getEmail());
                        // If the user is not verified, hide the phone number
                        if (userRepository.findByEmail(requestMakerEmail).get().getVerificationStatus() != UserVerificationStatus.VERIFIED) {
                            user.setPhoneNumber(null);
                        }
                        user.setProperties(user.getProperties().stream().filter(property -> property.getStatus().equals(PropertyStatus.VERIFIED)).toList());
                        return modelMapper.map(user, UserGetResponse.class);
                    })
                    .orElseThrow(() -> new UserNotFoundException(email));
        } else {
            throw new IllegalRequestException("You are not authorized to retrieve this user");
        }
    }

    @Override
    public UserGetResponse getUserById(UUID uuid) {
        return userRepository.findById(uuid)
                .map(user -> {
                    LOGGER.info("User found: {}", user.getEmail());
                    UserGetResponse response = modelMapper.map(user, UserGetResponse.class);
                    List<Property> properties = new ArrayList<>(user.getProperties().stream().filter(property -> property.getStatus().equals(PropertyStatus.VERIFIED)).toList());
                    List<GetPropertyDTO> propertyDTOs = properties.stream()
                            .map(property -> modelMapper.map(property, GetPropertyDTO.class))
                            .toList();
                    response.setProperties(propertyDTOs);
                    return response;
                })
                .orElseThrow(() -> new UserNotFoundException(uuid.toString()));
    }

    @Override
    public String getUserDocumentsByProperty(UUID userId, UUID propertyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with Email: " + userId));

        Property property = user.getProperties().stream()
                .filter(p -> p.getId().equals(propertyId))
                .findFirst()
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + propertyId));

        return property.getDocument().getFilename();
    }


    @Override
    @Transactional
    public void updatePassword(String oldPassword, String newPassword, String email) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Check if the currently authenticated user is an admin or the same user being updated
        if (hasRole(authentication, UserRole.ADMIN) || currentUserEmail.equals(email)) {
            final User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException(email));
            LOGGER.info("User found: {}", email);
            if (!PasswordEncoder.getPasswordEncoder().matches(oldPassword, user.getPassword())) {
                throw new IllegalRequestException("Old password is incorrect");
            }
            final String encryptedPassword = PasswordEncoder.getPasswordEncoder().encode(newPassword);
            user.setPassword(encryptedPassword);
            LOGGER.info("Password has been updated for user : {}", user.getEmail());
            userRepository.save(user);
        } else {
            throw new IllegalRequestException("You are not authorized to update this user's password");
        }
    }

    @Override
    @Transactional
    public void deleteUser(@Valid String email) {
        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Check if the currently authenticated user is an admin or the same user being deleted
        if (hasRole(authentication, UserRole.ADMIN) || currentUserEmail.equals(email)) {
            userRepository.delete(user);
            LOGGER.info("User deleted: {}", user.getEmail());
        } else {
            throw new IllegalRequestException("You are not authorized to delete this user");
        }
    }

    @Override
    public List<String> getUserDocuments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return user.getDocuments().stream()
                .filter(document -> !document.getDocumentType().equals(DocumentType.OWNERSHIP_DOCUMENT))
                .map(Document::getFilename)
                .collect(Collectors.toList());
    }


    @Override
    public Resource downloadFile(String filename) {
        User user = getAuthenticatedUser();
        // if the user does not have requested document, throw exception
        documentRepository.findDocumentsByUserEmail(user.getEmail()).orElseThrow(DocumentNotFoundException::new);
        return documentUtil.getDocument(filename);

    }

    @Override
    @Transactional
    public UUID uploadDocument(String documentType, MultipartFile file) throws IOException {
        if (!DocumentType.contains(documentType)) {
            throw new InvalidDocumentTypeException();
        }
        User user = getAuthenticatedUser();
        try {
            String documentName = documentUtil.saveDocumentToStorage(file);

            // Check if a document with the same user and documentType already exists
            Optional<Document> existingDocument = documentRepository.findByUserAndDocumentType(user, DocumentType.valueOf(documentType));

            if (existingDocument.isPresent()) {
                LOGGER.info("Document already exists for user: {}", user.getEmail());
                // If the document exists, update its filename and save it
                Document document = existingDocument.get();
                document.setFilename(documentName);
                return documentRepository.save(document).getId();
            } else {
                // If the document doesn't exist, create a new one and save it
                Document document = new Document(null, documentName, DocumentType.valueOf(documentType), LocalDateTime.now(), user, null);
                return documentRepository.save(document).getId();
            }
        } catch (Exception e) {
            documentUtil.deleteDocument(file.getOriginalFilename());
            throw new FileOperationException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public UUID uploadOwnershipDocument(MultipartFile file, UUID propertyId) throws IOException {
        User user = getAuthenticatedUser();
        String documentName = null;
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new IllegalRequestException("Property not found with id: " + propertyId));

            if (!property.getOwner().equals(user)) {
                throw new IllegalRequestException("User is not the owner of the property");
            }
            // Check if the property already has an ownership document
            Document document = documentRepository.findByPropertyAndDocumentType(property, DocumentType.OWNERSHIP_DOCUMENT)
                    .orElse(new Document());
            // if it is a new document, save it
            if (document.getId() == null) {
                documentName = documentUtil.saveDocumentToStorage(file);
                document.setFilename(documentName);
                document.setDocumentType(DocumentType.OWNERSHIP_DOCUMENT);
                document.setCreatedAt(LocalDateTime.now());
                document.setUser(user);
                document.setProperty(property);
            } else {
                // if the document already exists, update it
                documentUtil.deleteDocument(document.getFilename());
                // Save the new document
                documentName = documentUtil.saveDocumentToStorage(file);
                document.setFilename(documentName);
            }
            Document savedDocument = documentRepository.save(document);
            return savedDocument.getId();
        } catch (Exception e) {
//            documentUtil.deleteDocument(documentDirectory,documentName, true);
            throw new FileOperationException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteDocument(String documentName) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String requestMakerEmail = authentication.getName();
            Document document = documentRepository.findByFilename(documentName)
                    .orElseThrow(DocumentNotFoundException::new);
            String requestMaker = document.getUser().getEmail();
            // Check if the currently authenticated user is an admin or the same user being retrieved
            if (hasRole(authentication, UserRole.ADMIN) || requestMakerEmail.equals(requestMaker)) {
                documentRepository.delete(document);
                LOGGER.info("Document deleted: {}", documentName);
                documentUtil.deleteDocument(documentName);
            } else {
                throw new IllegalRequestException("You are not authorized to delete document of this user");
            }
        } catch (IOException e) {
            throw new FileOperationException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void verifyUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        user.setVerificationStatus(UserVerificationStatus.VERIFIED);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void activateUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        user.setAccountStatus(UserAccountStatus.ACTIVE);
        LOGGER.info("User account activated: {}", user.getEmail());
        applicationEventPublisher.publishEvent(new UserActivationEvent(user));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void createAdminUser(CreateAdminDTO createAdminDTO) {
        if (userRepository.existsByEmail(createAdminDTO.getEmail())) {
            throw new UserAlreadyExistsException(createAdminDTO.getEmail());
        }
        final User user = modelMapper.map(createAdminDTO, User.class);
        user.setRole(UserRole.ADMIN);
        user.setVerificationStatus(UserVerificationStatus.VERIFIED);
        user.setAccountStatus(UserAccountStatus.ACTIVE);
        user.setCreatedAt(java.time.LocalDateTime.now());
        // Encrypting the password with automatic salting
        final String encryptedPassword = PasswordEncoder.getPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        LOGGER.info("Admin user created: {}", user.getEmail());
    }

    @Override
    public void confirmEmail(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(InvalidVerificationTokenException::new);
        Date createdDate = token.getCreatedDate();

        // Get the current date and subtract 30 minutes
        Date currentDateMinus30Minutes = new Date(System.currentTimeMillis() - 30 * 60 * 1000);

        // If the token creation date is before the current date minus 30 minutes, then the token is expired
        if (createdDate.before(currentDateMinus30Minutes)) {
            throw new InvalidVerificationTokenException();
        }

        User user = userRepository.findByEmail(token.getUser().getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + token.getUser().getEmail()));
        user.setAccountStatus(UserAccountStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Generate a unique reset password token
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        userRepository.save(user);
        LOGGER.info("Reset password token generated for user: {}", user.getEmail());

        // Send the password reset email with the link
        MimeMessagePreparator resetPasswordEmail = MailUtil.buildResetPasswordEmail(user.getEmail(), resetToken);
        emailService.sendEmail(resetPasswordEmail);
        LOGGER.info("Reset password email sent to user: {}", user.getEmail());
    }

    @Override
    public void resetPassword(String resetToken, String newPassword) {
        User user = userRepository.findByResetPasswordToken(resetToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset password token : " + resetToken));

        // Update the user's password
        user.setPassword(PasswordEncoder.getPasswordEncoder().encode(newPassword));
        LOGGER.info("Password reset for user: {}", user.getEmail());
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    @Override
    public void blockUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        if (user.getAccountStatus() == UserAccountStatus.BLOCKED) {
            throw new IllegalRequestException("User is already blocked");
        }
        if (user.getRole() == UserRole.ADMIN) {
            throw new IllegalRequestException("Admin user cannot be blocked");
        }
        user.setAccountStatus(UserAccountStatus.BLOCKED);
        applicationEventPublisher.publishEvent(new UserBlockedEvent(user));
        userRepository.save(user);
        LOGGER.info("User account blocked: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void markUserAsNotVerified(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        user.setVerificationStatus(UserVerificationStatus.NOT_VERIFIED);
        applicationEventPublisher.publishEvent(new UserNotVerifiedEvent(user));
        userRepository.save(user);
    }

    @Override
    public void submitRequest(RequestFormDTO requestFormDTO) {
        List<User> admins = userRepository.findByRole(UserRole.ADMIN).orElseThrow(UserNotFoundException::new);
        for (User admin : admins) {
            MimeMessagePreparator complaintEmail = MailUtil.buildComplaintEmail(admin.getEmail(), requestFormDTO);
            emailService.sendEmail(complaintEmail);
        }
    }

    @Override
    @Transactional
    public void extendDataRetention(String token) {
        String[] parts = token.split("_");
        String email = parts[0];
        long timestamp = Long.parseLong(parts[1]);
        LOGGER.info("Data retention extension requested for user: {}", email);
        // Check if the token has expired (e.g., valid for 24 hours)
        if (System.currentTimeMillis() - timestamp > 24 * 60 * 60 * 1000) {
            throw new InvalidTokenException("Token has expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        // Update the createdAt timestamp to the current time plus 1 year
        final LocalDateTime newRetentionDate = user.getCreatedAt().plusYears(1);
        user.setCreatedAt(newRetentionDate);
        userRepository.save(user);
        LOGGER.info("Data retention is extended till {} for user {}", newRetentionDate, email);
    }


    private User getAuthenticatedUser() {
        return userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UserNotFoundException("User can not be gotten from the authorization token " + SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    private boolean hasRole(Authentication authentication, UserRole role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role.name()));
    }

    @EventListener
    public void handleUserActivationEvent(UserActivationEvent event) {
        User user = event.getUser();
        String userEmail = user.getEmail();

        MimeMessagePreparator messagePreparator = MailUtil.buildUserActivationEmail(userEmail);
        emailService.sendEmail(messagePreparator);
    }

    @EventListener
    public void handleUserNotVerifiedEvent(UserNotVerifiedEvent event) {
        User user = event.getUser();
        String userEmail = user.getEmail();

        MimeMessagePreparator messagePreparator = MailUtil.buildUserNotVerifiedEmail(userEmail);
        emailService.sendEmail(messagePreparator);
    }

    @EventListener
    public void handleUserBlockedEvent(UserBlockedEvent event) {
        User user = event.getUser();
        String userEmail = user.getEmail();

        MimeMessagePreparator messagePreparator = MailUtil.buildUserBlockedEmail(userEmail);
        emailService.sendEmail(messagePreparator);
    }


}

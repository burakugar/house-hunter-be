package com.house.hunter.controller;

import com.house.hunter.model.dto.user.CreateAdminDTO;
import com.house.hunter.model.dto.user.GetAllUsersResponse;
import com.house.hunter.model.dto.user.PasswordResetDto;
import com.house.hunter.model.dto.user.RequestFormDTO;
import com.house.hunter.model.dto.user.UserGetResponse;
import com.house.hunter.model.dto.user.UserPasswordUpdateDTO;
import com.house.hunter.model.dto.user.UserRegistrationDto;
import com.house.hunter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@Validated
@Tag(name = "User Controller", description = "Endpoints for user management")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get user by email")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")
    public ResponseEntity<UserGetResponse> getUserByEmail(@RequestParam @Valid @Email final String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByEmail(email));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserGetResponse> getUserById(@PathVariable("id") final UUID uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(uuid));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<GetAllUsersResponse> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PostMapping("/register")
    @Operation(summary = "Register user")
    @ResponseStatus(HttpStatus.CREATED)
    // no auth filter needed public endpoint
    public ResponseEntity<Void> registerUser(@RequestBody @Valid final UserRegistrationDto userRegistrationDto) {
        userService.registerUser(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/password")
    @Operation(summary = "Update password")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid final UserPasswordUpdateDTO userPasswordUpdateDTO) {
        userService.updatePassword(userPasswordUpdateDTO.getOldPassword(), userPasswordUpdateDTO.getNewPassword(), userPasswordUpdateDTO.getEmail());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD','TENANT')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable @NotEmpty final String email) {
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/documents/{email}")
    @Operation(summary = "Get user documents")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<String>> getDocuments(@Valid @PathVariable @NotEmpty final String email) {
        return new ResponseEntity<>(userService.getUserDocuments(email), HttpStatus.OK);
    }

    @GetMapping(value = "/documents/download/{documentName}", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Download document")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Resource> downloadDocument(@PathVariable(value = "documentName") @NotEmpty String documentName) {
        Resource file = userService.downloadFile(documentName);
        if (file == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        }
    }

    @PostMapping(path = "/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID uploadFile(@RequestParam @NotEmpty String documentType,
                           @Parameter(
                                   description = "Document file",
                                   required = true,
                                   content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
                           )
                           @RequestPart("file") MultipartFile file) throws IOException {
        return userService.uploadDocument(documentType, file);
    }

    @PostMapping(path = "/documents/ownership/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload ownership document as landlord")
    @PreAuthorize("hasRole('LANDLORD')")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID uploadOwnershipDocument(
            @RequestParam UUID propertyId,
            @Parameter(
                    description = "Document file",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            )
            @RequestPart("file") MultipartFile file) throws IOException {
        return userService.uploadOwnershipDocument(file, propertyId);
    }


    @DeleteMapping("/documents/{documentName}")
    @Operation(summary = "Delete document")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteDocument(@PathVariable(value = "documentName") @NotEmpty String documentName) {
        userService.deleteDocument(documentName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/verify/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify user identity as admin")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> verifyUserIdentity(@NotEmpty @PathVariable String email) {
        userService.verifyUser(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/unverify/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark user as not verified")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> markUserAsNotVerified(@NotEmpty @PathVariable String email) {
        userService.markUserAsNotVerified(email);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/admin/activate/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate user account status as admin")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> activateUserAccountStatus(@NotEmpty @PathVariable String email) {
        userService.activateUser(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/register")
    @Operation(summary = "Register admin user as admin")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createAdminUser(@RequestBody @Valid final CreateAdminDTO createAdminDTO) {
        userService.createAdminUser(createAdminDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/activate-account/verify", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Activate user account")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> activateUserAccount(@NotEmpty @RequestParam("token") String confirmationToken) {
        userService.confirmEmail(confirmationToken);
        return ResponseEntity.ok("Email verified successfully!");
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Reset password of the user with the token sent by email to the use")
    public ResponseEntity<String> resetPassword(@NotEmpty @RequestParam("token") String resetToken, @RequestBody PasswordResetDto passwordResetDto) {
        userService.resetPassword(resetToken, passwordResetDto.getNewPassword());
        return ResponseEntity.ok("Password reset successful");
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Send password reset email to the user with the email address provided")
    public ResponseEntity<String> forgotPassword(@NotEmpty @RequestParam("email") String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/block/{email}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Block the user with the email address provided")
    @PreAuthorize("hasRole('ADMIN')")
    public void blockUser(@PathVariable String email) {
        userService.blockUser(email);
    }

    @RequestMapping(path = "/extend-retention", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Extend the data retention period for the user with the provided token")
    public ResponseEntity<String> extendDataRetention(@RequestParam("token") String encodedToken) {
        String token = new String(Base64.getUrlDecoder().decode(encodedToken));
        userService.extendDataRetention(token);
        return ResponseEntity.ok("Data retention period extended successfully");
    }

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Submit a request to the admin for a complaint or a query")
    public ResponseEntity<String> submitRequest(@Valid @RequestBody RequestFormDTO requestFormDTO) {
        userService.submitRequest(requestFormDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Request form is submitted successfully");
    }

    @GetMapping("/documents/{userId}/property/{propertyId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user documents by property")
    public ResponseEntity<String> getUserDocumentsByProperty(
            @PathVariable UUID userId,
            @PathVariable UUID propertyId
    ) {
        String documentFilename = userService.getUserDocumentsByProperty(userId, propertyId);
        return ResponseEntity.ok(documentFilename);
    }

}

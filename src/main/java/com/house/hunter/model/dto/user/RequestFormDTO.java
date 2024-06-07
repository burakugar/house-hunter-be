package com.house.hunter.model.dto.user;

import com.house.hunter.constant.RequestFormSubject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestFormDTO {
    @NotEmpty(message = "Name is required")
    private String name;
    @NotEmpty(message = "Email is required")
    private String email;
    @NotEmpty(message = "Furnished info cannot be empty")
    @Pattern(regexp = RequestFormSubject.PATTERN, message = "Invalid subject value")
    private String subject;
    @NotEmpty(message = "Message is required")
    private String message;
    private String propertyId;
}

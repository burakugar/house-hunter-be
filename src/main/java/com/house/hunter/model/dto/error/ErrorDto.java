package com.house.hunter.model.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorDto {
    private final int status;
    private final String message;
    private final List<String> details;
}

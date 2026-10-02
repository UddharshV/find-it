package com.uddharsh.findit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClaimRequest(
        @NotBlank @Size(max = 1000) String message) {}
package com.tcrs_app.tcrs_app.payload.request;

import com.tcrs_app.tcrs_app.enums.AppUserRole;
import com.tcrs_app.tcrs_app.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "firstname is required")
    private String firstname;
    @NotBlank(message = "lastname is required")
    private String lastname;
    @NotBlank(message = "email is required")
    @Email(message = "email format is not valid")
    private String email;
    @NotBlank(message = "password is required")
    @StrongPassword
    private String password;
    @NotNull
    private AppUserRole role;
}

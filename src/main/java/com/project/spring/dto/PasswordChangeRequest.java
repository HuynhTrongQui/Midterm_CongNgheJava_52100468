package com.project.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    @NotBlank(message = "Password not provided!")
    private String passwordOld;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$", message = """
            Password in valid! \n
            At least 8 characters in length. \n
            At most 32 characters in length. \n
            At least one lowercase letter. \n
            At least one uppercase letter. \n
            At least one digit. \n
            At least one special character from @$!%*?&""")
    private String passwordNew;
	public String getPasswordOld() {
		return passwordOld;
	}
	public void setPasswordOld(String passwordOld) {
		this.passwordOld = passwordOld;
	}
	public String getPasswordNew() {
		return passwordNew;
	}
	public void setPasswordNew(String passwordNew) {
		this.passwordNew = passwordNew;
	}
}

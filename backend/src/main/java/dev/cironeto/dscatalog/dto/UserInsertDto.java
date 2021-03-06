package dev.cironeto.dscatalog.dto;

import dev.cironeto.dscatalog.service.validation.UserInsertValid;

@UserInsertValid
public class UserInsertDto extends UserDto {
    private static final long serialVersionUID = 1L;

    private String password;

    public UserInsertDto() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

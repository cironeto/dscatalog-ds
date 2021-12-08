package dev.cironeto.dscatalog.dto;

public class UserInsertionDto extends UserDto {
    private static final long serialVersionUID = 1L;

    private String password;

    public UserInsertionDto() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

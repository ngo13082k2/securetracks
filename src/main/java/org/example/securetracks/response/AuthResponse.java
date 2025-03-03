package org.example.securetracks.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.securetracks.model.enums.Role;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
    private String email;
    private String phone;
    private String userName;
    private Role role;
    private String fullName;
    private String token;
    public AuthResponse(String email, String phone, String userName, Role role, String fullName, String token) {
        this.email = email;
        this.phone = phone;
        this.userName = userName;
        this.role = role;
        this.fullName = fullName;
        this.token = token;
    }


}

package shopcart.com.example.shopcart.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequestDTO {
    private String name;
    private String email;
    private String password;
}

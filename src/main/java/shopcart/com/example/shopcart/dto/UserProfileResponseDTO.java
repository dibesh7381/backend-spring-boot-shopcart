package shopcart.com.example.shopcart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponseDTO {
    private String id;
    private String name;
    private String email;
    private String role;
}



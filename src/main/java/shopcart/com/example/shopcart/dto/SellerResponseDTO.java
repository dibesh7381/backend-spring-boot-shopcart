package shopcart.com.example.shopcart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellerResponseDTO {
    private String id;
    private String userId;
    private String shopName;
    private String shopAddress;
    private String shopType;
    private String shopPhotoUrl;
}

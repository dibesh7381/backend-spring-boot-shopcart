package shopcart.com.example.shopcart.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SellerRequestDTO {
    private String shopName;
    private String shopAddress;
    private String shopType;
    private MultipartFile shopImage;
}


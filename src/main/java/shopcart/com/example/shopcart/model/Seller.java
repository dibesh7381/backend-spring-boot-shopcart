package shopcart.com.example.shopcart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sellers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seller {
    @Id
    private String id;
    private String userId;
    private String shopName;
    private String shopAddress;
    private String shopType;
    private String shopPhotoUrl;
}


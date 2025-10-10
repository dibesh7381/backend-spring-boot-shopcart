package shopcart.com.example.shopcart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;
    private String userId;
    private String brand;
    private String model;       // optional
    private String color;
    private double price;
    private String productType; // Shirts, Jeans, Shoes, etc.
    private String shopType;    // Mobile Seller, Shoes Seller etc.
    private String imageUrl;
    private String category;    // Male/Female/Kids
    private String size;        // M, L, XL
    private Integer quantity;   // default 1

    // ✅ Extra field for shoes
    private String shoeSize;    // 6, 7, 8, 9, etc.
}


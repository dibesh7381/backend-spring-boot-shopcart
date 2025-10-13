package shopcart.com.example.shopcart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponseDTO {
    private String id;
    private String userId;
    private String brand;
    private String model;
    private String color;
    private Double price;
    private String productType;
    private Integer quantity;
    private String imageUrl;
    private String shopType;

    // ✅ Extra fields
    private String category;   // Male/Female/Kids
    private String size;       // M, L, XL
    private String shoeSize;   // 6, 7, 8, 9, etc.
}


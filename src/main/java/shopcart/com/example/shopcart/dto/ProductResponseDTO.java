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
}

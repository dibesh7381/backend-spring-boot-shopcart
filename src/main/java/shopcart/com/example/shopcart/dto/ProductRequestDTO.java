package shopcart.com.example.shopcart.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductRequestDTO {
    private String brand;
    private String model;
    private String color;
    private Double price;
    private String productType;
    private Integer quantity;
    private MultipartFile image;
}

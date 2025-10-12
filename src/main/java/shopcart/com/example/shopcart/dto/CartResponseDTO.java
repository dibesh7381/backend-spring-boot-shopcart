package shopcart.com.example.shopcart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponseDTO {
    private String id;
    private String userId;
    private ProductResponseDTO product;
    private Integer quantity;
}

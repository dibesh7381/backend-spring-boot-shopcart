package shopcart.com.example.shopcart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor      // Generates no-arg constructor
@AllArgsConstructor     // Generates all-args constructor
public class ApiResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
}
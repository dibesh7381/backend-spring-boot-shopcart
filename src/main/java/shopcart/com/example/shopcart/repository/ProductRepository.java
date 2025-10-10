package shopcart.com.example.shopcart.repository;

import shopcart.com.example.shopcart.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByUserId(String userId); // <-- exact field name 'userId'
}


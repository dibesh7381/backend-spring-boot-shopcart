package shopcart.com.example.shopcart.repository;

import shopcart.com.example.shopcart.model.Seller;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SellerRepository extends MongoRepository<Seller, String> {
    Optional<Seller> findByUserId(String userId);
}

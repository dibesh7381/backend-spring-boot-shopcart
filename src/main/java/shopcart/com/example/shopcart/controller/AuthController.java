package shopcart.com.example.shopcart.controller;

import shopcart.com.example.shopcart.model.User;
import shopcart.com.example.shopcart.model.Product;
import shopcart.com.example.shopcart.model.Seller;
import shopcart.com.example.shopcart.repository.ProductRepository;
import shopcart.com.example.shopcart.security.JwtUtil;
import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shopcart.com.example.shopcart.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*") // React frontend se fetch ke liye
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;

    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil, ProductRepository productRepository, Cloudinary cloudinary) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.productRepository = productRepository;
        this.cloudinary = cloudinary;
    }

    // ---------------- Utility Response Builder ----------------
    private ResponseEntity<?> buildResponse(boolean success, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    // ---------------- Signup ----------------
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            authService.saveUser(user);
            return buildResponse(true, "User registered successfully! Please login.", null);
        } catch (Exception e) {
            return buildResponse(false, "Signup failed!", e.getMessage());
        }
    }

    // ---------------- Login ----------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            String token = authService.login(user.getEmail(), user.getPassword());
            User currentUser = authService.getProfile(user.getEmail());
            currentUser.setPassword(null);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", currentUser);

            return buildResponse(true, "Login successful!", data);
        } catch (Exception e) {
            return buildResponse(false, "Login failed!", e.getMessage());
        }
    }

    // ---------------- Get Profile ----------------
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            User user = authService.getProfile(email);
            user.setPassword(null);
            return buildResponse(true, "Profile fetched successfully!", user);
        } catch (Exception e) {
            return buildResponse(false, "Unauthorized access!", null);
        }
    }

    // ---------------- Update Profile ----------------
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody User updatedUserData
    ) {
        try {
            String currentEmail = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            User updatedUser = authService.updateProfile(currentEmail, updatedUserData);
            updatedUser.setPassword(null);
            return buildResponse(true, "Profile updated successfully!", updatedUser);
        } catch (Exception e) {
            return buildResponse(false, "Failed to update profile!", e.getMessage());
        }
    }

    // ---------------- Become Seller ----------------
    @PutMapping("/become-seller")
    public ResponseEntity<?> becomeSeller(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam("shopName") String shopName,
            @RequestParam("shopAddress") String shopAddress,
            @RequestParam("shopType") String shopType,
            @RequestParam("shopImage") MultipartFile shopImage
    ) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            User user = authService.becomeSeller(email, shopName, shopAddress, shopType, shopImage);
            user.setPassword(null);
            return buildResponse(true, "🎉 You are now a seller!", user);
        } catch (Exception e) {
            return buildResponse(false, "Failed to become a seller!", e.getMessage());
        }
    }

    // ---------------- Add Product ----------------
    @PostMapping("/add-product")
    public ResponseEntity<?> addProduct(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String color,
            @RequestParam Double price,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) MultipartFile image
    ) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            Product product = new Product();
            product.setBrand(brand);
            product.setModel(model);
            product.setColor(color);
            product.setPrice(price);
            product.setProductType(productType);
            product.setQuantity(quantity != null ? quantity : 1);

            Product savedProduct = authService.addProduct(email, brand, model, color, price, productType, image);
            return buildResponse(true, "Product added successfully!", savedProduct);

        } catch (Exception e) {
            return buildResponse(false, "Failed to add product!", e.getMessage());
        }
    }

    // ---------------- Get Seller's Products ----------------
    @GetMapping("/my-products")
    public ResponseEntity<?> getMyProducts(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            User user = authService.getProfile(email);

            if (!"seller".equals(user.getRole())) {
                return buildResponse(false, "You are not a seller yet!", null);
            }

            var products = authService.getProductsBySellerId(user.getId());
            return buildResponse(true, "Seller products fetched successfully!", products);
        } catch (Exception e) {
            return buildResponse(false, "Failed to fetch products!", e.getMessage());
        }
    }

    // ---------------- Update Product ----------------
    @PutMapping("/update-product/{productId}")
    public ResponseEntity<?> updateProduct(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable String productId,
            @RequestBody Product updatedProduct
    ) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            User user = authService.getProfile(email);

            if (!"seller".equals(user.getRole())) {
                return buildResponse(false, "Only sellers can update products!", null);
            }

            Product savedProduct = authService.updateProduct(user.getId(), productId, updatedProduct);
            return buildResponse(true, "Product updated successfully!", savedProduct);

        } catch (Exception e) {
            return buildResponse(false, "Failed to update product!", e.getMessage());
        }
    }

    // ---------------- Delete Product ----------------
    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<?> deleteProduct(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable String productId
    ) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            User user = authService.getProfile(email);

            if (!"seller".equals(user.getRole())) {
                return buildResponse(false, "Only sellers can delete products!", null);
            }

            authService.deleteProduct(user.getId(), productId);
            return buildResponse(true, "Product deleted successfully!", null);
        } catch (Exception e) {
            return buildResponse(false, "Failed to delete product!", e.getMessage());
        }
    }

    // ---------------- Get All Products ----------------
    @GetMapping("/all-products")
    public ResponseEntity<?> getAllProducts(@RequestHeader("Authorization") String tokenHeader) {
        try {
            var products = productRepository.findAll();
            return buildResponse(true, "All products fetched successfully!", products);
        } catch (Exception e) {
            return buildResponse(false, "Failed to fetch products!", e.getMessage());
        }
    }

    // ---------------- Get My Shop ----------------
    @GetMapping("/my-shop")
    public ResponseEntity<?> getMyShop(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            User user = authService.getProfile(email);

            if (!"seller".equals(user.getRole())) {
                return buildResponse(false, "You are not a seller yet!", null);
            }

            Seller seller = authService.getSellerByUserId(user.getId());
            return buildResponse(true, "Seller details fetched successfully!", seller);
        } catch (Exception e) {
            return buildResponse(false, "Failed to fetch shop details!", e.getMessage());
        }
    }

    // ---------------- Static Pages ----------------
    @GetMapping("/home")
    public ResponseEntity<?> homePage() {
        return buildResponse(true, "Home Page", "Welcome to AuthApp Home Page!");
    }

    @GetMapping("/about")
    public ResponseEntity<?> aboutPage() {
        return buildResponse(true, "About Page", "This project is an E-commerce demo built using Spring Boot + React.");
    }
}
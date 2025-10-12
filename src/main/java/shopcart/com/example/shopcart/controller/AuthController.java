//package shopcart.com.example.shopcart.controller;
//
//import shopcart.com.example.shopcart.dto.*;
//import shopcart.com.example.shopcart.model.*;
//import shopcart.com.example.shopcart.repository.ProductRepository;
//import shopcart.com.example.shopcart.security.JwtUtil;
//import shopcart.com.example.shopcart.service.AuthService;
//import com.cloudinary.Cloudinary;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/auth")
//@CrossOrigin("*")
//public class AuthController {
//
//    private final AuthService authService;
//    private final JwtUtil jwtUtil;
//    private final ProductRepository productRepository;
//    private final Cloudinary cloudinary;
//
//    @Autowired
//    public AuthController(AuthService authService, JwtUtil jwtUtil, ProductRepository productRepository, Cloudinary cloudinary) {
//        this.authService = authService;
//        this.jwtUtil = jwtUtil;
//        this.productRepository = productRepository;
//        this.cloudinary = cloudinary;
//    }
//
//    // ---------------- Signup ----------------
//    @PostMapping("/signup")
//    public ResponseEntity<ApiResponseDTO<?>> signup(@RequestBody UserSignupRequestDTO userDto) {
//        try {
//            authService.saveUser(userDto);
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "User registered successfully! Please login.", null));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Signup failed!", e.getMessage()));
//        }
//    }
//
//    // ---------------- Login ----------------
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponseDTO<?>> login(@RequestBody UserLoginRequestDTO loginDto) {
//        try {
//            LoginResponseDTO response = authService.login(loginDto);
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Login successful!", response));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Login failed!", e.getMessage()));
//        }
//    }
//
//    // ---------------- Get Profile ----------------
//    @GetMapping("/profile")
//    public ResponseEntity<ApiResponseDTO<?>> getProfile(@RequestHeader("Authorization") String tokenHeader) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            UserProfileResponseDTO profile = authService.getProfile(email);
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Profile fetched successfully!", profile));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Unauthorized access!", null));
//        }
//    }
//
//    // ---------------- Update Profile ----------------
//    @PutMapping("/profile")
//    public ResponseEntity<ApiResponseDTO<?>> updateProfile(
//            @RequestHeader("Authorization") String tokenHeader,
//            @RequestBody UserProfileUpdateRequestDTO updatedUserData
//    ) {
//        try {
//            String currentEmail = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            UserProfileResponseDTO updatedProfile = authService.updateProfile(currentEmail, updatedUserData);
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Profile updated successfully!", updatedProfile));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to update profile!", e.getMessage()));
//        }
//    }
//
//
//    // ---------------- Become Seller ----------------
//    @PutMapping("/become-seller")
//    public ResponseEntity<ApiResponseDTO<?>> becomeSeller(
//            @RequestHeader("Authorization") String tokenHeader,
//            @RequestParam("shopName") String shopName,
//            @RequestParam("shopAddress") String shopAddress,
//            @RequestParam("shopType") String shopType,
//            @RequestParam("shopImage") MultipartFile shopImage
//    ) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            User user = authService.becomeSeller(email, shopName, shopAddress, shopType, shopImage);
//            user.setPassword(null);
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "🎉 You are now a seller!", user));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to become a seller!", e.getMessage()));
//        }
//    }
//
//    // ---------------- Add Product ----------------
//    @PostMapping("/add-product")
//    public ResponseEntity<ApiResponseDTO<?>> addProduct(
//            @RequestHeader("Authorization") String tokenHeader,
//            @RequestParam(required = false) String brand,
//            @RequestParam(required = false) String model,
//            @RequestParam(required = false) String color,
//            @RequestParam Double price,
//            @RequestParam(required = false) String productType,
//            @RequestParam(required = false) Integer quantity,
//            @RequestParam(required = false) MultipartFile image
//    ) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            Product savedProduct = authService.addProduct(email, brand, model, color, price, productType, quantity, image);
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Product added successfully!", savedProduct));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to add product!", e.getMessage()));
//        }
//    }
//
//    // ---------------- My Products ----------------
//    @GetMapping("/my-products")
//    public ResponseEntity<ApiResponseDTO<?>> getMyProducts(@RequestHeader("Authorization") String tokenHeader) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            User user = authService.getProfile(email);
//
//            if (!"seller".equals(user.getRole())) {
//                return ResponseEntity.ok(new ApiResponseDTO<>(false, "You are not a seller yet!", null));
//            }
//
//            var products = authService.getProductsBySellerId(user.getId());
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Seller products fetched successfully!", products));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to fetch products!", e.getMessage()));
//        }
//    }
//
//    // ---------------- Update Product ----------------
//    @PutMapping("/update-product/{productId}")
//    public ResponseEntity<ApiResponseDTO<?>> updateProduct(
//            @RequestHeader("Authorization") String tokenHeader,
//            @PathVariable String productId,
//            @RequestParam(required = false) String brand,
//            @RequestParam(required = false) String model,
//            @RequestParam(required = false) String color,
//            @RequestParam(required = false) Double price,
//            @RequestParam(required = false) String productType,
//            @RequestParam(required = false) Integer quantity,
//            @RequestParam(required = false) MultipartFile image
//    ) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            User user = authService.getProfile(email);
//
//            if (!"seller".equals(user.getRole())) {
//                return ResponseEntity.ok(new ApiResponseDTO<>(false, "Only sellers can update products!", null));
//            }
//
//            Product savedProduct = authService.updateProductWithFormData(
//                    user.getId(),
//                    productId,
//                    brand,
//                    model,
//                    color,
//                    price,
//                    productType,
//                    quantity,
//                    image
//            );
//
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Product updated successfully!", savedProduct));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to update product!", e.getMessage()));
//        }
//    }
//
//    // ---------------- Delete Product ----------------
//    @DeleteMapping("/delete-product/{productId}")
//    public ResponseEntity<ApiResponseDTO<?>> deleteProduct(
//            @RequestHeader("Authorization") String tokenHeader,
//            @PathVariable String productId
//    ) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            User user = authService.getProfile(email);
//
//            if (!"seller".equals(user.getRole())) {
//                return ResponseEntity.ok(new ApiResponseDTO<>(false, "Only sellers can delete products!", null));
//            }
//
//            authService.deleteProduct(user.getId(), productId);
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Product deleted successfully!", null));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to delete product!", e.getMessage()));
//        }
//    }
//
//    // ---------------- All Products ----------------
//    @GetMapping("/all-products")
//    public ResponseEntity<ApiResponseDTO<?>> getAllProducts() {
//        try {
//            var products = productRepository.findAll();
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "All products fetched successfully!", products));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to fetch products!", e.getMessage()));
//        }
//    }
//
//    // ---------------- My Shop ----------------
//    @GetMapping("/my-shop")
//    public ResponseEntity<ApiResponseDTO<?>> getMyShop(@RequestHeader("Authorization") String tokenHeader) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            User user = authService.getProfile(email);
//
//            if (!"seller".equals(user.getRole())) {
//                return ResponseEntity.ok(new ApiResponseDTO<>(false, "You are not a seller yet!", null));
//            }
//
//            Seller seller = authService.getSellerByUserId(user.getId());
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Seller details fetched successfully!", seller));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to fetch shop details!", e.getMessage()));
//        }
//    }
//
//    // ---------------- CART ENDPOINTS ----------------
//    @PostMapping("/cart/add/{productId}")
//    public ResponseEntity<ApiResponseDTO<?>> addToCart(
//            @RequestHeader("Authorization") String tokenHeader,
//            @PathVariable String productId
//    ) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            User user = authService.getProfile(email);
//
//            CartItem item = authService.addToCart(user.getId(), productId);
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Product added to cart!", item));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to add to cart!", e.getMessage()));
//        }
//    }
//
//    @PutMapping("/cart/increase/{productId}")
//    public ResponseEntity<ApiResponseDTO<?>> increaseCartQuantity(
//            @RequestHeader("Authorization") String tokenHeader,
//            @PathVariable String productId
//    ) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            User user = authService.getProfile(email);
//
//            CartItem item = authService.increaseCartQuantity(user.getId(), productId);
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Cart quantity increased!", item));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to increase cart quantity!", e.getMessage()));
//        }
//    }
//
//    @PutMapping("/cart/decrease/{productId}")
//    public ResponseEntity<ApiResponseDTO<?>> decreaseCartQuantity(
//            @RequestHeader("Authorization") String tokenHeader,
//            @PathVariable String productId
//    ) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            User user = authService.getProfile(email);
//
//            CartItem item = authService.decreaseCartQuantity(user.getId(), productId);
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Cart quantity decreased!", item));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to decrease cart quantity!", e.getMessage()));
//        }
//    }
//
//    @GetMapping("/cart")
//    public ResponseEntity<ApiResponseDTO<?>> getCartItems(@RequestHeader("Authorization") String tokenHeader) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            User user = authService.getProfile(email);
//
//            List<Map<String, Object>> cartItems = authService.getCartItems(user.getId());
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Cart items fetched!", cartItems));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to fetch cart items!", e.getMessage()));
//        }
//    }
//
//    @DeleteMapping("/cart/remove/{productId}")
//    public ResponseEntity<ApiResponseDTO<?>> removeFromCart(
//            @RequestHeader("Authorization") String tokenHeader,
//            @PathVariable String productId
//    ) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            User user = authService.getProfile(email);
//
//            authService.removeFromCart(user.getId(), productId);
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Product removed from cart!", null));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to remove from cart!", e.getMessage()));
//        }
//    }
//
//    @DeleteMapping("/cart/clear")
//    public ResponseEntity<ApiResponseDTO<?>> clearCart(@RequestHeader("Authorization") String tokenHeader) {
//        try {
//            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
//            User user = authService.getProfile(email);
//
//            authService.clearCart(user.getId());
//            return ResponseEntity.ok(new ApiResponseDTO<>(true, "All products removed from cart!", null));
//        } catch (Exception e) {
//            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to clear cart!", e.getMessage()));
//        }
//    }
//
//    // ---------------- Static Pages ----------------
//    @GetMapping("/home")
//    public ResponseEntity<ApiResponseDTO<?>> homePage() {
//        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Home Page", "Welcome to AuthApp Home Page!"));
//    }
//
//    @GetMapping("/about")
//    public ResponseEntity<ApiResponseDTO<?>> aboutPage() {
//        return ResponseEntity.ok(new ApiResponseDTO<>(true, "About Page", "This project is an E-commerce demo built using Spring Boot + React."));
//    }
//}

package shopcart.com.example.shopcart.controller;

import shopcart.com.example.shopcart.dto.*;
import shopcart.com.example.shopcart.model.User;
import shopcart.com.example.shopcart.repository.ProductRepository;
import shopcart.com.example.shopcart.security.JwtUtil;
import shopcart.com.example.shopcart.service.AuthService;
import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
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

    // ---------------- Signup ----------------
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDTO<?>> signup(@RequestBody UserSignupRequestDTO userDto) {
        try {
            UserResponseDTO savedUser = authService.saveUser(userDto);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "User registered successfully! Please login.", savedUser));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Signup failed!", e.getMessage()));
        }
    }

    // ---------------- Login ----------------
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<?>> login(@RequestBody UserLoginRequestDTO loginDto) {
        try {
            LoginResponseDTO response = authService.login(loginDto);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Login successful!", response));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Login failed!", e.getMessage()));
        }
    }

    // ---------------- Get Profile ----------------
    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDTO<?>> getProfile(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            UserProfileResponseDTO profile = authService.getProfile(email);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Profile fetched successfully!", profile));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Unauthorized access!", null));
        }
    }

    // ---------------- Update Profile ----------------
    @PutMapping("/profile")
    public ResponseEntity<ApiResponseDTO<?>> updateProfile(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody UserProfileUpdateRequestDTO updatedUserData
    ) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            UserProfileResponseDTO updatedProfile = authService.updateProfile(email, updatedUserData);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Profile updated successfully!", updatedProfile));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to update profile!", e.getMessage()));
        }
    }

    // ---------------- Become Seller ----------------
    @PutMapping("/become-seller")
    public ResponseEntity<ApiResponseDTO<?>> becomeSeller(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam("shopName") String shopName,
            @RequestParam("shopAddress") String shopAddress,
            @RequestParam("shopType") String shopType,
            @RequestParam("shopImage") MultipartFile shopImage
    ) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            SellerResponseDTO seller = authService.becomeSeller(email, shopName, shopAddress, shopType, shopImage);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "🎉 You are now a seller!", seller));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to become a seller!", e.getMessage()));
        }
    }

    // ---------------- Add Product ----------------
    @PostMapping("/add-product")
    public ResponseEntity<ApiResponseDTO<?>> addProduct(
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
            ProductResponseDTO savedProduct = authService.addProduct(email, brand, model, color, price, productType, quantity, image);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Product added successfully!", savedProduct));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to add product!", e.getMessage()));
        }
    }

    // ---------------- My Products ----------------
    @GetMapping("/my-products")
    public ResponseEntity<ApiResponseDTO<?>> getMyProducts(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            UserProfileResponseDTO user = authService.getProfile(email);

            if (!"seller".equals(user.getRole())) {
                return ResponseEntity.ok(new ApiResponseDTO<>(false, "You are not a seller yet!", null));
            }

            List<ProductResponseDTO> products = authService.getProductsBySellerId(user.getId());
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Seller products fetched successfully!", products));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to fetch products!", e.getMessage()));
        }
    }

    // ---------------- Update Product ----------------
    @PutMapping("/update-product/{productId}")
    public ResponseEntity<ApiResponseDTO<?>> updateProduct(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable String productId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) MultipartFile image
    ) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            UserProfileResponseDTO user = authService.getProfile(email);


            if (!"seller".equals(user.getRole())) {
                return ResponseEntity.ok(new ApiResponseDTO<>(false, "Only sellers can update products!", null));
            }

            ProductResponseDTO savedProduct = authService.updateProductWithFormData(user.getId(), productId, brand, model, color, price, productType, quantity, image);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Product updated successfully!", savedProduct));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to update product!", e.getMessage()));
        }
    }

    // ---------------- Delete Product ----------------
    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<ApiResponseDTO<?>> deleteProduct(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable String productId
    ) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            UserProfileResponseDTO user = authService.getProfile(email);


            if (!"seller".equals(user.getRole())) {
                return ResponseEntity.ok(new ApiResponseDTO<>(false, "Only sellers can delete products!", null));
            }

            authService.deleteProduct(user.getId(), productId);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Product deleted successfully!", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to delete product!", e.getMessage()));
        }
    }

    // ---------------- Get Cart Items ----------------
    @GetMapping("/cart")
    public ResponseEntity<ApiResponseDTO<?>> getCartItems(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            UserProfileResponseDTO user = authService.getProfile(email);


            List<CartResponseDTO> cartItems = authService.getCartItems(user.getId());
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Cart items fetched!", cartItems));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to fetch cart items!", e.getMessage()));
        }
    }

    // ---------------- Add To Cart ----------------
    @PostMapping("/cart/add/{productId}")
    public ResponseEntity<ApiResponseDTO<?>> addToCart(@RequestHeader("Authorization") String tokenHeader, @PathVariable String productId) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            UserProfileResponseDTO user = authService.getProfile(email);


            CartResponseDTO item = authService.addToCart(user.getId(), productId);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Product added to cart!", item));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to add to cart!", e.getMessage()));
        }
    }

    // ---------------- Increase/Decrease Cart Quantity ----------------
    @PutMapping("/cart/increase/{productId}")
    public ResponseEntity<ApiResponseDTO<?>> increaseCartQuantity(@RequestHeader("Authorization") String tokenHeader, @PathVariable String productId) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            UserProfileResponseDTO user = authService.getProfile(email);


            CartResponseDTO item = authService.increaseCartQuantity(user.getId(), productId);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Cart quantity increased!", item));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to increase cart quantity!", e.getMessage()));
        }
    }

    @PutMapping("/cart/decrease/{productId}")
    public ResponseEntity<ApiResponseDTO<?>> decreaseCartQuantity(@RequestHeader("Authorization") String tokenHeader, @PathVariable String productId) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            UserProfileResponseDTO user = authService.getProfile(email);


            CartResponseDTO item = authService.decreaseCartQuantity(user.getId(), productId);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Cart quantity decreased!", item));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to decrease cart quantity!", e.getMessage()));
        }
    }

    // ---------------- Remove From Cart ----------------
    @DeleteMapping("/cart/remove/{productId}")
    public ResponseEntity<ApiResponseDTO<?>> removeFromCart(@RequestHeader("Authorization") String tokenHeader, @PathVariable String productId) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            UserProfileResponseDTO user = authService.getProfile(email);


            authService.removeFromCart(user.getId(), productId);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Product removed from cart!", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to remove from cart!", e.getMessage()));
        }
    }

    // ---------------- Clear Cart ----------------
    @DeleteMapping("/cart/clear")
    public ResponseEntity<ApiResponseDTO<?>> clearCart(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String email = jwtUtil.getEmailFromToken(tokenHeader.replace("Bearer ", ""));
            UserProfileResponseDTO userProfile = authService.getProfile(email); // DTO
            authService.clearCart(userProfile.getId());                          // DTO se id pass karo

            return ResponseEntity.ok(new ApiResponseDTO<>(true, "All products removed from cart!", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponseDTO<>(false, "Failed to clear cart!", e.getMessage()));
        }
    }

//     ---------------- Static Pages ----------------
    @GetMapping("/home")
    public ResponseEntity<ApiResponseDTO<?>> homePage() {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Home Page", "Welcome to AuthApp Home Page!"));
    }

    @GetMapping("/about")
    public ResponseEntity<ApiResponseDTO<?>> aboutPage() {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "About Page", "This project is an E-commerce demo built using Spring Boot + React."));
    }
}

package shopcart.com.example.shopcart.service;

import shopcart.com.example.shopcart.model.User;
import shopcart.com.example.shopcart.model.Product;
import shopcart.com.example.shopcart.model.Seller;
import shopcart.com.example.shopcart.model.CartItem;
import shopcart.com.example.shopcart.repository.ProductRepository;
import shopcart.com.example.shopcart.repository.SellerRepository;
import shopcart.com.example.shopcart.repository.UserRepository;
import shopcart.com.example.shopcart.repository.CartRepository;
import shopcart.com.example.shopcart.security.JwtUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Cloudinary cloudinary;

    @Autowired
    public AuthService(UserRepository userRepository,
                       SellerRepository sellerRepository,
                       ProductRepository productRepository,
                       CartRepository cartRepository,
                       JwtUtil jwtUtil,
                       Cloudinary cloudinary) {
        this.userRepository = userRepository;
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.jwtUtil = jwtUtil;
        this.cloudinary = cloudinary;
    }

    // ---------------- User Methods ----------------
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtUtil.generateToken(user.getEmail());
    }

    public User getProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateProfile(String currentEmail, User updatedUserData) {
        User user = getProfile(currentEmail);
        if (updatedUserData.getName() != null && !updatedUserData.getName().isBlank()) {
            user.setName(updatedUserData.getName());
        }
        if (updatedUserData.getEmail() != null && !updatedUserData.getEmail().isBlank()) {
            user.setEmail(updatedUserData.getEmail());
        }
        if (updatedUserData.getPassword() != null && !updatedUserData.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updatedUserData.getPassword()));
        }
        return userRepository.save(user);
    }

    // ---------------- Seller Methods ----------------
    public User becomeSeller(String email, String shopName, String shopAddress, String shopType, MultipartFile shopImage) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("seller".equals(user.getRole())) {
            throw new RuntimeException("User is already a seller");
        }

        String imageUrl = null;
        if (shopImage != null && !shopImage.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(shopImage.getBytes(),
                        ObjectUtils.asMap("folder", "shop_photos"));
                imageUrl = (String) uploadResult.get("secure_url");
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload shop image to Cloudinary");
            }
        }

        user.setRole("seller");
        userRepository.save(user);

        Seller seller = new Seller();
        seller.setUserId(user.getId());
        seller.setShopName(shopName);
        seller.setShopAddress(shopAddress);
        seller.setShopType(shopType);
        seller.setShopPhotoUrl(imageUrl);
        sellerRepository.save(seller);

        return user;
    }

    public Seller getSellerByUserId(String userId) {
        return sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
    }

    // ---------------- Product Methods ----------------
    public Product addProduct(String email, String brand, String model, String color, double price, String productType, MultipartFile image) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"seller".equals(user.getRole())) {
            throw new RuntimeException("Only sellers can add products!");
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(),
                        ObjectUtils.asMap("folder", "product_images"));
                imageUrl = (String) uploadResult.get("secure_url");
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload product image to Cloudinary");
            }
        }

        Product product = new Product();
        product.setUserId(user.getId());
        product.setBrand(brand);
        product.setModel(model);
        product.setColor(color);
        product.setPrice(price);
        product.setProductType(productType);
        product.setImageUrl(imageUrl);
        product.setQuantity(1); // default quantity 1

        return productRepository.save(product);
    }

    public List<Product> getProductsBySellerId(String userId) {
        return productRepository.findByUserId(userId);
    }

    public Product updateProduct(String userId, String productId, Product updatedProduct) {
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this product");
        }

        if (updatedProduct.getBrand() != null) existing.setBrand(updatedProduct.getBrand());
        if (updatedProduct.getModel() != null) existing.setModel(updatedProduct.getModel());
        if (updatedProduct.getColor() != null) existing.setColor(updatedProduct.getColor());
        if (updatedProduct.getPrice() != 0) existing.setPrice(updatedProduct.getPrice());
        if (updatedProduct.getQuantity() != null) existing.setQuantity(updatedProduct.getQuantity());
        if (updatedProduct.getProductType() != null) existing.setProductType(updatedProduct.getProductType());

        return productRepository.save(existing);
    }

    public void deleteProduct(String userId, String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getUserId().equals(userId)) {
            throw new RuntimeException("You cannot delete someone else's product!");
        }

        productRepository.delete(product);
    }

    // ---------------- Encode Password ----------------
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // ---------------- CART METHODS ----------------

    public CartItem addToCart(String userId, String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getQuantity() <= 0) throw new RuntimeException("Product out of stock");

        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElse(new CartItem(null, userId, productId, 0));

        cartItem.setQuantity(cartItem.getQuantity() + 1);
        product.setQuantity(product.getQuantity() - 1);

        productRepository.save(product);
        return cartRepository.save(cartItem);
    }

    public CartItem increaseCartQuantity(String userId, String productId) {
        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() <= 0) throw new RuntimeException("No more stock available");

        cartItem.setQuantity(cartItem.getQuantity() + 1);
        product.setQuantity(product.getQuantity() - 1);

        productRepository.save(product);
        return cartRepository.save(cartItem);
    }

    public CartItem decreaseCartQuantity(String userId, String productId) {
        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        cartItem.setQuantity(cartItem.getQuantity() - 1);
        product.setQuantity(product.getQuantity() + 1);

        if (cartItem.getQuantity() <= 0) {
            cartRepository.delete(cartItem);
            productRepository.save(product);
            return null;
        }

        productRepository.save(product);
        return cartRepository.save(cartItem);
    }

    public List<CartItem> getCartItems(String userId) {
        return cartRepository.findByUserId(userId);
    }

    public void removeFromCart(String userId, String productId) {
        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setQuantity(product.getQuantity() + cartItem.getQuantity());
        productRepository.save(product);

        cartRepository.delete(cartItem);
    }
}

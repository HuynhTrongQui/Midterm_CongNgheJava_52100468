package com.project.spring.api;

import com.project.spring.dto.CartDTO;
import com.project.spring.dto.CartItemDTO;
import com.project.spring.dto.ResponseObject;
import com.project.spring.exceptions.UserNotFoundException;
import com.project.spring.model.Cart;
import com.project.spring.model.CartItem;
import com.project.spring.model.Product;
import com.project.spring.model.AppUser;
import com.project.spring.repositories.CartItemRepository;
import com.project.spring.repositories.CartRepository;
import com.project.spring.repositories.ProductRepository;
import com.project.spring.repositories.UserRepository;
import com.project.spring.service.CartService;
import com.project.spring.service.UserService;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/carts")
public class CartAPI {
    private static final Logger LOGGER = LogManager.getLogger(CartAPI.class);
    @Autowired
    CartService cartService;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    UserService userService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;

    /* @GetMapping("/carts")
     ResponseEntity<ResponseObject> getAllCart() {
         List<Cart> carts = this.cartRepository.findAll();
         List<CartDTO> cartDTOS = carts.stream().map(Cart::toCartDTO).toList();
         return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", cartDTOS));
     }


     @RequestMapping(value = "/carts", method = RequestMethod.POST)
     ResponseEntity<ResponseObject> create(@RequestBody CartDTO cartDTO) throws URISyntaxException {
         User user = userService.findUserById(cartDTO.getUser_Id()).get();
         Cart cart = new Cart();
         cart.setUser(user);
         cartService.saveOrUpdateCart(cart);
         return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", cart.toCartDTO()));
     }
     */

    /* [get] URL: http://localhost:8080/api/carts/user/1*/
    @GetMapping("/user/{idUser}")
    ResponseEntity<ResponseObject> getCartByUser(@PathVariable("idUser") String idUserString) {
        try {
            Long idUser = Long.parseLong(idUserString);
            AppUser user = this.userRepository.findById(idUser).orElse(null);
            if (user == null) {
                throw new UserNotFoundException(idUser);
            }
            List<CartDTO> cartDTOS = this.cartService.getCartDTOByIdUser(idUser);
            if (cartDTOS.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", "User " + idUser + " not have cart.Create~~"));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", cartDTOS));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("0", "Error", "Invalid user ID format"));
        } catch (UserNotFoundException e) {
            // TODO: handle exception
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", e.getMessage()));
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject("0", "Error", "An error occurred."));
        }
    }

    /* [get] URL: http://localhost:8080/api/carts/1*/
    @GetMapping("/{idCart}")
    ResponseEntity<ResponseObject> getCartById(@PathVariable("idCart") String idCartString) {
        try {
            Long idCart = Long.parseLong(idCartString);
            Optional<CartDTO> cartDTO = this.cartService.getCartDTOById(idCart);
            if (cartDTO.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", cartDTO));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("0", "Fail", "Not found cart"));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("0", "Error", "Invalid cart ID format"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("0", "Fail", "Not found cart"));
        }
    }

    /* [POST] URL: http://localhost:8080/api/carts/1/addProduct  */
    @PostMapping("/{idCart}/addProduct")
    ResponseEntity<ResponseObject> addProductToCart(@PathVariable("idCart") Long idCart, @RequestBody CartItemDTO cartItemDTO) {
        Long idProduct = cartItemDTO.getProductId();
        int quantity = cartItemDTO.getQuantity();
        /* Get cart */
        Cart cart = this.cartRepository.findById(idCart).orElse(new Cart());
        /*Get User*/
        AppUser user = cart.getUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("0", "Fail.No found user!", this.cartService.getCartDTOById(idCart)));
        }
        /* Get Product*/
        Product product = this.productRepository.findById(idProduct).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("0", "Fail.No found Product!", this.cartService.getCartDTOById(idCart)));
        }
        /*Check product exist in the cart*/
        Optional<CartItem> cartItem = cart.getCartItems().stream().filter(item -> item.getProduct().getId().equals(idProduct)).findFirst();

        if (cartItem.isPresent()) {
            CartItem item = cartItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cart.getCartItems().add(item);
        }
        /* save to cart*/
        cart.setTotal(cart.getCost());
        cart.setUser(user);
        cartService.saveOrUpdateCart(cart);
        Optional<CartDTO> cartDTO = this.cartService.getCartDTOById(idCart);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Add product to cart success!", cartDTO));
    }

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    ModelMapper modelMapper;

    @PutMapping("/{idCart}")
    ResponseEntity<ResponseObject> updateCart(@RequestBody CartItemDTO cartItemDTO, @PathVariable("idCart") Long idCart) {
        Product product = this.productRepository.findById(cartItemDTO.getProductId()).get();
        int quantity = cartItemDTO.getQuantity();
        Cart cart = this.cartRepository.findById(idCart).get();
        List<CartItem> cartItems = cart.getCartItems();
        boolean found = false;
        for (CartItem item : cartItems) {
            if (product.getId().equals(item.getProduct().getId())) {
                item.setQuantity(quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            CartItem item = new CartItem();
            item.setQuantity(quantity);
            item.setProduct(product);
            item.setCart(cart);
            cartItems.add(item);
        }
        cart.setCartItems(cartItems);
        double total = 0.0;
        for (CartItem cartItem : cartItems) {
            total += cartItem.getQuantity() * cartItem.getProduct().getPrice();
        }
        cart.setTotal(total);
        this.cartRepository.save(cart);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("" + HttpStatus.OK, "Successful", modelMapper.map(cart, CartDTO.class)));
    }

    @Autowired
    CartItemRepository cartItemRepository;

    @DeleteMapping("/{idCart}")
    public ResponseEntity<ResponseObject> deleteProductInCart(@PathVariable("idCart") Long idCart, @RequestBody Long idProduct) {
        Cart cart = this.cartRepository.findById(idCart).get();
        List<CartItem> cartItems = cart.getCartItems();
        double total = cart.getTotal();
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(idProduct)) {
                cartItems.remove(item);
                total = total - item.getProduct().getPrice() * item.getQuantity();
                this.cartItemRepository.delete(item);
                break;
            }
        }
        cart.setTotal(total);
        this.cartRepository.save(cart);

        Cart cartUpdate = this.cartRepository.findById(idCart).get();
        CartDTO cartDTO = modelMapper.map(cartUpdate, CartDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("" + HttpStatus.OK, "Successful", cartDTO));
    }

    @GetMapping("/cookie")
    public ResponseEntity<ResponseObject> readCookie(@CookieValue(name = "cart",required = false,defaultValue = "123") String cart) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("12", "Success", cart));
    }
}

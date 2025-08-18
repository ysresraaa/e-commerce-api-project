package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.exception.InsufficientStockException;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.model.Cart;
import com.ecommerce.e_commerce_api.model.CartItem;
import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.model.Product;
import com.ecommerce.e_commerce_api.repository.ICartItemRepository;
import com.ecommerce.e_commerce_api.repository.ICartRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Locale.filter;

@Service
public class CartService {

    private final ICartRepository cartRepository;
    public final ICartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CustomerService customerService;

    public CartService(ICartRepository cartRepository,ICartItemRepository cartItemRepository,ProductService productService,CustomerService customerService){
        this.cartRepository=cartRepository;
        this.cartItemRepository=cartItemRepository;
        this.productService=productService;
        this.customerService=customerService;
    }

    public Cart  getCartByCustomerId(Long customerId){
        Customer customer=customerService.getCustomerById(customerId);
        return cartRepository.findByCustomer(customer)
                .orElseGet(()->createCartForCustomer(customer));
    }

    private Cart createCartForCustomer(Customer customer){
        Cart newCart=new Cart();
        newCart.setCustomer(customer);
        newCart.setTotalPrice(BigDecimal.ZERO);
        return cartRepository.save(newCart);
    }

    @Transactional
    public Cart addProductToCart(Long customerId ,Long productId,int quantity){
        Cart cart=getCartByCustomerId(customerId);
        Product product = productService.findProductEntityById(productId);

        if(quantity<=0){
            throw new IllegalArgumentException("Quantity must be positive");

        }
        Optional<CartItem>existingItemOptional=cart.getItems().stream()
                .filter(item->item.getProduct().getId().equals(productId))
                .findFirst();
        CartItem cartItem;

        if(existingItemOptional.isPresent()){
            cartItem=existingItemOptional.get();
            int newQuantity=cartItem.getQuantity()+quantity;

            if(newQuantity>product.getStock()){
                throw new InsufficientStockException("Not enough stock for product"+product.getName());

            }
            cartItem.setQuantity(newQuantity);
        }else{
            if(quantity>product.getStock()){
                throw new InsufficientStockException("Not enough stock for product"+ product.getName());
            }
            cartItem=new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        }
        cartItem.setPrice(product.getPrice());
        cartItemRepository.save(cartItem);

        recalculateCartTotalPrice(cart);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeProductFromCart(Long customerId,Long productId){
        Cart cart=getCartByCustomerId(customerId);

        CartItem itemToRemove=cart.getItems().stream()
                .filter(item->item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException("Product not found in cart"));
        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);

        recalculateCartTotalPrice(cart);
        return cartRepository.save(cart);
    }
    @Transactional
    public Cart clearCart(Long customerId){
        Cart cart=getCartByCustomerId(customerId);

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();

        cart.setTotalPrice(BigDecimal.ZERO);
        return cartRepository.save(cart);

    }



    private void recalculateCartTotalPrice(Cart cart)  {
        BigDecimal total=BigDecimal.ZERO;
        for(CartItem item :cart.getItems()){
            BigDecimal itemTotal=item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            total=total.add(itemTotal);
        }
        cart.setTotalPrice(total);
    }


}

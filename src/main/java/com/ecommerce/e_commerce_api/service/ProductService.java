package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.CreateProductRequestDTO;
import com.ecommerce.e_commerce_api.dto.ProductResponseDTO;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.model.Product;
import com.ecommerce.e_commerce_api.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private IProductRepository productRepository;

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return mapToProductResponseDTO(product);
    }

    public ProductResponseDTO createProduct(CreateProductRequestDTO requestDTO) {
        Product product = Product.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .price(requestDTO.getPrice())
                .stock(requestDTO.getStock())
                .build();
        Product savedProduct = productRepository.save(product);
        return mapToProductResponseDTO(savedProduct);
    }

    public ProductResponseDTO updateProduct(Long id, CreateProductRequestDTO requestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        existingProduct.setName(requestDTO.getName());
        existingProduct.setDescription(requestDTO.getDescription());
        existingProduct.setPrice(requestDTO.getPrice());
        existingProduct.setStock(requestDTO.getStock());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductResponseDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }


    private ProductResponseDTO mapToProductResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
    public Product findProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }
}
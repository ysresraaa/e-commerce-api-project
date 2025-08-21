package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.CreateProductRequestDTO;
import com.ecommerce.e_commerce_api.dto.ProductResponseDTO;
import com.ecommerce.e_commerce_api.exception.InsufficientStockException;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.mapper.ProductMapper;
import com.ecommerce.e_commerce_api.model.Product;
import com.ecommerce.e_commerce_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toResponseDTOList(products);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductByCode(UUID productCode) {
        Product product = findProductEntityByCode(productCode);
        return productMapper.toResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO createProduct(CreateProductRequestDTO requestDTO) {
        Product product = productMapper.toEntity(requestDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    @Transactional
    public ProductResponseDTO updateProduct(UUID productCode, CreateProductRequestDTO requestDTO) {
        Product existingProduct = findProductEntityByCode(productCode);

        existingProduct.setName(requestDTO.getName());
        existingProduct.setDescription(requestDTO.getDescription());
        existingProduct.setPrice(requestDTO.getPrice());
        existingProduct.setStock(requestDTO.getStock());

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(UUID productCode) {
        Product productToDelete = findProductEntityByCode(productCode);
        productRepository.delete(productToDelete);
    }

    @Transactional
    public void decreaseStock(UUID productCode, int quantity) {
        Product product = findProductEntityByCode(productCode);

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                    "Not enough stock for product: " + product.getName() +
                            ". Required: " + quantity + ", Available: " + product.getStock()
            );
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product findProductEntityByCode(UUID productCode) {
        return productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with code: " + productCode));
    }
}
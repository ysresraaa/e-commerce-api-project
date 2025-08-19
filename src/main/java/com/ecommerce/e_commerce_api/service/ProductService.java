package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.CreateProductRequestDTO;
import com.ecommerce.e_commerce_api.dto.ProductResponseDTO;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.mapper.ProductMapper;
import com.ecommerce.e_commerce_api.model.Product;
import com.ecommerce.e_commerce_api.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final IProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(IProductRepository productRepository ,ProductMapper productMapper){
        this.productRepository=productRepository;
        this.productMapper=productMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        List<Product>products=productRepository.findAll();
        return productMapper.toResponseDTOList(products);

    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
     Product product=findProductEntityById(id);
     return productMapper.toResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO createProduct(CreateProductRequestDTO requestDTO) {
        Product product=productMapper.toEntity(requestDTO);
        Product savedProduct=productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);

    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, CreateProductRequestDTO requestDTO) {
        Product existingProduct = findProductEntityById(id);


        existingProduct.setName(requestDTO.getName());
        existingProduct.setDescription(requestDTO.getDescription());
        existingProduct.setPrice(requestDTO.getPrice());
        existingProduct.setStock(requestDTO.getStock());

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product productToDelete=findProductEntityById(id);
        productRepository.delete(productToDelete);
    }

@Transactional(readOnly = true)
    public Product findProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }
}
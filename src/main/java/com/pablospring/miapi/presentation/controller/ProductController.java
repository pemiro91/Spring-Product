package com.pablospring.miapi.presentation.controller;

import com.pablospring.miapi.domain.model.Product;
import com.pablospring.miapi.payload.ApiResponse;
import com.pablospring.miapi.presentation.dto.product.PageResponse;
import com.pablospring.miapi.presentation.dto.product.ProductRequest;
import com.pablospring.miapi.presentation.dto.product.ProductResponse;
import com.pablospring.miapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> getProductsPages(Pageable pageable) {
        Page<ProductResponse> products = productService.listAllForPage(pageable);
        PageResponse<ProductResponse> pageResponse = new PageResponse<>(
                products.getContent(),
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages()
        );
        return ApiResponse.success("Lista de productos paginada", pageResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> list() {
        return ResponseEntity.ok(productService.listAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.getForId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest product) {
        return ResponseEntity.ok(productService.create(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

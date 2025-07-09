package com.pablospring.miapi.service;

import com.pablospring.miapi.config.AuthUtil;
import com.pablospring.miapi.domain.event.ProductCreateEvent;
import com.pablospring.miapi.domain.exception.ForbiddenException;
import com.pablospring.miapi.domain.exception.NotFoundException;
import com.pablospring.miapi.domain.model.Product;
import com.pablospring.miapi.domain.model.RoleName;
import com.pablospring.miapi.domain.model.User;
import com.pablospring.miapi.domain.repository.ProductRepository;
import com.pablospring.miapi.domain.repository.UserRepository;
import com.pablospring.miapi.presentation.dto.product.ProductRequest;
import com.pablospring.miapi.presentation.dto.product.ProductResponse;
import com.pablospring.miapi.presentation.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;
    private final AuthUtil authUtil;
    private final ApplicationEventPublisher eventPublisher;

    public List<ProductResponse> listAll() {
        /* User actual = userRepository.findById(authUtil.getUserCurrent().getId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        List<Product> products = actual.getRoles().stream()
                .anyMatch(r -> r.getName().equals(RoleName.ROLE_USER))
                ? productRepository.findAll()
                : productRepository.findByUserId(actual.getId());*/
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Page<ProductResponse> listAllForPage(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);

        return products.map(productMapper::toResponse);
    }

    public Optional<Product> getForId(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return Optional.empty();

        User currentUser = authUtil.getUserCurrent();
        boolean esAdmin = currentUser.getRoles().stream()
                .anyMatch(rol -> rol.getName().equals(RoleName.ROLE_ADMIN));
        if (!product.getUser().getId().equals(currentUser.getId()) &&
                !esAdmin) {
            throw new ForbiddenException("No tienes permiso para acceder a este producto.");
        }

        return Optional.of(product);
    }

    public ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .createdFor(p.getUser().getEmail())
                .build();
    }

    public ProductResponse create(ProductRequest request) {
        User user = userRepository.findById(authUtil.getUserCurrent().getId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        Product newProduct = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .user(user)
                .build();
        return toResponse(productRepository.save(newProduct));
    }

    public Optional<Product> update(Long id, Product newProduct) {
        return productRepository.findById(id).map(product -> {
            User currentUser = authUtil.getUserCurrent();
            boolean esAdmin = currentUser.getRoles().stream()
                    .anyMatch(rol -> rol.getName().equals(RoleName.ROLE_ADMIN));
            if (!product.getUser().getId().equals(currentUser.getId()) && !esAdmin) {
                throw new ForbiddenException("No tienes permiso para modificar este producto.");
            }

            product.setName(newProduct.getName());
            product.setDescription(newProduct.getDescription());
            product.setPrice(newProduct.getPrice());
            return productRepository.save(product);
        });
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        User currentUser = authUtil.getUserCurrent();
        boolean esAdmin = currentUser.getRoles().stream()
                .anyMatch(rol -> rol.getName().equals(RoleName.ROLE_ADMIN));
        if (!product.getUser().getId().equals(currentUser.getId()) && !esAdmin) {
            throw new ForbiddenException("No tienes permiso para eliminar este producto.");
        }
        productRepository.delete(product);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public ProductResponse createWithImage(ProductRequest productRequest, MultipartFile file) throws IOException {
        Product product = productMapper.toEntity(productRequest);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        product.setUser(user); // <-- clave

        if (file != null && !file.isEmpty()) {
            String filename = fileStorageService.storeFile(file);
            product.setImagenUrl("/api/products/imagen/" + filename);
        }

        Product savedProduct = productRepository.save(product);

        eventPublisher.publishEvent(new ProductCreateEvent(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImagenUrl()
        ));
        return productMapper.toResponse(savedProduct);
    }


}

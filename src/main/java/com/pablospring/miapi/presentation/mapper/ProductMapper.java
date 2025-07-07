package com.pablospring.miapi.presentation.mapper;

import com.pablospring.miapi.domain.model.Product;
import com.pablospring.miapi.presentation.dto.product.ProductRequest;
import com.pablospring.miapi.presentation.dto.product.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toEntity(ProductRequest dto);

    @Mapping(source = "user.username", target = "createdFor")
    ProductResponse toResponse(Product product);
}

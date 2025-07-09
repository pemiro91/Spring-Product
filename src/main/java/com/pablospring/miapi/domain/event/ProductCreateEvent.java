package com.pablospring.miapi.domain.event;


import java.math.BigDecimal;

public record ProductCreateEvent(Long productId, String name, Double price, String imagenUrl) {

}

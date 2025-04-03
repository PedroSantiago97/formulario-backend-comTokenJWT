package com.example.auth.controllers;

import com.example.auth.domain.product.Product;
import com.example.auth.domain.product.ProductRequestDTO;
import com.example.auth.domain.product.ProductResponseDTO;
import com.example.auth.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("product")
public class ProductController {

    @Autowired
    ProductRepository repository;

    @PostMapping("/add")
    public ResponseEntity<?> postProduct(@RequestBody @Valid ProductRequestDTO body) {
        try {
            System.out.println("O produto " + body.name()+" foi cadastrado com sucesso!");
            
            // Validação explícita (redundante, mas útil para debug)
            if (body.name() == null || body.name().isBlank()) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "VALIDATION_ERROR", "message", "O campo 'name' é obrigatório")
                );
            }
            
            if (body.price() == null || body.price() < 0) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "VALIDATION_ERROR", "message", "O campo 'price' deve ser positivo")
                );
            }

            Product newProduct = new Product(body);
            repository.save(newProduct);

            return ResponseEntity.ok().body(
                Map.of("success", true, "message", "Produto cadastrado com sucesso")
            );
            
        } catch (Exception e) {
            System.err.println("Erro no cadastro: " + e.getMessage());
            return ResponseEntity.internalServerError().body(
                Map.of("error", "SERVER_ERROR", "message", "Erro ao processar requisição")
            );
        }
    }

    @GetMapping
    public ResponseEntity getAllProducts(){
        List<ProductResponseDTO> productList = this.repository.findAll().stream().map(ProductResponseDTO::new).toList();

        return ResponseEntity.ok(productList);
    }
}

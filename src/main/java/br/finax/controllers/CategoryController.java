package br.finax.controllers;

import br.finax.models.Category;
import br.finax.services.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{id}")
    private Category getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @GetMapping("/get-by-user")
    private List<Category> getByUser() {
        return categoryService.getByUser();
    }

    @PostMapping
    private Category save(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return categoryService.deleteById(id);
    }
}

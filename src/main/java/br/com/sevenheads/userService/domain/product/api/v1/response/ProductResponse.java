package br.com.sevenheads.userService.domain.product.api.v1.response;

import br.com.sevenheads.userService.domain.entity.Category;
import br.com.sevenheads.userService.domain.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private UUID id;
    private String name;
    private Set<String> categories;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.categories = product.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toSet());
    }
}

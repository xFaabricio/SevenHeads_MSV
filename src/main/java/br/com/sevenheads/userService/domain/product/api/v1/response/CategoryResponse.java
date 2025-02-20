package br.com.sevenheads.userService.domain.product.api.v1.response;

import br.com.sevenheads.userService.domain.entity.Category;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {

    private Long id;
    private String name;
    private List<CategoryResponse> subcategories;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.subcategories = category.getSubcategories() != null
                ? category.getSubcategories().stream().map(CategoryResponse::new).collect(Collectors.toList())
                : null;
    }
}

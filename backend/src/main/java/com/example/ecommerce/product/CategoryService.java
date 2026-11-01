
package com.example.ecommerce.product;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.common.exception.ResourceNotFoundException;
import com.example.ecommerce.product.dto.CategoryRequest;
import com.example.ecommerce.product.dto.CategoryResponse;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        Category category = getOrThrow(id);
        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        validateParentId(request.parentId());

        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        category.setParentId(request.parentId());

        Category savedCategory = categoryRepository.save(category);

        return CategoryResponse.from(savedCategory);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getOrThrow(id);
        validateParentId(request.parentId());

        if (id.equals(request.parentId())) {
            throw new IllegalArgumentException(
                    "Danh mục không thể là danh mục cha của chính nó"
            );
        }

        category.setName(request.name());
        category.setDescription(request.description());
        category.setParentId(request.parentId());

        return CategoryResponse.from(category);
    }

    private Category getOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy danh mục với id " + id
                        )
                );
    }
    private void validateParentId(Long parentId) {
        if (parentId != null) {
            getOrThrow(parentId);
        }
    }

}


package com.codegym.service.category;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.service.IGenneralService;

public interface ICategoryService extends IGenneralService<Category> {
    Iterable<Product> productsFindByCategoryId(Long id);
    void deleteByProcedure(Long category_id);
}

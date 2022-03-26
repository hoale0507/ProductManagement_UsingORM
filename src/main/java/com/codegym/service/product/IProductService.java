package com.codegym.service.product;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.service.IGenneralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProductService extends IGenneralService<Product> {
    Page<Product> searchProductByPartOfName(String name, Pageable pageable);
    Iterable<Product> searchProductByPriceAndName(Double min, Double max);
    Iterable<Product> findAllByCategory(Category category);
    Page<Product> findAll(Pageable pageable);

}

package com.codegym.repository;

import com.codegym.model.Category;
import com.codegym.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends PagingAndSortingRepository<Product,Long> {
    Page<Product> findByNameContaining(String name, Pageable pageable);
    @Query(value = "select * from products where price between ?1 and ?2",nativeQuery = true)
    Iterable<Product> searchProductByPriceAndName(Double min, Double max);

    Iterable<Product> findAllByCategory(Category category);
}

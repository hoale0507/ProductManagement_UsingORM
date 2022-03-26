package com.codegym.repository;

import com.codegym.model.Category;
import com.codegym.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends PagingAndSortingRepository<Category,Long> {
@Query(value = "select * from products where category_id = ?1;",nativeQuery = true)
    Iterable<Product> productsFindByCategoryId(Long id);

    @Query(value = "call delete_category(?1)",nativeQuery = true)
    void deleteByProcedure(Long category_id);
}

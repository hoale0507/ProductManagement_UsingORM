package com.codegym.dao;

import com.codegym.model.Product;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Component
@Transactional
public class ProductDao implements IProductDao{
@PersistenceContext
private EntityManager entityManager;
    @Override
    public List<Product> findAll() {
        TypedQuery<Product> query = entityManager.createQuery("select p from Product as p",Product.class);
        return query.getResultList();
    }

    @Override
    public Product findById(Long id) {
        TypedQuery<Product> query = entityManager.createQuery("select p from Product as p where p.id = :id ",Product.class);
        query.setParameter("id",id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public Product save(Product product) {
        if(product.getId() == null){
            entityManager.persist(product);
        } else {
            entityManager.merge(product);
        }
        return product;
    }

    @Override
    public void removeById(Long id) {
        Product product = this.findById(id);
        if(product != null){
            entityManager.remove(product);
        }
    }

    @Override
    public List<Product> searchProductByPartOfName(String q) {
        TypedQuery<Product> query = entityManager.createQuery("Select p from Product as p where p.name like ?1",Product.class);
        query.setParameter(1,q);
        return query.getResultList();
    }
}

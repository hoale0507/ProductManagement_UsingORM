package com.codegym.controller;

import com.codegym.model.Product;
import com.codegym.model.ProductForm;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductRestController {
    @Autowired
    private IProductService productService;
    @Value("${upload-file}")
    private String uploadPath;
    @GetMapping
    public ResponseEntity<Page<Product>> findAll(@RequestParam(name = "q") Optional<String> q, @RequestParam(defaultValue = "0", required = false) Integer page) {
        PageRequest pageable = PageRequest.of(page,3, Sort.by("price").ascending());
        Page<Product> products = productService.findAll(pageable);
        if (q.isPresent()) {
            products = productService.searchProductByPartOfName(q.get(),pageable);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(productOptional.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> save(@ModelAttribute ProductForm productForm) {
        MultipartFile multipartFile = productForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        fileName = System.currentTimeMillis() + fileName;
        try {
            FileCopyUtils.copy(multipartFile.getBytes(), new File(uploadPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Product product = new Product(productForm.getId(), productForm.getName(), productForm.getPrice(),productForm.getDescription(),fileName);
        product.setCategory(productForm.getCategory());
        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @ModelAttribute ProductForm newProductForm) {
        Optional<Product> oldProduct = productService.findById(id);
        if (!oldProduct.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        MultipartFile multipartFile = newProductForm.getImage();
        String fileName;
        if(multipartFile.getSize() == 0){
            fileName = oldProduct.get().getImage();
        } else {
            fileName = multipartFile.getOriginalFilename();
            fileName = System.currentTimeMillis() + fileName;
            try {
                FileCopyUtils.copy(multipartFile.getBytes(), new File(uploadPath + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Product newProduct = new Product(id,newProductForm.getName(), newProductForm.getPrice(), newProductForm.getDescription(), fileName);
        newProduct.setCategory(newProductForm.getCategory());
        return new ResponseEntity<>(productService.save(newProduct), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        productService.removeById(id);
        return new ResponseEntity<>(productOptional.get(), HttpStatus.OK);
    }
}

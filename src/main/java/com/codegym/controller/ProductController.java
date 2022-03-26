package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.model.ProductForm;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Value("${upload-file}")
    private String uploadPath;
    @Autowired
    private IProductService productService;
    @Autowired
    private ICategoryService categoryService;

    @GetMapping
    public ModelAndView showListProduct(@RequestParam(name = "q") Optional<String> q, @PageableDefault(value = 10) Pageable pageable) {
        Page<Product> products;
        if(!q.isPresent()){
        products = productService.findAll(pageable);
        } else {
            products = productService.searchProductByPartOfName(q.get(), pageable);
        }
        ModelAndView modelAndView = new ModelAndView("/product/list", "products", products);
        return modelAndView;
    }
    @GetMapping("/{id}")
    public ModelAndView showProductDetail(@PathVariable Long id){
        ModelAndView modelAndView = new ModelAndView("/product/view");
        Optional<Product> product = productService.findById(id);
        modelAndView.addObject("product",product.get());
        return modelAndView;
    }
    @GetMapping("/create")
    public ModelAndView showCreateForm(){
        ModelAndView modelAndView = new ModelAndView("/product/create");
        ProductForm productForm = new ProductForm();
        Iterable<Category> categories = categoryService.findAll();
        modelAndView.addObject("categories",categories);
        modelAndView.addObject("productForm",productForm);
        return modelAndView;
    }
    @PostMapping("/create")
    public ModelAndView createProduct(@ModelAttribute ProductForm productForm){
        MultipartFile multipartFile = productForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        fileName = System.currentTimeMillis() + fileName;
        try {
            FileCopyUtils.copy(multipartFile.getBytes(),new File(uploadPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Product product = new Product(productForm.getId(),productForm.getName(),productForm.getPrice(),productForm.getDescription(),fileName,productForm.getCategory());
        productService.save(product);
        ModelAndView modelAndView = new ModelAndView("redirect:/products");
        return modelAndView;
    }
    @GetMapping("/edit/{id}")
    public ModelAndView showEditForm(@PathVariable Long id){
        Optional<Product> product = productService.findById(id);
//        ProductForm productForm = new ProductForm();
        ModelAndView modelAndView = new ModelAndView("/product/edit");
        modelAndView.addObject("product",product.get());
        Iterable<Category> categories = categoryService.findAll();
        modelAndView.addObject("categories",categories);
        return modelAndView;
    }
    @PostMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable Long id, @ModelAttribute ProductForm productForm){
        MultipartFile multipartFile = productForm.getImage();
        String fileName;
        if(multipartFile.getSize() == 0){
            Optional<Product> product = productService.findById(id);
            fileName = product.get().getImage();
        } else {
            fileName = multipartFile.getOriginalFilename();
            fileName = System.currentTimeMillis() + fileName;
            try {
                FileCopyUtils.copy(multipartFile.getBytes(),new File(uploadPath+fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Product newProduct = new Product(id,productForm.getName(), productForm.getPrice(), productForm.getDescription(), fileName);
        newProduct.setCategory(productForm.getCategory());
        productService.save(newProduct);
        ModelAndView modelAndView = new ModelAndView("redirect:/products");
        return modelAndView;
    }
    @GetMapping("/delete/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id){
        ModelAndView modelAndView = new ModelAndView("/product/delete");
        Optional<Product> product = productService.findById(id);
        modelAndView.addObject("product",product.get());
        return modelAndView;
    }
    @PostMapping("/delete/{id}")
    public ModelAndView deleteProduct(@PathVariable Long id){
        productService.removeById(id);
        ModelAndView modelAndView = new ModelAndView("redirect:/products");
        return modelAndView;
    }
    @GetMapping("/search")
    public ModelAndView showSearchResult(@RequestParam("min") Double min, @RequestParam("max") Double max){
        Iterable<Product> products = productService.searchProductByPriceAndName(min,max);
        ModelAndView modelAndView = new ModelAndView("/product/list");
        modelAndView.addObject("products",products);
        return modelAndView;
    }
}

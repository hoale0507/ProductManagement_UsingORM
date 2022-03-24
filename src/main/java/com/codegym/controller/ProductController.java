package com.codegym.controller;

import com.codegym.model.Product;
import com.codegym.model.ProductForm;
import com.codegym.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
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

    @GetMapping
    public ModelAndView showListProduct(@RequestParam(name = "q") Optional<String> q) {
        List<Product> products = new ArrayList<>();
        if(!q.isPresent()){
        products = productService.findAll();
        } else {
            products = productService.searchProductByPartOfName(q.get());
        }
        ModelAndView modelAndView = new ModelAndView("/product/list", "products", products);
        return modelAndView;
    }
    @GetMapping("/{id}")
    public ModelAndView showProductDetail(@PathVariable Long id){
        ModelAndView modelAndView = new ModelAndView("/product/view");
        Product product = productService.findById(id);
        modelAndView.addObject("product",product);
        return modelAndView;
    }
    @GetMapping("/create")
    public ModelAndView showCreateForm(){
        ModelAndView modelAndView = new ModelAndView("/product/create");
        ProductForm productForm = new ProductForm();
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
        Product product = new Product(productForm.getId(),productForm.getName(),productForm.getPrice(),productForm.getDescription(),fileName);
        productService.save(product);
        ModelAndView modelAndView = new ModelAndView("redirect:/products");
        return modelAndView;
    }
    @GetMapping("/edit/{id}")
    public ModelAndView showEditForm(@PathVariable Long id){
        Product product = productService.findById(id);
//        ProductForm productForm = new ProductForm();
        ModelAndView modelAndView = new ModelAndView("/product/edit");
        modelAndView.addObject("product",product);
//        modelAndView.addObject("productForm",productForm);
        return modelAndView;
    }
    @PostMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable Long id, @ModelAttribute ProductForm productForm){
        MultipartFile multipartFile = productForm.getImage();
        String fileName;
        if(multipartFile.getSize() == 0){
            Product product = productService.findById(id);
            fileName = product.getImage();
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
        productService.save(newProduct);
        ModelAndView modelAndView = new ModelAndView("redirect:/products");
        return modelAndView;
    }
    @GetMapping("/delete/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id){
        ModelAndView modelAndView = new ModelAndView("/product/delete");
        Product product = productService.findById(id);
        modelAndView.addObject("product",product);
        return modelAndView;
    }
    @PostMapping("/delete/{id}")
    public ModelAndView deleteProduct(@PathVariable Long id){
        productService.removeById(id);
        ModelAndView modelAndView = new ModelAndView("redirect:/products");
        return modelAndView;
    }
}

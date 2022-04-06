package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.service.category.CategoryService;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IProductService productService;
    @GetMapping
    public ModelAndView showCategoryList(){
        Iterable<Category> categories = categoryService.findAll();
        ModelAndView modelAndView = new ModelAndView("category/list","categories",categories);
        return modelAndView;
    }
    @GetMapping("/{id}")
    public ModelAndView showCategoryDetail(@PathVariable Long id){
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (!categoryOptional.isPresent()){
            return new ModelAndView("error-404");
        }
        Iterable<Product> products = productService.findAllByCategory(categoryOptional.get());
        ModelAndView modelAndView = new ModelAndView("category/view","products",products);
        return modelAndView;
    }
    @GetMapping("/delete/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id){
        ModelAndView modelAndView = new ModelAndView("/category/delete");
        Optional<Category> category = categoryService.findById(id);
        if(!category.isPresent()){
            return new ModelAndView("error-404");
        }
        modelAndView.addObject("category",category.get());
        return modelAndView;
    }
    @PostMapping("/delete/{id}")
    public ModelAndView deleteCategory(@PathVariable Long id){
//        categoryService.removeById(id);
        categoryService.deleteByProcedure(id);
        ModelAndView modelAndView = new ModelAndView("redirect:/categories");
        return modelAndView;
    }
    @GetMapping("/create")
    public ModelAndView modelAndView(){
        ModelAndView modelAndView = new ModelAndView("/category/create");
        Category category = new Category();
        modelAndView.addObject("category",category);
        return modelAndView;
    }
    @PostMapping("/create")
    public ModelAndView modelAndView(@ModelAttribute Category category){
        categoryService.save(category);
        ModelAndView modelAndView = new ModelAndView("/category/create");
        modelAndView.addObject("message","Create successfully");
        return modelAndView;
    }
    @GetMapping("/edit/{id}")
    public ModelAndView showEditForm(@PathVariable Long id){
        Optional<Category> category = categoryService.findById(id);
        if(!category.isPresent()){
            return new ModelAndView("error-404");
        }
        ModelAndView modelAndView = new ModelAndView("/category/edit","category",category.get());
        return modelAndView;
    }
    @PostMapping("/edit/{id}")
    public ModelAndView editCategory(@ModelAttribute Category category){
        categoryService.save(category);
        ModelAndView modelAndView = new ModelAndView("/category/edit");
        modelAndView.addObject("message","Edit successfully!");
        return modelAndView;
    }
}

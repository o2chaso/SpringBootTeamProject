package com.example.SpringGroupBB.controller;

import com.example.SpringGroupBB.dto.ProductDTO;
import com.example.SpringGroupBB.entity.Product;
import com.example.SpringGroupBB.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping("/productList")
  public String productListGet(Model model) {

    List<Product> productList =  productService.searchProductAll();

    model.addAttribute("productList", productList);
    return "product/productList";
  }

  @GetMapping("/productContent/{id}")
  public String productContentGet(Model model, @PathVariable Long id) {

    ProductDTO productDTO = productService.searchProduct(id);
    model.addAttribute("productDTO", productDTO);

    return "/product/productContent";
  }

  @GetMapping("/productDetail")
  public String productDetailGet(@RequestParam(required = false) Integer flag, Model model) {
    System.out.println(flag);
    model.addAttribute("flag", flag);
    return "/product/productDetail";
  }
}

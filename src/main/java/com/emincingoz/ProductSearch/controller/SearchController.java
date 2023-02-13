package com.emincingoz.ProductSearch.controller;

import com.emincingoz.ProductSearch.model.Product;
import com.emincingoz.ProductSearch.service.ProductSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class SearchController {

    private ProductSearchService searchService;

    @Autowired
    public SearchController(ProductSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/products")
    @ResponseBody
    public List<Product> fetchByNameOrDesc(@RequestParam(value = "q", required = false) String query) {
        log.info("searching by name {}",query);
        List<Product> products = searchService.processSearch(query) ;
        log.info("products {}",products);
        return products;
    }

    @GetMapping("/suggestions")
    @ResponseBody
    public List<String> fetchSuggestions(@RequestParam(value = "q", required = false) String query) {
        log.info("fetch suggests {}",query);
        List<String> suggests = searchService.fetchSuggestions(query);
        log.info("suggests {}",suggests);
        return suggests;
    }

}

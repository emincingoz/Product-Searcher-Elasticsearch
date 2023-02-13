package com.emincingoz.ProductSearch.service;

import com.emincingoz.ProductSearch.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSearchService {

    private static final String PRODUCT_INDEX = "productindex";

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public List<IndexedObjectInformation> createProductIndexBulk(final List<Product> products) {

        List<IndexQuery> queries =
                products.stream()
                .map(product ->
                        new IndexQueryBuilder()
                                .withId(product.getId())
                                .withObject(product).build())
                        .collect(Collectors.toList());

        return elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(PRODUCT_INDEX));
    }

    public String createProductIndex(Product product) {

        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(product.getId().toString())
                .withObject(product).build();

        String documentId = elasticsearchOperations
                .index(indexQuery, IndexCoordinates.of(PRODUCT_INDEX));

        return documentId;
    }

    /**
     * NativeQuery provides the maximum flexibility for building a query using objects representing Elasticsearch constructs like aggregation, filter and sort.
     *
     * We are building a query with a NativeSearchQueryBuilder which uses a MatchQueryBuilder to specify the match query containing the filed
     * @param brandName
     */
    public void findProductsByBrand(final String brandName) {

        MatchQueryBuilder queryBuilder =
                QueryBuilders
                .matchQuery("manufacturer", brandName);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();

        SearchHits<Product> productSearchHits =
                elasticsearchOperations
                        .search(searchQuery,
                                Product.class,
                                IndexCoordinates.of(PRODUCT_INDEX));

        log.info("productHts {} {}", productSearchHits.getSearchHits().size(), productSearchHits.getSearchHits());

        List<SearchHit<Product>> searchHits = productSearchHits.getSearchHits();

        for(SearchHit<Product> searchHit : searchHits) {
            log.info("searchHit {}", searchHit);
        }
    }

    /**
     * StringQuery gives full control by allowing the use of the native Elasticsearch query as a JSON string.
     * @param productName
     */
    public void findProductByName(final String productName) {
        Query searchQuery = new StringQuery("{\\\"match\\\":{\\\"name\\\":{\\\"query\\\":\\\"\"+ productName + \"\\\"}}}\\\"");

        SearchHits<Product> productSearchHits = elasticsearchOperations
                .search(searchQuery,
                        Product.class,
                        IndexCoordinates.of(PRODUCT_INDEX));
    }

    /**
     * With CriteriaQuery we can build queries without knowing any terminology of Elasticsearch.
     * The queries are built using method chaining with Criteria Objects.
     * Each object speicifies some criteria used for searching documents.
     *
     * @param productPrice
     */
    public void findByProductPrice(final String productPrice) {
        Criteria criteria = new Criteria("price")
                .greaterThan(10.0)
                .lessThan(100.0);

        Query searchQuery = new CriteriaQuery(criteria);

        SearchHits<Product> productSearchHits = elasticsearchOperations
                .search(searchQuery,
                        Product.class,
                        IndexCoordinates.of(PRODUCT_INDEX));
    }

    public List<Product> processSearch(final String query) {
        log.info("Search with query {}", query);

        // 1. Create query on multiple fields enabling fuzzy search
        MultiMatchQueryBuilder queryBuilder =
                QueryBuilders
                        .multiMatchQuery(query, "name", "description")
                        .fuzziness(Fuzziness.AUTO);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(queryBuilder)
                .build();

        // 2. Execute search
        SearchHits<Product> productHits =
                elasticsearchOperations
                        .search(searchQuery, Product.class,
                                IndexCoordinates.of(PRODUCT_INDEX));

        // 3. Map searchHits to product list
        List<Product> productMatches = new ArrayList<Product>();
        productHits.forEach(srchHit->{
            productMatches.add(srchHit.getContent());
        });
        return productMatches;
    }

    public List<String> fetchSuggestions(String query) {
        WildcardQueryBuilder queryBuilder = QueryBuilders
                .wildcardQuery("name", query+"*");

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(queryBuilder)
                .withPageable(PageRequest.of(0, 5))
                .build();

        SearchHits<Product> searchSuggestions =
                elasticsearchOperations.search(searchQuery,
                        Product.class,
                        IndexCoordinates.of(PRODUCT_INDEX));

        List<String> suggestions = new ArrayList<String>();

        searchSuggestions.getSearchHits().forEach(searchHit->{
            suggestions.add(searchHit.getContent().getName());
        });
        return suggestions;
    }
}

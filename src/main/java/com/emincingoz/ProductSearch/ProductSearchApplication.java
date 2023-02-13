package com.emincingoz.ProductSearch;

import com.emincingoz.ProductSearch.model.Product;
import com.emincingoz.ProductSearch.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.util.*;

@Slf4j
@SpringBootApplication
public class ProductSearchApplication {

	@Autowired
	private static final String COMMA_DELIMITER = ",";

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ElasticsearchOperations operations;

	public static void main(String[] args) {
		SpringApplication.run(ProductSearchApplication.class, args);
	}

	@PreDestroy
	public void deleteIndex() {
		operations.indexOps(Product.class).delete();
	}

	@PostConstruct
	public void buildIndex() {
		operations.indexOps(Product.class).refresh();
		productRepository.deleteAll();
		productRepository.saveAll(prepareDataset());
	}

	private Collection<Product> prepareDataset() {
		Resource resource = new ClassPathResource("fashion-products.csv");
		List<Product> productList = new ArrayList<>();

		try {
			InputStream inputStream = resource.getInputStream();
			Scanner scanner = new Scanner(resource.getInputStream());
			int lineNo = 0;

			while (scanner.hasNextLine()) {
				++lineNo;
				String line = scanner.nextLine();

				if (lineNo == 1) continue;

				Optional<Product> product = csvRowToProductMapper(line);

				if (product.isPresent())
					productList.add(product.get());
			}

		} catch (Exception e) {
			log.error("File read error {}", e);
		}

		return productList;
	}

	private Optional<Product> csvRowToProductMapper(final String line) {
		try {
			Scanner rowScanner = new Scanner(line);
			rowScanner.useDelimiter(COMMA_DELIMITER);

			while (rowScanner.hasNext()) {
				String name = rowScanner.next();
				String description = rowScanner.next();
				String manufacturer = rowScanner.next();

				return Optional.of(Product
						.builder()
						.name(name)
						.description(description)
						.manufacturer(manufacturer)
						.build());
			}

		} catch (Exception e) {
			log.error("Csv split error {}", e);
		}

		return Optional.of(null);
	}
}

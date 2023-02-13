# Product-Searcher-Elasticsearch
Elasticsearch implementation

<h3>Dependencies:</h3>

- Spring Web
- Thymeleaf
- Elasticsearch
  ```
  <dependency>
	  <groupId>org.springframework.data</groupId>
	  <artifactId>spring-data-elasticsearch</artifactId>
  </dependency>
  ```

<h3>Info:</h3>

- UI Url: `http://localhost:8080/search`
- Elasticsearch Url: `http://localhost:9200`
- Index Url: `http://localhost:9200/productindex/`

---

PostgreSQL => Databases => Tables => Columns/Rows

Elasticsearch => Indices => Types => Fields/Documents

<h4>Index:</h4>

Elasticsearch’e eklenen her kayıt JSON belgesi olarak yapılandırılır. Yani, dökümanların içerisindeki her bir kelime(terim) için hangi döküman yada dökümanlarda o kelimenin olduğu bilgisini tutan bir endeksleme sistemi vardır. 
Bir nevi veritabanı gibi düşünülebilir. Veri tabanındaki verilerde olan düzen gibi, Elasticsearch’ün indexleri de JSON formatı şeklinde düzenlidir.

---


![image](https://user-images.githubusercontent.com/49842813/218399031-c077c1ad-cbe7-49f8-aeaf-6550686c7ea3.png)

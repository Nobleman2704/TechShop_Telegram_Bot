package org.example.repository.product;

import org.example.domain.model.product.Product;
import org.example.repository.BaseRepository;

import java.util.List;

public interface ProductRepository extends BaseRepository<Product> {
    String GET_PRODUCTS_BY_TYPE = """
                                       select * from products
                                       where type = ?""";
    String GET_PRODUCT_ID = """
            select product_id from user_products
            where user_id = ?::uuid""";
    String GET_PRODUCT_BY_ID = """
            select * from products where id = ?::uuid""";
    String GET_PRODUCT_ID_FROM_USER_PRODUCTS = """
                                        select product_id from user_products
                                        where user_id = ?::uuid""";
    String GET_BY_ID = "select * from products where id = ?::uuid";
    List<Product> getProducts(String productType);

    String getProductType(String userId);

    Product getProduct(String userId);
}

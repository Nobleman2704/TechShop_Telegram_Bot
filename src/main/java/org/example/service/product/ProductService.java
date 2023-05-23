package org.example.service.product;

import org.example.domain.DTO.BaseResponse;
import org.example.domain.model.product.Product;
import org.example.service.BaseService;

import java.util.List;

public interface ProductService extends BaseService<Product, String> {
    BaseResponse<List<Product>> getProducts(String productType);

    String getProductType(String userId);

    Product getProduct(String toString);
}

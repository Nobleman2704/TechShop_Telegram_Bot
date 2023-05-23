package org.example.service.product;

import org.example.domain.DTO.BaseResponse;
import org.example.domain.model.product.Product;
import org.example.repository.order.OrderRepository;
import org.example.repository.order.OrderRepositoryImpl;
import org.example.repository.product.ProductRepository;
import org.example.repository.product.ProductRepositoryImpl;

import java.util.List;
import java.util.UUID;

public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository = ProductRepositoryImpl.getInstance();
    private ProductServiceImpl(){}
    private static ProductServiceImpl productService;
    public static ProductServiceImpl getInstance(){
        return (productService==null)?productService = new ProductServiceImpl()
                :productService;
    }

    @Override
    public BaseResponse<String> save(Product o) {
        return null;
    }

    @Override
    public BaseResponse<String> getById(UUID id) {
        Product product = productRepository.getById(id);
        String response = "👨‍💻 Name: " + product.getName() +
                ",\n💵 Price: " + product.getPrice() +
                " $,\n👨‍💻🖥️📱💻 Type: " + product.getType() +
                ",\n\n Click 1...10 to add product to your Basket";
        return new BaseResponse<String>()
                .setData(response);
    }

    @Override
    public BaseResponse<List<Product>> getProducts(String productType) {
        List<Product> products = productRepository.getProducts(productType);
        return new BaseResponse<List<Product>>().setData(products);
    }

    @Override
    public Product getProduct(String userId) {
        return productRepository.getProduct(userId);
    }

    @Override
    public String getProductType(String userId) {
        return productRepository.getProductType(userId);
    }
}

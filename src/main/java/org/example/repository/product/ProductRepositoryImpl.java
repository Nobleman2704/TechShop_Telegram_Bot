package org.example.repository.product;

import lombok.SneakyThrows;
import org.example.domain.model.product.Product;
import org.example.repository.user.UserRepository;
import org.example.repository.user.UserRepositoryImpl;
import org.example.util.BeanUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ProductRepositoryImpl implements ProductRepository{
    private final UserRepository userRepository = UserRepositoryImpl.getInstance();
    private final Connection connection = BeanUtil.getConnection();

    @SneakyThrows
    @Override
    public Product getProduct(String userId) {
        PreparedStatement getProductId = connection.prepareStatement(GET_PRODUCT_ID_FROM_USER_PRODUCTS);
        getProductId.setString(1, userId);
        getProductId.execute();
        UUID productId = null;
        ResultSet resultSet = getProductId.getResultSet();
        if (resultSet.next()){
            productId = UUID.fromString(resultSet.getString(1));
        }
        return getById(productId);
    }

    private ProductRepositoryImpl(){}
    private static ProductRepositoryImpl productRepository;

    @SneakyThrows
    @Override
    public String getProductType(String userId) {
        String user_Id = userRepository.findUserByChatId(userId).get().getId().toString();
        PreparedStatement getProductId = connection.prepareStatement(GET_PRODUCT_ID);
        getProductId.setString(1, user_Id);
        getProductId.execute();
        String productId = null;
        ResultSet resultSet = getProductId.getResultSet();
        if (resultSet.next()){
            productId = resultSet.getString(1);
        }
        return getById(UUID.fromString(productId)).getType().toString();
    }

    public static ProductRepositoryImpl getInstance(){
        return (productRepository==null)?productRepository = new ProductRepositoryImpl()
                :productRepository;
    }
    @Override
    public int save(Product product) {
        return 0;
    }

    @SneakyThrows
    @Override
    public Product getById(UUID id) {
        PreparedStatement get = connection.prepareStatement(GET_PRODUCT_BY_ID);
        get.setString(1, id.toString());
        get.execute();
        ResultSet resultSet = get.getResultSet();
        Product product = null;
        if (resultSet.next()){
            product = Product.map(resultSet);
        }
        return product;
    }

    @Override
    public void update(Product product) {

    }

    @SneakyThrows
    @Override
    public List<Product> getProducts(String productType) {
        PreparedStatement products = connection.prepareStatement(GET_PRODUCTS_BY_TYPE);
        products.setString(1, productType);
        products.execute();
        ResultSet resultSet = products.getResultSet();
        List<Product> products1 = new LinkedList<>();
        while (resultSet.next()){
            products1.add(Product.map(resultSet));
        }
        return products1;
    }
}

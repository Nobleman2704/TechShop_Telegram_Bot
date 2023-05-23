package org.example.domain.model;

import lombok.*;
import lombok.experimental.Accessors;

import java.sql.ResultSet;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class Basket extends BaseModel{
    private UUID productId;
    private UUID userId;
    private Integer amount;
    private String productName;
    private Double totalPrice;
    private Double productPrice;
    @SneakyThrows
    public static Basket map(ResultSet resultSet){
        Basket basket = new Basket();
        basket.setId(UUID.fromString(resultSet.getString("id")));
        basket.setCreatedDate(resultSet.getTimestamp("created").toLocalDateTime());
        basket.setUpdatedDate(resultSet.getTimestamp("updated").toLocalDateTime());
        basket
                .setUserId(UUID.fromString(resultSet.getString("user_id")))
                .setProductId(UUID.fromString(resultSet.getString("product_id")))
                .setAmount(resultSet.getInt("amount"))
                .setProductName(resultSet.getString("product_name"))
                .setTotalPrice(resultSet.getDouble("total_price"))
                .setProductPrice(resultSet.getDouble("product_price"));
        return basket;
    }
}

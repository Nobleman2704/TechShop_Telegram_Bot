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
@ToString
public class Order extends BaseModel{
    private UUID productId;
    private UUID userId;
    private String productName;
    private Integer amount;
    private Double totalPrice;
    private Double productPrice;
    @SneakyThrows
    public static Order map(ResultSet resultSet){
        Order order = new Order();
        order.setId(UUID.fromString(resultSet.getString("id")));
        order.setCreatedDate(resultSet.getTimestamp("created").toLocalDateTime());
        order.setUpdatedDate(resultSet.getTimestamp("updated").toLocalDateTime());
        order
                .setUserId(UUID.fromString(resultSet.getString("user_id")))
                .setProductId(UUID.fromString(resultSet.getString("product_id")))
                .setAmount(resultSet.getInt("amount"))
                .setProductName(resultSet.getString("product_name"))
                .setTotalPrice(resultSet.getDouble("total_price"))
                .setProductPrice(resultSet.getDouble("product_price"));
        return order;
    }
}

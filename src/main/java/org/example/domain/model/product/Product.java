package org.example.domain.model.product;


import lombok.*;
import lombok.experimental.Accessors;
import org.example.domain.model.BaseModel;

import java.sql.ResultSet;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class Product extends BaseModel {
    private String name;
    private Double price;
    private ProductType type;
    @SneakyThrows
    public static Product map(ResultSet resultSet){
        Product product = new Product();
        product.setId(UUID.fromString(resultSet.getString("id")));
        product.setUpdatedDate(resultSet.getTimestamp("updated").toLocalDateTime());
        product.setCreatedDate(resultSet.getTimestamp("created").toLocalDateTime());
        product
                .setName(resultSet.getString("name"))
                .setPrice(resultSet.getDouble("price"))
                .setType(ProductType.valueOf(resultSet.getString("type")));
        return product;
    }
}

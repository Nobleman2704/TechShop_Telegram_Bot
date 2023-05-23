package org.example.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BeanUtil {
    private static Connection connection;
    private static ModelMapper modelMapper;
    public static Connection getConnection() {
        if(connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/tech_shop", "postgres", "Asadbek");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    public static ModelMapper getModelMapper() {
        if(modelMapper == null) {
            modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        }
        return modelMapper;
    }
}

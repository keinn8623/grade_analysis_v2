package com.keinn.grade_analysis_v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
public class GradeAnalysisV2Application {

    public static void main(String[] args) {
        SpringApplication.run(GradeAnalysisV2Application.class, args);
    }

    @Bean
    public CommandLineRunner testDatabaseConnection(@Autowired DataSource dataSource) {
        return args -> {
            System.out.println("开始测试数据库连接...");
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) {
                    System.out.println("✅ 数据库连接成功!");
                    System.out.println("数据库URL: " + connection.getMetaData().getURL());
                    System.out.println("数据库产品名称: " + connection.getMetaData().getDatabaseProductName());
                    System.out.println("数据库产品版本: " + connection.getMetaData().getDatabaseProductVersion());
                } else {
                    System.out.println("❌ 数据库连接无效");
                }
            } catch (SQLException e) {
                System.err.println("❌ 数据库连接失败: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}

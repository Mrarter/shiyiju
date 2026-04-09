package com.shiyiju.modules.admin.support;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminSchemaInitializer {

    public AdminSchemaInitializer(JdbcTemplate jdbcTemplate) {
        Integer exists = jdbcTemplate.queryForObject(
            """
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'admin_operation_config'
                  AND column_name = 'image_url'
                """,
            Integer.class
        );
        if (exists != null && exists == 0) {
            jdbcTemplate.execute("ALTER TABLE admin_operation_config ADD COLUMN image_url VARCHAR(500) DEFAULT NULL AFTER target");
        }
    }
}

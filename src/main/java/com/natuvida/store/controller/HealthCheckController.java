package com.natuvida.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @GetMapping("health/database")
  public String checkDatabaseConnection(){
    try {
      Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
      return "Database connection successful: " + ((result != null && result == 1) ? "OK" : "FAILED" );
    } catch (DataAccessException e) {
      return "Database connection failed: " + e.getMessage();
    }
  }
}

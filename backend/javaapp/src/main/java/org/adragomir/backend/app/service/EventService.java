package org.adragomir.backend.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.adragomir.backend.app.controller.EventController;
import org.adragomir.backend.app.model.Event;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

@Service
@Async
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper;

    @Autowired
    public EventService(JdbcTemplate jdbcTemplate, ObjectMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    public CompletableFuture<Boolean> saveEvent(Event event) {
        return CompletableFuture.supplyAsync(() -> {
            return jdbcTemplate.execute((Connection conn) -> {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO events (data) VALUES (?)");
                PGobject jsonbObj = new PGobject();
                jsonbObj.setType("jsonb");
                try {
                    jsonbObj.setValue(mapper.writeValueAsString(event));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                ps.setObject(1, jsonbObj);
                return ps.execute();
            });
        });
    }
}

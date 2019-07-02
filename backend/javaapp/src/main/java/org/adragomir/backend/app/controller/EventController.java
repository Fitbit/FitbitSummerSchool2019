package org.adragomir.backend.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class EventController {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Async
    @RequestMapping(path = "/event", method = RequestMethod.POST)
    public CompletableFuture<ResponseEntity> ingestEventAsync() {
        return CompletableFuture.completedFuture(ResponseEntity.ok().build());
    }
}

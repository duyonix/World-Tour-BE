package com.onix.worldtour.controller.api;

import com.onix.worldtour.dto.response.Response;
import com.onix.worldtour.service.ReviewService;
import com.onix.worldtour.util.ValueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/review")
@Slf4j
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Response> getVideoData(
            @RequestParam(value = "url", required = true) String url
    ) {
        log.info("ReviewController::getVideoData url {}", url);
        Object data = reviewService.getVideoData(url);

        Response<Object> response = Response.ok().setPayload(data);
        log.info("ReviewController::getVideoData response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

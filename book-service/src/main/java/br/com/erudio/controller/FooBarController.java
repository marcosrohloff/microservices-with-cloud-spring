package br.com.erudio.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FooBar Endpoint", description = "Endpoint to retrieve book information")
@RestController
@RequestMapping("book-service")
public class FooBarController {

    private Logger logger = LoggerFactory.getLogger(FooBarController.class);

    @GetMapping("/foo-bar")
    //@Retry(name = "foo-bar")
    //@Retry(name = "foo-bar", fallbackMethod = "fallbackMethod")
    @Bulkhead(name = "default")
    public String fooBar() {
        logger.info("Request foo-bar");
        //var response = new RestTemplate().getForEntity("http://localhost:8080/foo-bar", String.class);
        return "Foo Bar";
    }

    public String fallbackMethod(Exception e) {
        return "fallbackMethod foo-bar";
    }
}

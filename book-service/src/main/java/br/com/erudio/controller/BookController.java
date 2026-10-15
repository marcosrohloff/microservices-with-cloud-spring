package br.com.erudio.controller;

import br.com.erudio.dto.Exchange;
import br.com.erudio.enviroment.InstanceInformationService;
import br.com.erudio.model.Book;
import br.com.erudio.proxy.ExchangeProxy;
import br.com.erudio.repository.BookRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book Endpoint", description = "Endpoint to retrieve book information")
@RestController
@RequestMapping("book-service")
public class BookController {

    @Autowired
    private InstanceInformationService instanceInformationService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ExchangeProxy proxy;

    @Operation(summary = "Find a book by Id", description = "Find a book by Id")
    @GetMapping(value = "/{id}/{currency}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book findBook(
            @PathVariable("id") Long id,
            @PathVariable("currency") String currency) {

        String port  = instanceInformationService.retrieveServerPort();

        var book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));

        Exchange exchange = proxy.getExchange(book.getPrice(), "USD", currency);

        book.setEnvironment("Book port: " + port + " Exchange Port: " + exchange.getEnvironment());
        //book.setEnvironment(port + " FEIGN");
        book.setPrice(exchange.getConvertedValue());
        book.setCurrency(currency);
        return book;
    }


    /*
    @GetMapping(value = "/{id}/{currency}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book findBook(
            @PathVariable("id") Long id,
            @PathVariable("currency") String currency) {

        String port  = instanceInformationService.retrieveServerPort();

        var book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));

        HashMap<String, String> params = new HashMap<>();
        params.put("amount", book.getPrice().toString());
        params.put("from", "USD");
        params.put("to", currency);

        var response = new RestTemplate()
                .getForEntity("http://localhost:8000/exchange-service/{amount}/{from}/{to}",
                        Exchange.class, params);
        Exchange exchange = response.getBody();

        book.setEnvironment(port);
        book.setPrice(exchange.getConvertedValue());
        book.setCurrency(currency);
        return book;
    }
    */

}

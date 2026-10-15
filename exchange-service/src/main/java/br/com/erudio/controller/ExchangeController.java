package br.com.erudio.controller;

import br.com.erudio.enviroment.InstanceInformationService;
import br.com.erudio.model.Exchange;
import br.com.erudio.repository.ExchangeRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Tag(name = "Exchange Endpoint", description = "Endpoint to retrieve exchange information")
@RestController
@RequestMapping("/exchange-service")
public class ExchangeController {

    private Logger logger = LoggerFactory.getLogger(ExchangeController.class);

    @Autowired
    InstanceInformationService instanceInformationService;

    @Autowired
    ExchangeRepository exchangeRepository;

    @GetMapping(value = "/{amount}/{from}/{to}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Exchange getExchange(@PathVariable("amount") BigDecimal amount,
                                @PathVariable("from") String from,
                                @PathVariable("to") String to) {

        logger.info("getExchange is called -> {}, {} and {}", amount, from, to);
        Exchange exchange = exchangeRepository.findByFromAndTo(from, to);

        if  (exchange == null) throw new RuntimeException("Currency Unsupported");

        BigDecimal conversionFactor = exchange.getConversionFactor();
        BigDecimal convertedValue = conversionFactor.multiply(amount);
        exchange.setConvertedValue(convertedValue);
        exchange.setEnvironment("PORT " + instanceInformationService.retrieveServerPort());

        /*
        return new Exchange(1L, from, to, BigDecimal.ONE,
                BigDecimal.ONE, "PORT " + instanceInformationService.retrieveServerPort());*/

        return exchange;

    }
}

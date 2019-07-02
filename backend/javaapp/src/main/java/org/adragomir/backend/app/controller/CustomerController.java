package org.adragomir.backend.app.controller;

import org.adragomir.backend.app.dataaccess.CustomerRepository;
import org.adragomir.backend.app.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @RequestMapping(path = "/customer", method = RequestMethod.POST)
    public void newCustomer(@RequestBody Customer customer) {
        customerRepository.save(customer);
    }
}

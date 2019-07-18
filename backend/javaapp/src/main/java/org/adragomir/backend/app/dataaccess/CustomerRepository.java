package org.adragomir.backend.app.dataaccess;

import java.util.List;

import org.adragomir.backend.app.model.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    List<Customer> findByLastName(String lastName);
}
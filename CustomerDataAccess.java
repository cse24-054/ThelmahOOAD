package com.bankingsystem;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining the necessary methods for retrieving and managing Customer data.
 * This abstracts the data source (in our case, the mock Database class).
 */
public interface CustomerDataAccess {

    /**
     * Finds a customer based on their unique login code (username).
     * @param customerCode The login code to search for.
     * @return An Optional containing the Customer if found, or empty otherwise.
     */
    Optional<Customer> findCustomerByCode(String customerCode);

    /**
     * Retrieves a list of all customers in the system.
     * @return A List of all Customer objects.
     */
    List<Customer> getAllCustomers();

    /**
     * Updates an existing customer record.
     * @param customer The Customer object containing the updated data.
     * @return True if the update was successful, false otherwise.
     */
    boolean updateCustomer(Customer customer);
}
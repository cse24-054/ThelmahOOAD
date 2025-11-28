package com.bankingsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock data layer for the banking system, implementing CustomerDataAccess.
 * Uses the Singleton pattern.
 * This class now initializes specific Account objects using the new 'Savings' class name.
 */
public class Database implements CustomerDataAccess {

    private static Database instance;
    private final Map<String, Customer> customerData;

    // Define a constant branch name for mock data initialization
    private static final String MOCK_BRANCH = "Central Branch";

    private Database() {
        this.customerData = new HashMap<>();
        initializeMockData();
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Creates and populates the map of mock customers and their specific accounts.
     * FIX: Added MOCK_BRANCH parameter to all Account subclass constructors.
     */
    private void initializeMockData() {
        // Customer 1: John Smith (Code: JSMITH, Pass: 1234)
        Customer c1 = new Customer("John", "Smith", "01/01/1980", "123-456-7890", "john@example.com", "JSMITH", "1234");
        // Savings constructor now requires branch
        c1.addAccount(new Savings("S1001", 5500.00, 0.02, MOCK_BRANCH));
        // ChequeAccount constructor now requires branch
        c1.addAccount(new ChequeAccount("C1001", 1250.75, 500.00, MOCK_BRANCH));
        // InvestmentAccount constructor now requires branch
        c1.addAccount(new InvestmentAccount("I1001", 25000.00, 0.04, MOCK_BRANCH));
        customerData.put(c1.getCustomerCode(), c1);

        // Customer 2: Jane Doe (Code: JDOE, Pass: 5678)
        Customer c2 = new Customer("Jane", "Doe", "05/15/1992", "987-654-3210", "jane@example.com", "JDOE", "5678");
        c2.addAccount(new Savings("S2002", 150.00, 0.01, MOCK_BRANCH));
        c2.addAccount(new ChequeAccount("C2002", 5000.00, 200.00, MOCK_BRANCH));
        c2.addAccount(new InvestmentAccount("I2002", 1000.00, 0.03, MOCK_BRANCH));
        customerData.put(c2.getCustomerCode(), c2);

        // Customer 3: Alice Johnson (Code: AJOHN, Pass: pass)
        Customer c3 = new Customer("Alice", "Johnson", "11/20/1975", "555-123-4567", "alice@example.com", "AJOHN", "pass");
        c3.addAccount(new Savings("S3003", 10000.00, 0.015, MOCK_BRANCH));
        c3.addAccount(new ChequeAccount("C3003", 750.00, 100.00, MOCK_BRANCH));
        c3.addAccount(new InvestmentAccount("I3003", 50000.00, 0.045, MOCK_BRANCH));
        customerData.put(c3.getCustomerCode(), c3);

        // Customer 4: Bob Williams (Code: BWILL, Pass: bob)
        Customer c4 = new Customer("Bob", "Williams", "03/10/2000", "555-987-6543", "bob@example.com", "BWILL", "bob");
        c4.addAccount(new Savings("S4004", 200.00, 0.01, MOCK_BRANCH));
        c4.addAccount(new ChequeAccount("C4004", 3500.00, 100.00, MOCK_BRANCH));
        c4.addAccount(new InvestmentAccount("I4004", 8000.00, 0.04, MOCK_BRANCH));
        customerData.put(c4.getCustomerCode(), c4);

        // Customer 5: Cathy Brown (Code: CBROWN, Pass: secure)
        Customer c5 = new Customer("Cathy", "Brown", "07/25/1988", "555-555-5555", "cathy@example.com", "CBROWN", "secure");
        c5.addAccount(new Savings("S5005", 15000.00, 0.03, MOCK_BRANCH));
        c5.addAccount(new ChequeAccount("C5005", 200.50, 200.00, MOCK_BRANCH));
        c5.addAccount(new InvestmentAccount("I5005", 1200.00, 0.04, MOCK_BRANCH));
        customerData.put(c5.getCustomerCode(), c5);

        // Customer 6: David Lee (Code: DLEE, Pass: 9876)
        Customer c6 = new Customer("David", "Lee", "12/03/1965", "555-666-7777", "david@example.com", "DLEE", "9876");
        c6.addAccount(new Savings("S6006", 75.00, 0.01, MOCK_BRANCH));
        c6.addAccount(new ChequeAccount("C6006", 1500.00, 50.00, MOCK_BRANCH));
        c6.addAccount(new InvestmentAccount("I6006", 60000.00, 0.06, MOCK_BRANCH));
        customerData.put(c6.getCustomerCode(), c6);

        // Customer 7: Eva Martinez (Code: EMAR, Pass: mypass)
        Customer c7 = new Customer("Eva", "Martinez", "02/29/1996", "555-888-9999", "eva@example.com", "EMAR", "mypass");
        c7.addAccount(new Savings("S7007", 800.00, 0.02, MOCK_BRANCH));
        c7.addAccount(new ChequeAccount("C7007", 8500.00, 300.00, MOCK_BRANCH));
        c7.addAccount(new InvestmentAccount("I7007", 3500.00, 0.05, MOCK_BRANCH));
        customerData.put(c7.getCustomerCode(), c7);

        // Customer 8: Frank Green (Code: FGREEN, Pass: admin)
        Customer c8 = new Customer("Frank", "Green", "06/18/1972", "555-111-2222", "frank@example.com", "FGREEN", "admin");
        c8.addAccount(new Savings("S8008", 3000.00, 0.02, MOCK_BRANCH));
        c8.addAccount(new ChequeAccount("C8008", 100.00, 50.00, MOCK_BRANCH));
        c8.addAccount(new InvestmentAccount("I8008", 45000.00, 0.06, MOCK_BRANCH));
        customerData.put(c8.getCustomerCode(), c8);

        // Customer 9: Grace Hall (Code: GHALL, Pass: grace)
        Customer c9 = new Customer("Grace", "Hall", "09/01/1985", "555-333-4444", "grace@example.com", "GHALL", "grace");
        c9.addAccount(new Savings("S9009", 1200.00, 0.01, MOCK_BRANCH));
        c9.addAccount(new ChequeAccount("C9009", 620.00, 150.00, MOCK_BRANCH));
        c9.addAccount(new InvestmentAccount("I9009", 900.00, 0.03, MOCK_BRANCH));
        customerData.put(c9.getCustomerCode(), c9);

        // Customer 10: Henry King (Code: HKING, Pass: king)
        Customer c10 = new Customer("Henry", "King", "04/04/1990", "555-777-8888", "henry@example.com", "HKING", "king");
        c10.addAccount(new Savings("S1010", 400.00, 0.01, MOCK_BRANCH));
        c10.addAccount(new ChequeAccount("C1010", 4000.00, 50.00, MOCK_BRANCH));
        c10.addAccount(new InvestmentAccount("I1010", 150000.00, 0.07, MOCK_BRANCH));
        customerData.put(c10.getCustomerCode(), c10);
    }

    // Database methods remain the same...

    @Override
    public Optional<Customer> findCustomerByCode(String customerCode) {
        return Optional.ofNullable(customerData.get(customerCode));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customerData.values());
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        if (customerData.containsKey(customer.getCustomerCode())) {
            customerData.put(customer.getCustomerCode(), customer);
            return true;
        }
        return false;
    }
}
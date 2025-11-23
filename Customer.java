import java.util.ArrayList;
import java.util.List;



public class Customer {

    // Attributes from the diagram: name, surname, address, and list of Accounts
    private String name;
    private String surname;
    private String address;
    private List<Account> accounts;

    /**
     Constructor for the Customer class.

     */
    public Customer (String name, String surname, String address){
        this.name = name;
        this.surname = surname;
        this.address = address;
        // Initializes the list to avoid NullPointerException when adding accounts
        this.accounts = new ArrayList<>();
    }



    /**
     * Retrieves the list of accounts held by the customer.
     * @return A List of Account objects.
     */
    public List<Account> getAccounts() {
        return this.accounts;
    }

    /**
     Adds a new account to the customer's list of accounts.
     */
    public void addAccount(Account account) {
        this.accounts.add(account);
        System.out.println(this.name + " " + this.surname + " has successfully added a new account.");
    }
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }
}



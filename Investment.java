import com.bankingsystem.Account;
import com.bankingsystem.InterestBearing;

public class Investment extends Account implements InterestBearing {

    public Investment(String accountNumber, String branch) {
        super(accountNumber, branch);
    }

    @Override
    public void applyInterest() {
        balance += balance * 0.05; // 5% interest
    }

    // Optional display method
    public void displayAccountDetails() {
        System.out.println("com.bankingsystem.Investment com.bankingsystem.Account [" + accountNumber + "]");
        System.out.println("Branch: " + branch);
        System.out.println("Current Balance: " + balance);
    }

    @Override
    public void withdraw(double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withdraw'");
    }


}







package library.entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Patron implements Serializable {

	private long patronId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private long phoneNo;
	private double finesOwing;
	
	private Map<Long, Loan> currentLoans;

	
	public Patron(String firstName, String lastName, String emailAddress, long phoneNo, long patronId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.phoneNo = phoneNo;
		this.patronId = patronId;
		
		this.currentLoans = new HashMap<>();
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Patron:  ").append(patronId).append("\n")
		  .append("  Name:  ").append(firstName).append(" ").append(lastName).append("\n")
		  .append("  Email: ").append(emailAddress).append("\n")
		  .append("  Phone: ").append(phoneNo)
		  .append("\n")
          .append(String.format("  Fines Owed :  $%.2f", finesOwing))
		  .append("\n");
		
		for (Loan loan : currentLoans.values()) {
			sb.append(loan).append("\n");
		}		  
		return sb.toString();
	}

	
	public Long getId() {
		return patronId;
	}

	
	public List<Loan> getLoans() {
		return new ArrayList<Loan>(currentLoans.values());
	}

	
	public int getNumberOfCurrentLoans() {
		return currentLoans.size();
	}

	
	public double finesOwed() {
		return finesOwing;
	}

	
	public void takeOutLoan(Loan loan) {
		if (!currentLoans.containsKey(loan.getId())) {
			currentLoans.put(loan.getId(), loan);
		}
		else {
			throw new RuntimeException("Duplicate loan added to member");
		}
	}

	
	public void dischargeLoan(Loan loan) {
	    long loanId = loan.getId();
		if (currentLoans.containsKey(loanId)) {
		    finesOwing += loan.getFines();
			currentLoans.remove(loanId);
		}
		else {
			throw new RuntimeException("No such loan held by member");
		}
	}

	
	public String getLastName() {
		return lastName;
	}

	
	public String getFirstName() {
		return firstName;
	}


	public void incurFine(double fine) {
		finesOwing += fine;
	}
	
	
	public double payFine(double amount) {
		if (amount < 0) {
			throw new RuntimeException("Member.payFine: amount must be positive");
		}
		double change = 0;
		if (amount > finesOwing) {
			change = amount - finesOwing;
			finesOwing = 0;
		}
		else {
			finesOwing -= amount;
		}
		return change;
	}

}

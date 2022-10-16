package library.entities;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Loan implements Serializable {
	
	private enum LoanState { CURRENT, OVER_DUE, DISCHARGED };
	
	private long loanId;
	private Item item;
	private Patron patron;
	private Date dueDate;
	private double fines;
	private LoanState state;

	
	public Loan(long loanId, Item item, Patron patron, Date dueDate) {
		this.loanId = loanId;
		this.item = item;
		this.patron = patron;
		this.dueDate = dueDate;
		this.state = LoanState.CURRENT;
	}

	
    public void discharge(boolean isDamaged) {
        patron.dischargeLoan(this);
        item.takeBack(isDamaged);
        state = LoanState.DISCHARGED;       
    }


	public void updateStatus(double overDueFeePerDay) {
		if (state == LoanState.CURRENT &&
			Calendar.getInstance().getDate().after(dueDate)) {
			this.state = LoanState.OVER_DUE;
		}
		if (isOverDue()) {
            long daysOverDue = Calendar.getInstance().getDaysDifference(dueDate);
            fines = overDueFeePerDay * daysOverDue;
		}
	}

	
	public boolean isOverDue() {
		return state == LoanState.OVER_DUE;
	}

	
	public Long getId() {
		return loanId;
	}


	public Date getDueDate() {
		return dueDate;
	}
	
	
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder sb = new StringBuilder();
		sb.append("Loan:  ").append(loanId).append("\n")
		  .append("  Borrower ").append(patron.getId()).append(" : ")
		  .append(patron.getFirstName()).append(" ").append(patron.getLastName()).append("\n")
		  .append("  Item ").append(item.getId()).append(" : " )
		  .append(item.getType()).append("\n")
		  .append(item.getTitle()).append("\n")
		  .append("  DueDate: ").append(sdf.format(dueDate)).append("\n")
		  .append("  State: ").append(state).append("\n")
		  .append("  Fines: ").append(String.format("$%.2f", fines));	
		
		return sb.toString();
	}


	public Patron getPatron() {
		return patron;
	}


	public Item getItem() {
		return item;
	}


    public double getFines() {
         return fines;
    }

}

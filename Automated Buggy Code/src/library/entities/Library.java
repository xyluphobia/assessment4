package library.entities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Library implements Serializable {
	
	private static final String LIBRARY_FILE = "library.obj";
	private static final int LOAN_LIMIT = 2;
	private static final int LOAN_PERIOD = 2;
	private static final double OVERDUE_FINE_PER_DAY = 1.0;
	private static final double MAX_FINES_OWED = 1.0;
	private static final double DAMAGE_FEE = 2.0;
	
	private static Library self;
	private long nextItemId;
	private long nextPatronId;
	private long nextLoanId;
	private Date currentDate;
	
	private Map<Long, Item> catalog;
	private Map<Long, Patron> patrons;
	private Map<Long, Loan> loans;
	private Map<Long, Loan> currentLoans;
	private Map<Long, Item> damagedItems;
	

	private Library() {
		catalog = new HashMap<>();
		patrons = new HashMap<>();
		loans = new HashMap<>();
		currentLoans = new HashMap<>();
		damagedItems = new HashMap<>();
		nextItemId = 1;
		nextPatronId = 1;		
		nextLoanId = 1;		
	}

	
	public static synchronized Library getInstance() {		
		if (self == null) {
			Path path = Paths.get(LIBRARY_FILE);			
			if (Files.exists(path)) {	
				try (ObjectInputStream libraryFile = new ObjectInputStream(new FileInputStream(LIBRARY_FILE));) {
			    
					self = (Library) libraryFile.readObject();
					Calendar.getInstance().setDate(self.currentDate);
					libraryFile.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else {
			    self = new Library();
			}
		}
		return self;
	}

	
	public static synchronized void save() {
		if (self != null) {
			self.currentDate = Calendar.getInstance().getDate();
			try (ObjectOutputStream libraryFile = new ObjectOutputStream(new FileOutputStream(LIBRARY_FILE));) {
				libraryFile.writeObject(self);
				libraryFile.flush();
				libraryFile.close();	
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	private long getNextItemId() {
		return nextItemId++;
	}

	
	private long getNextPatronId() {
		return nextPatronId++;
	}

	
	private long getNextLoanId() {
		return nextLoanId++;
	}

	
	public List<Patron> listPatrons() {		
		return new ArrayList<Patron>(patrons.values()); 
	}


	public List<Item> listItems() {		
		return new ArrayList<Item>(catalog.values()); 
	}


	public List<Loan> listCurrentLoans() {
		return new ArrayList<Loan>(currentLoans.values());
	}


	public Patron addPatron(String firstName, String lastName, String email, long phoneNo) {		
		Patron patron = new Patron(firstName, lastName, email, phoneNo, getNextPatronId());
		patrons.put(patron.getId(), patron);		
		return patron;
	}

	
	public Item addItem(String author, String title, String callNo, ItemType itemType) {		
		Item item = new Item(author, title, callNo, itemType, getNextItemId());
		catalog.put(item.getId(), item);		
		return item;
	}

	
	public Patron getPatron(long patronId) {
		if (patrons.containsKey(patronId)) {
			return patrons.get(patronId);
		}
		return null;
	}

	
	public Item getItem(long itemId) {
		if (catalog.containsKey(itemId)) {
			return catalog.get(itemId);
		}
		return null;
	}

	
	public int getLoanLimit() {
		return LOAN_LIMIT;
	}

	
	public boolean canPatronBorrow(Patron patron) {		
		if (patron.getNumberOfCurrentLoans() == LOAN_LIMIT ) {
			return false;
		}
		if (patron.finesOwed() >= MAX_FINES_OWED) {
			return false;
		}
		for (Loan loan : patron.getLoans()) {
			if (loan.isOverDue()) {
				return false;
			}
		}	
		return true;
	}

	
	public int getNumberOfLoansRemainingForPatron(Patron patron) {		
		return LOAN_LIMIT - patron.getNumberOfCurrentLoans();
	}

	
	public Loan issueLoan(Item item, Patron patron) {
		Date dueDate = Calendar.getInstance().getDueDate(LOAN_PERIOD);
		Loan loan = new Loan(getNextLoanId(), item, patron, dueDate);
		patron.takeOutLoan(loan);
		item.takeOut();
		loans.put(loan.getId(), loan);
		currentLoans.put(item.getId(), loan);
		return loan;
	}
	
	
	public Loan getLoanByItemId(long itemId) {
		if (currentLoans.containsKey(itemId)) {
			return currentLoans.get(itemId);
		}
		return null;
	}

	
	public double calculateOverDueFine(Loan loan) {
		if (loan.isOverDue()) {
		    Date dueDate = loan.getDueDate();
			long daysOverDue = Calendar.getInstance().getDaysDifference(dueDate);
			double fine = daysOverDue * OVERDUE_FINE_PER_DAY;
			return fine;
		}
		return 0.0;		
	}

	
	public double calculateDamageFee(boolean isDamaged) {
	    if (isDamaged) {
	        return DAMAGE_FEE;
	    }
	    return 0.0;
	}


	public void dischargeLoan(Loan currentLoan, boolean isDamaged) {
		Patron patron = currentLoan.getPatron();
		Item item  = currentLoan.getItem();
		long itemId = item.getId();

		if (currentLoan.isOverDue()) {
	        double overDueFine = currentLoan.getFines();      
	        patron.incurFine(overDueFine); 		    
		}
		if (isDamaged) {
		    patron.incurFine(DAMAGE_FEE);
            damagedItems.put(itemId, item);
		}
		currentLoan.discharge(isDamaged);
		currentLoans.remove(itemId);
	}


	public void updateCurrentLoanStatus() {
		for (Loan loan : currentLoans.values()) {
			loan.updateStatus(OVERDUE_FINE_PER_DAY);
		}
	}


	public void repairItem(Item currentItem) {
	    long itemId = currentItem.getId();
		if (damagedItems.containsKey(itemId)) {
			currentItem.repair();
			damagedItems.remove(itemId);
		}
		else {
			throw new RuntimeException("Library: repairItem: item is not damaged");
		}
	}
	
	
}

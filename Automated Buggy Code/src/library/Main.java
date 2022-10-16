package library;

import java.text.SimpleDateFormat;
import java.util.Scanner;

import library.entities.Item;
import library.entities.ItemType;
import library.borrowitem.BorrowItemUI;
import library.borrowitem.BorrowItemControl;
import library.entities.Calendar;
import library.entities.Library;
import library.entities.Loan;
import library.entities.Patron;
import library.fixitem.FixItemUI;
import library.fixitem.FixItemControl;
import library.payfine.PayFineUI;
import library.payfine.PayFineControl;
import library.returnItem.ReturnItemUI;
import library.returnItem.ReturnItemControl;


public class Main {
	
	private static Scanner scanner;
	private static Library library;
	private static Calendar calendar;
	private static SimpleDateFormat simpleDateFormat;
	
	private static String MENU = """
		Library Main Menu
		
			AP  : add patron
			LP : list patrons
		
			AI  : add item
			LI : list items
			FI : fix item
		
			B  : borrow an item
			R  : return an item
			L  : list loans
		
			P  : pay fine
		
			T  : increment date
			Q  : quit
		
		Choice : 
		""";		

	
	public static void main(String[] args) {		
		try {			
			scanner = new Scanner(System.in);
			library = Library.getInstance();
			calendar = Calendar.getInstance();
			simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
			for (Patron patron : library.listPatrons()) {
				output(patron);
			}
			output(" ");
			for (Item item : library.listItems()) {
				output(item);
			}
						
			boolean finished = false;
			
			while (!finished) {
				
				output("\n" + simpleDateFormat.format(calendar.getDate()));
				String choice = input(MENU);
				
				switch (choice.toUpperCase()) {
				
				case "AP": 
					addPatron();
					break;
					
				case "LP": 
					listPatrons();
					break;
					
				case "AI": 
					addItem();
					break;
					
				case "LI": 
					listItems();
					break;
					
				case "FI": 
					fixItems();
					break;
					
				case "B": 
					borrowItems();
					break;
					
				case "R": 
					returnItems();
					break;
					
				case "L": 
					listCurrentLoans();
					break;
					
				case "P": 
					payFines();
					break;
					
				case "T": 
					incrementDate();
					break;
					
				case "Q": 
					finished = true;
					break;
					
				default: 
					output("\nInvalid option\n");
					break;
				}
				
				Library.save();
			}			
		} catch (RuntimeException e) {
			output(e);
		}		
		output("\nEnded\n");
	}	

	
	private static void payFines() {
		new PayFineUI(new PayFineControl()).run();		
	}


	private static void listCurrentLoans() {
		output("");
		for (Loan loan : library.listCurrentLoans()) {
			output(loan + "\n");
		}		
	}



	private static void listItems() {
		output("");
		for (Item book : library.listItems()) {
			output(book + "\n");
		}		
	}



	private static void listPatrons() {
		output("");
		for (Patron member : library.listPatrons()) {
			output(member + "\n");
		}		
	}



	private static void borrowItems() {
		new BorrowItemUI(new BorrowItemControl()).run();		
	}


	private static void returnItems() {
		new ReturnItemUI(new ReturnItemControl()).run();		
	}


	private static void fixItems() {
		new FixItemUI(new FixItemControl()).run();		
	}


	private static void incrementDate() {
		try {
			int days = 2;
			calendar.incrementDate(days);
			library.updateCurrentLoanStatus();
			output(simpleDateFormat.format(calendar.getDate()));
			
		} catch (NumberFormatException e) {
			 output("\nInvalid number of days\n");
		}
	}


	private static void addItem() {
		
		ItemType itemType = ItemType.BOOK;
		
		String author = "Example Author";
		String title  = "Example Title";
		String callNo = "Example CallNo";
		Item item = library.addItem(author, title, callNo, itemType);
		output("\n" + item + "\n");
		
	}

	
	private static void addPatron() {
		try {
			String firstName  = "Matthew";
			String lastName = "Scavone";
			String emailAddress = "mattscavone11@gmail.com";
			long phoneNo = Long.valueOf("0459741190").intValue();
			Patron patron = library.addPatron(firstName, lastName, emailAddress, phoneNo);
			output("\n" + patron + "\n");
			
		} catch (NumberFormatException e) {
			 output("\nInvalid phone number\n");
		}
		
	}


	private static String input(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine();
	}
	
	
	
	private static void output(Object object) {
		System.out.println(object);
	}

	
}
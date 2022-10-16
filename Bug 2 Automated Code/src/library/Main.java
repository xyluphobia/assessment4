package library;

import java.text.SimpleDateFormat;

import library.entities.Item;
import library.entities.ItemType;
import library.borrowitem.BorrowItemUI;
import library.borrowitem.BorrowItemControl;
import library.entities.Calendar;
import library.entities.Library;
import library.entities.Loan;
import library.entities.Patron;


public class Main {
	
	private static Library library;
	private static Calendar calendar;
	private static SimpleDateFormat simpleDateFormat;
	public static String responseId = "1";
	public static String responseItem = "1";
	public static String responseCommit = "Y";

	public static void main(String[] args) {		
		try {			
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
				
			output("\n" + simpleDateFormat.format(calendar.getDate()));

			addPatron("Matthew", "Scavone", "mattscavone11@gmail.com");

			addItem("A1", "T1", "N1");
			addItem("A2", "T2", "N2");
			addItem("A3", "T3", "N3");
			addItem("A4", "T4", "N4");
			addItem("A5", "T5", "N5");

			borrowItems();

			borrowItems();

			System.out.println("\nCurrent Loans");

			listCurrentLoans();

		
		} catch (RuntimeException e) {
			output(e);
		}		
		output("\nEnded\n");
	}	

	public static String getResponseId() {
		return responseId;
	}

	public static String getResponseItem() {
		return responseItem;
	}

	public static void incrementResponseItem() {
		int newValue = Integer.parseInt(responseItem) + 1;
		responseItem = Integer.toString(newValue);
	}

	public static String getResponseCommit() {
		return responseCommit;
	}

	private static void listCurrentLoans() {
		output("");
		for (Loan loan : library.listCurrentLoans()) {
			output(loan + "\n");
		}		
	}

	private static void borrowItems() {
		new BorrowItemUI(new BorrowItemControl()).run();		
	}

	private static void addItem(String author, String title, String callNo) {
		ItemType itemType = ItemType.BOOK;
		library.addItem(author, title, callNo, itemType);
	}
	
	private static void addPatron(String firstName, String lastName, String emailAddress) {
		long phoneNo = Long.valueOf("0459741190").intValue();
		library.addPatron(firstName, lastName, emailAddress, phoneNo);
	}
	
	private static void output(Object object) {
		System.out.println(object);
	}
}

package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the methods for each of the menu options.
 * 
 * This file should not need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove methods as you see necessary. But you MUST have all of the menu methods (including exit!)
 * 
 * Simply removing menu methods because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 */

public class Menu {

	public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws SQLException, IOException {

		System.out.println("Welcome to Pizzas-R-Us!");
		
		int menu_option = 0;

		// present a menu of options and take their selection
		
		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
			case 1:// enter order
				EnterOrder();
				break;
			case 2:// view customers
				viewCustomers();
				break;
			case 3:// enter customer
				EnterCustomer();
				break;
			case 4:// view order
				// open/closed/date
				ViewOrders();
				break;
			case 5:// mark order as complete
				MarkOrderAsComplete();
				break;
			case 6:// view inventory levels
				ViewInventoryLevels();
				break;
			case 7:// add to inventory
				AddInventory();
				break;
			case 8:// view reports
				PrintReports();
				break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException 
	{

		/*
		 * EnterOrder should do the following:
		 *
		 * Ask if the order is delivery, pickup, or dinein
		 *   if dine in....ask for table number
		 *   if pickup...
		 *   if delivery...
		 * 
		 * Then, build the pizza(s) for the order (there's a method for this)
		 *  until there are no more pizzas for the order
		 *  add the pizzas to the order
		 *
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * return to menu
		 * 
		 * make sure you use the prompts below in the correct order!
		 */

		//create a new order object
		Order newOrder = new Order(0,1, "", "", 0.00,0.00,0);

		//This is another way to get OrderID
		//Order lastOrder = DBNinja.getLastOrder();
		//int orderID = (lastOrder != null) ? lastOrder.getOrderID() + 1 : 1;
		//newOrder.setOrderID(orderID);

		//insert this order to DB in order to get an OrderID
		DBNinja.addOrder(newOrder);
		int orderID = newOrder.getOrderID();

		//get user input for ordertype
		System.out.println("Is this order for: \n1.) Dine-in\n2.) Pick-up\n3.) Delivery\nEnter the number of your choice:");
		int orderType = Integer.parseInt(reader.readLine());

		//for dineinOrder, set tableNum and orderType
		if(orderType == 1){
			System.out.println("What is the table number for this order?");
			int tableNum = Integer.parseInt(reader.readLine());
			newOrder = new DineinOrder(orderID, 1, "", 0.00, 0.00, 0, tableNum);
			newOrder.setOrderType("dinein");

		//for pickup order
		}else if(orderType == 2) {
			System.out.println("Is this order for an existing customer? Answer y/n: ");
			String customer = reader.readLine();

			//for existing customer, set CusID and orderType
			if (customer.equals("y")) {
				System.out.println("Here's a list of the current customers: ");
				ArrayList<Customer> customers = DBNinja.getCustomerList();
				if(customers != null) {
					for (Customer c : customers) {
						System.out.println(c.toString());
					}
				}
				System.out.println("Which customer is this order for? Enter ID Number:");
				int customerID = Integer.parseInt(reader.readLine());
				newOrder = new PickupOrder(orderID, customerID, "", 0.00, 0.00, 1, 0);
				newOrder.setOrderType("pickup");

			//for pick up new customer, only name and phone needed
			} else if (customer.equals("n")) {
				//get customer information
				System.out.println("Please enter the customer name (First Name <space> Last Name):");
				String custName = reader.readLine();
				System.out.println("What is customer's phone number (xxxxxxxxxx) (No dash/space):");
				String custPhone = reader.readLine();
				//System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
				//int houseNum = Integer.parseInt(reader.readLine());
				//System.out.println("What is the Street for this order? (e.g., Smile Street)");
				//String street = reader.readLine();
				//System.out.println("What is the City for this order? (e.g., Greenville)");
				//String city = reader.readLine();
				//System.out.println("What is the State for this order? (e.g., SC)");
				//String state = reader.readLine();
				//System.out.println("What is the Zip Code for this order? (e.g., 20605)");
				//String zip = reader.readLine();
				//String fullAddress = houseNum + " " + street;

				//split name string to 2
				String[] names = custName.split(" ", 2);
				String fName = "";
				String lName = "";
				if (names.length == 2) {
					fName = names[0];
					lName = names[1];
				}else{
					System.out.println("ERROR: Invalid format");
				}

				//create new customer object, set CusID to -1 as we will retrieve the autokey from DB later
				Customer newCust = new Customer(-1, fName, lName, custPhone);
				//set address to empty
				newCust.setAddress("", "", "", "");

				//add to SQL DB
				DBNinja.addCustomer(newCust);

				//now we get custID, set OrderID, OrderType and CustID to this order
				int custID = newCust.getCustID();
				newOrder = new PickupOrder(orderID, custID, "", 0.00, 0.00, 1, 0);
				newOrder.setOrderType("pickup");
			}

			//for delivery order, address is required
		}else if(orderType == 3) {
			System.out.println("Is this order for an existing customer? Answer y/n: ");
			String customer = reader.readLine();

			//for existing customer, we need to check if we have his address information
			if (customer.equals("y")) {
				System.out.println("Here's a list of the current customers: ");
				ArrayList<Customer> customers = DBNinja.getCustomerList();
				if (customers != null) {
					for (Customer c : customers) {
						System.out.println(c.toString());
					}
				}

				System.out.println("Which customer is this order for? Enter ID Number:");
				int customerID = Integer.parseInt(reader.readLine());
				Customer selectedCust = null;
				if (customers != null) {
					for (Customer c: customers) {
						if (customerID == c.getCustID()) {
							selectedCust = c;
							break;
						}
					}
				}

				String Address = null;
				if (selectedCust != null) {
					Address = selectedCust.getAddress();
				}

				//In case customer's address is missing, complete missing info
				if (Address == null || Address.isEmpty()) {
					System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
					int houseNum = Integer.parseInt(reader.readLine());
					System.out.println("What is the Street for this order? (e.g., Smile Street)");
					String street = reader.readLine();
					System.out.println("What is the City for this order? (e.g., Greenville)");
					String city = reader.readLine();
					System.out.println("What is the State for this order? (e.g., SC)");
					String state = reader.readLine();
					System.out.println("What is the Zip Code for this order? (e.g., 20605)");
					String zip = reader.readLine();
					String streetAddress = houseNum + " " + street;
					selectedCust.setAddress(streetAddress, city, state, zip);

					//update DB, DB will check if customer exist, if existed, it will run an update query for address
					DBNinja.addCustomer(selectedCust);
				}

				//set new order's OrderID, CusID, Address, OrderType
				newOrder = new DeliveryOrder(orderID, customerID, "", 0.00, 0.00, 0, selectedCust.getAddress());
				newOrder.setOrderType("delivery");

			//for new customer, create a new customer object and insert into DB
			} else if (customer.equals("n")) {
				//get customer information
				System.out.println("Please enter the customer name (First Name <space> Last Name):");
				String custName = reader.readLine();
				System.out.println("What is customer's phone number (xxxxxxxxxx) (No dash/space):");
				String custPhone = reader.readLine();
				System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
				int houseNum = Integer.parseInt(reader.readLine());
				System.out.println("What is the Street for this order? (e.g., Smile Street)");
				String street = reader.readLine();
				System.out.println("What is the City for this order? (e.g., Greenville)");
				String city = reader.readLine();
				System.out.println("What is the State for this order? (e.g., SC)");
				String state = reader.readLine();
				System.out.println("What is the Zip Code for this order? (e.g., 20605)");
				String zip = reader.readLine();
				String streetAddress = houseNum + " " + street;

				//split name string to 2
				String[] names = custName.split(" ", 2);
				String fName = "";
				String lName = "";
				if (names.length == 2) {
					fName = names[0];
					lName = names[1];
				}else{
					System.out.println("ERROR: Invalid format");
				}

				//create new customer object
				Customer newCust = new Customer(-1,fName, lName, custPhone);
				newCust.setAddress(streetAddress, city, state, zip);
				//add to SQL DB
				DBNinja.addCustomer(newCust);
				int custID = newCust.getCustID();

				//set OrderID, CusID, Address and OrderType
				newOrder = new DeliveryOrder(orderID, custID, "", 0.00, 0.00, 0, newCust.getAddress());
				newOrder.setOrderType("delivery");

			} else {
				System.out.println("ERROR: I don't understand your input for: Is this order an existing customer?");
			}
		}

		//start buildpizza;
		System.out.println("Let's build a pizza!");
		//add first pizza
		Pizza newPizza;
		newPizza = buildPizza(orderID);

		//add pizza to pizzalist
		newOrder.addPizza(newPizza);

		//add this pizza's price to order
		double custPrice = 0;
		double busPrice = 0;
		custPrice += newPizza.getCustPrice();
		busPrice += newPizza.getBusPrice();
		newOrder.setCustPrice(custPrice);
		newOrder.setBusPrice(busPrice);
		System.out.println("Enter -1 to stop adding pizzas...Enter anything else to continue adding pizzas to the order.");
		int addingPizza = Integer.parseInt(reader.readLine());

		//check if more pizza should be added
		//if more pizza added, add all pizza price as order price
		//and add pizza to order's pizzaList
		while(addingPizza != -1) {
			newPizza = buildPizza(orderID);
			newOrder.addPizza(newPizza);
			custPrice += newPizza.getCustPrice();
			busPrice += newPizza.getBusPrice();
			newOrder.setCustPrice(custPrice);
			newOrder.setBusPrice(busPrice);
			System.out.println("Enter -1 to stop adding pizzas...Enter anything else to continue adding pizzas to the order.");
			addingPizza = Integer.parseInt(reader.readLine());
		}


		//discounts to order
		ArrayList<Discount> allDiscounts = DBNinja.getDiscountList();
		Discount selectedDis = null;
		System.out.println("Do you want to add discounts to this order? Enter y/n?");
		String orderDisOption = reader.readLine();

		//if discount to order applies
		if(orderDisOption.equals("y")){
			for(Discount d: allDiscounts){
				System.out.println(d.toString());
			}
			System.out.println("Which Order Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
			int orderDisID = Integer.parseInt(reader.readLine());

			if(orderDisID == -1){
				System.out.println("Invalid Option");
				return;
			}

			while(orderDisID != -1){
				for(Discount d: allDiscounts){
					if(d.getDiscountID() == orderDisID){
						selectedDis = d;
						break;
					}
				}
				//add discount to order's discountList
				newOrder.addDiscount(selectedDis);
				for(Discount d: allDiscounts){
					System.out.println(d.toString());
				}
				System.out.println("Which Order Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
				orderDisID = Integer.parseInt(reader.readLine());
			}
		}

		//update order in DB with completed order infor
		DBNinja.addOrder(newOrder);
		System.out.println("Finished adding order...Returning to menu...");
	}


	public static void viewCustomers() throws SQLException, IOException
	{
		/*
		 * Simply print out all of the customers from the database. 
		 */
		try {
			ArrayList<Customer> customers = DBNinja.getCustomerList();
			for(Customer customer : customers){
				System.out.println(customer.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}

	}
	

	// Enter a new customer in the database
	public static void EnterCustomer() throws SQLException, IOException 
	{
		/*
		 * Ask for the name of the customer:
		 *   First Name <space> Last Name
		 * 
		 * Ask for the  phone number.
		 *   (##########) (No dash/space)
		 * 
		 * Once you get the name and phone number, add it to the DB
		 */
		try {
			String phoneNum = "";
			String fName = "";
			String lName = "";

			System.out.println("Please Enter the Customer name (First Name <space> Last Name)");
			String custName = reader.readLine();

			String[] names = custName.split(" ", 2);

			if (names.length == 2) {
				fName = names[0];
				lName = names[1];
			}else{
				System.out.println("ERROR: Invalid format");
			}

			System.out.println("What is this customer's phone number (##########) (No dash/space)");
            phoneNum = reader.readLine();

			if(phoneNum.length() != 10)
				System.out.println("Invalid format");

			Customer newCustomer = new Customer(-1, fName, lName, phoneNum);
			newCustomer.setAddress("","","","");

			//add to SQL DB, a custID will be auto generated by mySQL
			DBNinja.addCustomer(newCustomer);

		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}

	}

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException
	{
		/*  
		* This method allows the user to select between three different views of the Order history:
		* The program must display:
		* a.	all open orders
		* b.	all completed orders 
		* c.	all the orders (open and completed) since a specific date (inclusive)
		* 
		* After displaying the list of orders (in a condensed format) must allow the user to select a specific order for viewing its details.  
		* The details include the full order type information, the pizza information (including pizza discounts), and the order discounts.
		* 
		*/

		System.out.println("Would you like to:\n(a) display all orders [open or closed]\n(b) display all open orders\n(c) display all completed [closed] orders\n(d) display orders since a specific date");
		String viewOption = reader.readLine();

		//create an arrayList to hold selected orders
		ArrayList<Order> ordersToDisplay;
		ArrayList<Order> closedOrders = new ArrayList<>();
		switch (viewOption) {
			case "a":
				ordersToDisplay = DBNinja.getOrders(false);
				break;
			case "b":
				ordersToDisplay = DBNinja.getOrders(true);
				break;
			case "c":
				ordersToDisplay = DBNinja.getOrders(false);
				for(Order o: ordersToDisplay){
					if(o.getIsComplete() == 1){
						closedOrders.add(o);
					}
				}
				ordersToDisplay = closedOrders;
				break;
			case "d":
				System.out.println("What is the date you want to restrict by? (FORMAT= YYYY-MM-DD)");
				String date = reader.readLine();
				ordersToDisplay = DBNinja.getOrdersByDate(date);
				break;
			default:
				System.out.println("Invalid input");
				return;
		}

		if (ordersToDisplay.isEmpty()) {
			System.out.println("No orders to display.");
			return;
		}

		for (Order o : ordersToDisplay) {
			System.out.println(o.toSimplePrint());
		}

		System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
		int orderID = Integer.parseInt(reader.readLine());
		Order selectedOrder = null;
		if (orderID != -1) {
			for (Order order : ordersToDisplay) {
				if (order.getOrderID() == orderID) {
					selectedOrder = order;
					break;
				}
			}

			// Print order info
			System.out.println(selectedOrder);

			// Print order discounts
			if (!selectedOrder.getDiscountList().isEmpty()) {
				System.out.println("ORDER DISCOUNTS: ");
				for (Discount orderDis : selectedOrder.getDiscountList()) {
					System.out.println(orderDis.getDiscountName());
				}
			} else {
				System.out.println("NO ORDER DISCOUNTS");
			}

			// Print pizza list
			ArrayList<Pizza> pizzas = selectedOrder.getPizzaList();
			for (Pizza pizza : pizzas) {
				System.out.println(pizza.toString());

				// Print pizza discounts
				if (pizza.getDiscounts().isEmpty()) {
					System.out.println("NO PIZZA DISCOUNTS");
					return;
				} else {
					System.out.println("PIZZA DISCOUNTS: ");
					for (Discount pizzaDis : pizza.getDiscounts()) {
						System.out.println(pizzaDis.getDiscountName());
					}
				}
			}
		}
		//System.out.println("Incorrect entry, returning to menu.");
	}

	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException
	{
		/*
		 * All orders that are created through java (part 3, not the orders from part 2) should start as incomplete
		 * 
		 * When this method is called, you should print all of the "opoen" orders marked
		 * and allow the user to choose which of the incomplete orders they wish to mark as complete
		 * 
		 */

		ArrayList<Order> openOrders = DBNinja.getOrders(true);
		if(openOrders.isEmpty()){
			System.out.println("No open orders");
			return;
		}else {
			for (Order openOrder : openOrders) {
				System.out.println(openOrder.toSimplePrint());
			}
		}

		System.out.println("Which order would you like mark as complete? Enter the OrderID: ");
		int orderID = Integer.parseInt(reader.readLine());

		for(Order openOrder: openOrders) {
			if(orderID == openOrder.getOrderID()){
				DBNinja.completeOrder(openOrder);
				break;
			}
		}

	}

	public static void ViewInventoryLevels() throws SQLException, IOException 
	{
		/*
		 * Print the inventory. Display the topping ID, name, and current inventory
		*/
		DBNinja.printInventory();

	}


	public static void AddInventory() throws SQLException, IOException 
	{
		/*
		 * This should print the current inventory and then ask the user which topping (by ID) they want to add more to and how much to add
		 */

		ViewInventoryLevels();
		System.out.println("Which topping do you want to add inventory to? Enter the number: ");
		int topID = Integer.parseInt(reader.readLine());
		Topping selectedTop;
		selectedTop = null;
		try {
			for (Topping t : DBNinja.getToppingList()) {
				if (topID == t.getTopID()) {
					selectedTop = t;
					break;
				}
			}
		}catch(SQLException e){
			System.out.println("Invalid input: " + e.getMessage());
		}

		System.out.println("How many units would you like to add? ");
		try {
			int units = Integer.parseInt(reader.readLine());
			if (selectedTop != null) {
				DBNinja.addToInventory(selectedTop, units);
			}
		} catch(SQLException e){
			System.out.println("Invalid input: " + e.getMessage());
		}
	}

	// A method that builds a pizza. Used in our add new order method
	public static Pizza buildPizza(int orderID) throws SQLException, IOException 
	{
		
		/*
		 * This is a helper method for first menu option.
		 * 
		 * It should ask which size pizza the user wants and the crustType.
		 * 
		 * Once the pizza is created, it should be added to the DB.
		 * 
		 * We also need to add toppings to the pizza. (Which means we not only need to add toppings here, but also our bridge table)
		 * 
		 * We then need to add pizza discounts (again, to here and to the database)
		 * 
		 * Once the discounts are added, we can return the pizza
		 */

		//create pizza object
		Pizza ret = new Pizza(0, " ", " ", 0, "", "", 0.00, 0.00);
		//get orderID
		ret.setOrderID(orderID);
		//set pizza states;
		ret.setPizzaState("being processed");
		//first insert to pizza table, to retrieve a pizzaID for adding toppings
		DBNinja.addPizza(ret);
		int pizzaID = ret.getPizzaID();
		ret.setPizzaID(pizzaID);

		System.out.println("What size is the pizza?");
		System.out.println("1."+DBNinja.size_s);
		System.out.println("2."+DBNinja.size_m);
		System.out.println("3."+DBNinja.size_l);
		System.out.println("4."+DBNinja.size_xl);
		System.out.println("Enter the corresponding number: ");
		int sizeOption = Integer.parseInt(reader.readLine());

		//get size
		String size = "";
		switch (sizeOption) {
			case 1:
				size = DBNinja.size_s;
				break;
			case 2:
				size = DBNinja.size_m;
				break;
			case 3:
				size = DBNinja.size_l;
				break;
			case 4:
				size = DBNinja.size_xl;
				break;
			default:
				System.out.println("Invalid option. Please enter a number between 1 and 4.");
		}
		ret.setSize(size);

		//get crustType
		System.out.println("What crust for this pizza?");
		System.out.println("1."+DBNinja.crust_thin);
		System.out.println("2."+DBNinja.crust_orig);
		System.out.println("3."+DBNinja.crust_pan);
		System.out.println("4."+DBNinja.crust_gf);
		System.out.println("Enter the corresponding number: ");
		int crustOption = Integer.parseInt(reader.readLine());
		String crust = "";
		switch (crustOption) {
			case 1:
				crust = DBNinja.crust_thin;
				break;
			case 2:
				crust = DBNinja.crust_orig;
				break;
			case 3:
				crust = DBNinja.crust_pan;
				break;
			case 4:
				crust = DBNinja.crust_gf;
				break;
			default:
				System.out.println("Invalid option. Please enter a number between 1 and 4.");
		}
		ret.setCrustType(crust);

		//get custprice
		double custPrice = DBNinja.getBaseCustPrice(size, crust);
		ret.setCustPrice(custPrice);

		//get busprice
		double busPrice = DBNinja.getBaseBusPrice(size, crust);
		ret.setBusPrice(busPrice);

		//get toppings list
		ArrayList<Topping> allToppings = DBNinja.getToppingList();
		DBNinja.printInventory();
		System.out.println("Which topping do you want to add? Enter the TopID. Enter -1 to stop adding toppings: ");
		int toppingOption = Integer.parseInt(reader.readLine());

		while(toppingOption != -1) {
			Topping selectedTopping = null;
			for(Topping t: allToppings){
				if(t.getTopID() == toppingOption){
					selectedTopping = t;
					break;
				}
			}

			//we need to make sure inventory does not go negative
			double usage = 0.00;
			if(size.equals(DBNinja.size_s)) {
				usage = selectedTopping.getPerAMT();
			}else if(size.equals(DBNinja.size_m)){
				usage = selectedTopping.getMedAMT();
			}else if(size.equals(DBNinja.size_l)){
				usage = selectedTopping.getLgAMT();
			}else if(size.equals(DBNinja.size_xl)){
				usage = selectedTopping.getXLAMT();
			}

			if(selectedTopping.getCurINVT()<usage){
				System.out.println("Sorry, selectedTopping is out of Stock");
			}

			System.out.println("Do you want to add extra topping? Enter y/n");
			String extraOption = reader.readLine();

			if(extraOption.equals("y")){
				usage *= 2;
				if(selectedTopping.getCurINVT()<usage){
					System.out.println("Sorry, selectedTopping is out of Stock");
				}
				ret.addToppings(selectedTopping, true);
				ret.modifyDoubledArray(ret.getToppings().size()-1, true);
			}else if(extraOption.equals("n")){
				ret.addToppings(selectedTopping, false);
				ret.modifyDoubledArray(ret.getToppings().size()-1, false);
			}

			System.out.println("Available Toppings");
			DBNinja.printInventory();
			System.out.println("Which topping do you want to add? Enter the top ID. Enter -1 to stop adding toppings:");
			toppingOption = Integer.parseInt(reader.readLine());
		}

		//get discountlist
		ArrayList<Discount> allDiscounts = DBNinja.getDiscountList();
		System.out.println("Do you want to add discounts to this Pizza? Enter y/n?");
		String disOption = reader.readLine();

		if(disOption.equals("y")){
			for(Discount d: allDiscounts){
				System.out.println(d.toString());
			}
			System.out.println("Which Pizza Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
			int pizzaDisOption = Integer.parseInt(reader.readLine());
			while(pizzaDisOption != -1){
				Discount selectedDiscount = null;
				for(Discount d: allDiscounts){
					if(d.getDiscountID() == pizzaDisOption){
						selectedDiscount = d;
						break;
					}
				}

				ret.addDiscounts(selectedDiscount);
				for(Discount d: allDiscounts){
					System.out.println(d.toString());
				}
				System.out.println("Which Pizza Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
				pizzaDisOption = Integer.parseInt(reader.readLine());
			}
		}

		DBNinja.addPizza(ret);

        return ret;
	}
	
	
	public static void PrintReports() throws SQLException, NumberFormatException, IOException
	{
		/*
		 * This method asks the use which report they want to see and calls the DBNinja method to print the appropriate report.
		 * 
		 */

		System.out.println("Which report do you wish to print? Enter\n(a) ToppingPopularity\n(b) ProfitByPizza\n(c) ProfitByOrderType:");
		String report = reader.readLine();
		if(report.equals("a")){
			DBNinja.printToppingPopReport();
		}else if(report.equals("b")){
			DBNinja.printProfitByPizzaReport();
		}else if(report.equals("c")){
			DBNinja.printProfitByOrderType();
		}else {
			System.out.println("I don't understand that input... returning to menu...");
		}
	}

	//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
	// DO NOT EDIT ANYTHING BELOW HERE, THIS IS NEEDED TESTING.
	// IF YOU EDIT SOMETHING BELOW, IT BREAKS THE AUTOGRADER WHICH MEANS YOUR GRADE WILL BE A 0 (zero)!!

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	/*
	 * autograder controls....do not modiify!
	 */

	public final static String autograder_seed = "6f1b7ea9aac470402d48f7916ea6a010";

	
	private static void autograder_compilation_check() {

		try {
			Order o = null;
			Pizza p = null;
			Topping t = null;
			Discount d = null;
			Customer c = null;
			ArrayList<Order> alo = null;
			ArrayList<Discount> ald = null;
			ArrayList<Customer> alc = null;
			ArrayList<Topping> alt = null;
			double v = 0.0;
			String s = "";

			DBNinja.addOrder(o);
			DBNinja.addPizza(p);
			DBNinja.useTopping(p, t, false);
			DBNinja.usePizzaDiscount(p, d);
			DBNinja.useOrderDiscount(o, d);
			DBNinja.addCustomer(c);
			DBNinja.completeOrder(o);
			alo = DBNinja.getOrders(false);
			o = DBNinja.getLastOrder();
			alo = DBNinja.getOrdersByDate("01/01/1999");
			ald = DBNinja.getDiscountList();
			d = DBNinja.findDiscountByName("Discount");
			alc = DBNinja.getCustomerList();
			c = DBNinja.findCustomerByPhone("0000000000");
			alt = DBNinja.getToppingList();
			t = DBNinja.findToppingByName("Topping");
			DBNinja.addToInventory(t, 1000.0);
			v = DBNinja.getBaseCustPrice("size", "crust");
			v = DBNinja.getBaseBusPrice("size", "crust");
			DBNinja.printInventory();
			DBNinja.printToppingPopReport();
			DBNinja.printProfitByPizzaReport();
			DBNinja.printProfitByOrderType();
			s = DBNinja.getCustomerName(0);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}


}



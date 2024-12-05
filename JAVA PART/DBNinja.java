package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "Small";
	public final static String size_m = "Medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";



	
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	
	public static void addOrder(Order o) throws SQLException, IOException 
	{
		connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 * 
		 */

		try {
			//add to table order
			//if order does not exist, insert order to get order ID
			//only with OrderID, we can start inserting pizzaList
			if(o.getOrderType() == null || o.getOrderType().trim().isEmpty()) {
				String ordersql = "Insert into cusorder(CusID, States, OrderCost, OrderPrice, OrderType) Values (?,?,?,?,?)";
				PreparedStatement orderStatement = conn.prepareStatement(ordersql,Statement.RETURN_GENERATED_KEYS);
				orderStatement.setInt(1, o.getCustID());
				orderStatement.setInt(2, o.getIsComplete());
				orderStatement.setDouble(3, o.getBusPrice());
				orderStatement.setDouble(4, o.getCustPrice());
				orderStatement.setString(5, o.getOrderType());
				orderStatement.executeUpdate();

				//get orderID for this new order
				ResultSet autoKeys = orderStatement.getGeneratedKeys();
				if(autoKeys.next()) {
					int orderID = autoKeys.getInt(1);
					o.setOrderID(orderID);
				}

				//if order already exists, we update order
			}else {
				String sql = "UPDATE cusorder SET CusID = ?, States = ?, OrderCost = ?, OrderPrice = ?, OrderType = ? WHERE OrderID = ?";
				PreparedStatement  orderStatement = conn.prepareStatement(sql);
				orderStatement.setInt(1, o.getCustID());
				orderStatement.setInt(2, o.getIsComplete());
				orderStatement.setDouble(3, o.getBusPrice());
				orderStatement.setDouble(4, o.getCustPrice());
				orderStatement.setString(5,o.getOrderType());
				orderStatement.setInt(6, o.getOrderID());
				orderStatement.executeUpdate();

				if (o instanceof DineinOrder) {
					//add to table dinein
					DineinOrder dineinOrder = (DineinOrder) o;
					String dineinsql = "Insert into dinein(OrderID, TableNum) Values (?,?)";
					PreparedStatement dineinStatement = conn.prepareStatement(dineinsql);
					dineinStatement.setInt(1, dineinOrder.getOrderID());
					dineinStatement.setInt(2, dineinOrder.getTableNum());
					dineinStatement.executeUpdate();
				} else if (o.getOrderType().equals("pickup")) {
					//add to table pickup
					String pickupsql = "Insert into pickup(OrderID, CusID) Values (?,?)";
					PreparedStatement pickupStatement = conn.prepareStatement(pickupsql);
					pickupStatement.setInt(1, o.getOrderID());
					pickupStatement.setInt(2, o.getCustID());
					pickupStatement.executeUpdate();
				} else if (o.getOrderType().equals("delivery")) {
					//add to table delivery
					String deliverysql = "Insert into delivery(OrderID, CusID) Values (?,?)";
					PreparedStatement deliveryStatement = conn.prepareStatement(deliverysql);
					deliveryStatement.setInt(1, o.getOrderID());
					deliveryStatement.setInt(2, o.getCustID());
					deliveryStatement.executeUpdate();
				} else {
					throw new SQLException("OrderType not Found");
				}

				//add to table orderdiscount
				for (Discount discount : o.getDiscountList()) {
					useOrderDiscount(o, discount);
				}
			}
		}catch(SQLException | IOException e){
			e.printStackTrace();
			throw e;
		}finally {
			conn.close();
		}

	}
	
	public static void addPizza(Pizza p) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind adding pizza discounts and toppings associated with the pizza,
		 * there are other methods below that may help with that process.
		 */

		//insert pizza into pizza table, get pizzaID
		if (p.getSize() == null || p.getSize().trim().isEmpty()) {
			String pizzaSql = "Insert into pizza(OrderId, Size, CrustType, States, PizzaCost, PizzaPrice)\n" +
					"Values(?,?,?,?,?,?)";
			PreparedStatement pizzaStatement = conn.prepareStatement(pizzaSql, Statement.RETURN_GENERATED_KEYS);
			pizzaStatement.setInt(1, p.getOrderID());
			pizzaStatement.setString(2, p.getSize());
			pizzaStatement.setString(3, p.getCrustType());
			pizzaStatement.setString(4, p.getPizzaState());
			pizzaStatement.setDouble(5, p.getBusPrice());
			pizzaStatement.setDouble(6, p.getCustPrice());
			pizzaStatement.executeUpdate();

			//pizzaID and pizzaDate are auto generated from DB, retrieve them
			ResultSet autoKeys = pizzaStatement.getGeneratedKeys();
			if (autoKeys.next()) {
				int pizzaID = autoKeys.getInt(1);
				p.setPizzaID(pizzaID);
				return;
			}
			//if pizza already exists, update pizza info
		} else {
			String sql = "UPDATE pizza SET Size = ?, CrustType = ?, PizzaCost = ?, PizzaPrice = ? WHERE PizzaID = ?";
			PreparedStatement pizzaStatement = conn.prepareStatement(sql);
			pizzaStatement.setString(1, p.getSize());
			pizzaStatement.setString(2, p.getCrustType());
			pizzaStatement.setDouble(3, p.getBusPrice());
			pizzaStatement.setDouble(4, p.getCustPrice());
			pizzaStatement.setInt(5, p.getPizzaID());
			pizzaStatement.executeUpdate();

			//get pizza date info
			String dateSql = "SELECT PizzaDate FROM pizza WHERE PizzaID = ?";
			PreparedStatement pizzaDateStmt = conn.prepareStatement(dateSql);
			pizzaDateStmt.setInt(1, p.getPizzaID());
			ResultSet pizzaDateSet = pizzaDateStmt.executeQuery();

			if (pizzaDateSet.next()) {
				Timestamp pizzaTimeStamp = pizzaDateSet.getTimestamp("PizzaDate");
				Date pizzaDate = new Date(pizzaTimeStamp.getTime());
				String pizzaDateString = pizzaDate.toString();
				p.setPizzaDate(pizzaDateString);
			}


			//add to table pizzatopping DB, a pizza could have many toppings, so we loop topping list
			//to insert them to pizzatopping table
			boolean[] extraTopping = p.getIsDoubleArray();
			for (int i = 0; i < p.getToppings().size(); i++) {
				Topping topping = p.getToppings().get(i);
				boolean isDoubled = extraTopping[i];
				useTopping(p, topping, isDoubled);
			}

			//add to table pizzadiscount, same here, a pizza could have many discounts
			for (Discount discount : p.getDiscounts()) {
				usePizzaDiscount(p, discount);
			}
		}

            conn.close();
	}

	
	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this method will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		connect_to_db();
		/*
		 * This method should do 2 two things.
		 * - update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * - connect the topping to the pizza
		 *   What that means will be specific to your implementation.
		 * 
		 * Ideally, you should't let toppings go negative....but this should be dealt with BEFORE calling this method.
		 *
		 */
		try {
			//first insert new toppings to bridging table pizzatopping
			String pizzaTopQuery = "Insert into pizzatopping(PizzaID, ToppingID, ExtraTopping) Values(?,?,?)";
			PreparedStatement pizzatopStatement = conn.prepareStatement(pizzaTopQuery);
			pizzatopStatement.setInt(1, p.getPizzaID());
			pizzatopStatement.setInt(2, t.getTopID());
			pizzatopStatement.setBoolean(3, isDoubled);
			pizzatopStatement.executeUpdate();

			//then update Topping table's inventory
			String topQuery = "UPDATE topping SET CurrentInv = CurrentInv - ? WHERE ToppingID = ?";
			PreparedStatement topStatement = conn.prepareStatement(topQuery);

			double topUsage = 0.0;
			switch (p.getSize()) {
				case "small":
					topUsage = t.getPerAMT();
					break;
				case "medium":
					topUsage = t.getMedAMT();
					break;
				case "large":
					topUsage = t.getLgAMT();
					break;
				case "XLarge":
					topUsage = t.getXLAMT();
					break;
			}

			// if it is doubled, *2
			if (isDoubled) {
				topUsage *= 2;
			}

			topStatement.setDouble(1, topUsage);
			topStatement.setInt(2, t.getTopID());
			topStatement.executeUpdate();

		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}finally {
				conn.close();
		}
	}
	
	
	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * This method connects a discount with a Pizza in the database.
		 * 
		 * What that means will be specific to your implementatinon.
		 */
		try {
			//insert to bridging table pizzadiscount
			String pizzadisQuery = "Insert into pizzadiscount(PizzaID, DiscountID)\n" +
					"Values(?,?)";
			PreparedStatement pizzadisStatement = conn.prepareStatement(pizzadisQuery);
			pizzadisStatement.setInt(1, p.getPizzaID());
			pizzadisStatement.setInt(2, d.getDiscountID());
			pizzadisStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}finally {
			conn.close();
		}
	}
	
	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * This method connects a discount with an order in the database
		 * 
		 * You might use this, you might not depending on where / how to want to update
		 * this information in the dabast
		 */
		try {
			//insert into bridging table orderdiscount
			String orderDisQuery = "Insert into orderdiscount(OrderID, DiscountID)\n" +
					"Values(?,?)";
			PreparedStatement orderdisStatement = conn.prepareStatement(orderDisQuery);
			orderdisStatement.setInt(1, o.getOrderID());
			orderdisStatement.setInt(2, d.getDiscountID());
			orderdisStatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}finally {
			conn.close();
		}

	}
	
	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This method adds a new customer to the database.
		 */
		try {
			//separate Address to parts to match our DB format
			String[] fullAddress = c.getAddress().split(", ");
			String streetAddress = fullAddress.length > 0 ? fullAddress[0] : "";
			String city = fullAddress.length > 1 ? fullAddress[1] : "";
			String state = fullAddress.length > 2 ? fullAddress[2] : "";
			String zipCode = fullAddress.length > 3 ? fullAddress[3] : "";

			//if customer can be found via phone, then it is an existing customer
			//so we update customer to complete missing information(address)
			String phoneNum = c.getPhone();
			if(findCustomerByPhone(phoneNum) != null){
				String updateCus = "update customer SET CusStreetAddress = ?, CusCity = ?, CusState =?, CusZipCode=? WHERE CusID =?";
				PreparedStatement cusStatement = conn.prepareStatement(updateCus);
				cusStatement.setString(1, streetAddress);
				cusStatement.setString(2, city);
				cusStatement.setString(3, state);
				cusStatement.setString(4, zipCode);
				cusStatement.setInt(5, c.getCustID());
				cusStatement.executeUpdate();

			}else{
				//otherwise, we insert new customer info to customer table
				String cusQuery = "Insert into customer(CusFname, CusLname, CusStreetAddress, CusCity, CusState, CusZipCode, CusPhone)\n" +
						"VALUES(?,?,?,?,?,?,?)";
				PreparedStatement cusStatement = conn.prepareStatement(cusQuery, Statement.RETURN_GENERATED_KEYS);
				cusStatement.setString(1, c.getFName());
				cusStatement.setString(2, c.getLName());
				cusStatement.setString(3, streetAddress);
				cusStatement.setString(4, city);
				cusStatement.setString(5, state);
				cusStatement.setString(6, zipCode);
				cusStatement.setString(7, c.getPhone());
				cusStatement.executeUpdate();

				//obtain auto_increment custID
				ResultSet autoKeys = cusStatement.getGeneratedKeys();
				if (autoKeys.next()) {
					c.setCustID(autoKeys.getInt(1));

				} else {
					throw new SQLException("Creating customer failed, no ID obtained.");
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}finally{
			conn.close();
		}
	}

	public static void completeOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Find the specifed order in the database and mark that order as complete in the database.
		 */
		try{
			//find the order based on OrderID, and change states to complete
			String statesOrder = "UPDATE cusorder SET States = 1 WHERE OrderID = ? ";
			PreparedStatement statesStatement = conn.prepareStatement(statesOrder);
			statesStatement.setInt(1,o.getOrderID());
			statesStatement.executeUpdate();

		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}finally {
			conn.close();
		}

	}


	public static ArrayList<Order> getOrders(boolean openOnly) throws SQLException, IOException {

			connect_to_db();
			/*
			 * Return an arraylist of all of the orders.
			 * 	openOnly == true => only return a list of open (ie orders that have not been marked as completed)
			 *           == false => return a list of all the orders in the database
			 * Remember that in Java, we account for supertypes and subtypes
			 * which means that when we create an arrayList of orders, that really
			 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
			 *
			 * Don't forget to order the data coming from the database appropriately.
			 *
			 */
			try {
				ArrayList<Order> orders = new ArrayList<>();
				String sql;

				//if true, all open orders
				//else, all orders
				if (openOnly) {
					sql = "SELECT * FROM cusorder WHERE STATES = 0 Order by OrderTimeStamp DESC";
				} else {
					sql = "SELECT * FROM cusorder ORDER BY OrderTimeStamp DESC";
				}

				Statement orderStatement = conn.createStatement();
				ResultSet orderResults = orderStatement.executeQuery(sql);

				//loop each order to retrieve info
				while (orderResults.next()) {
					int OrderID = orderResults.getInt("OrderID");
					int CusID = orderResults.getInt("CusID");
					String OrderTimeStamp = orderResults.getString("OrderTimeStamp");
					int States = orderResults.getInt("States");
					double OrderCost = orderResults.getDouble("OrderCost");
					double OrderPrice = orderResults.getDouble("OrderPrice");
					String OrderType = orderResults.getString("OrderType");

					//add order info retrieved from DB to orders arrayList
					Order order = new Order(OrderID, CusID, OrderType, OrderTimeStamp, OrderCost, OrderPrice, States);
					orders.add(order);

					//in order to display order -> pizza ->pizza discount, we need to retrieve all information

					//retrieve pizzaList from each order
					String pizzaList = "SELECT * FROM pizza WHERE OrderID = ?";
					PreparedStatement pizzastmt = conn.prepareStatement(pizzaList);
					pizzastmt.setInt(1, OrderID);
					ResultSet pizzaSet = pizzastmt.executeQuery();

					//each order can have many pizzas, so loop the pizza resultSet
					while (pizzaSet.next()) {
						int pizzaID = pizzaSet.getInt("PizzaID");
						String size = pizzaSet.getString("Size");
						String crustType = pizzaSet.getString("CrustType");
						String pizzaState = pizzaSet.getString("States");
						String pizzaDate = pizzaSet.getString("PizzaDate");
						double custPrice = pizzaSet.getDouble("PizzaPrice");
						double busPrice = pizzaSet.getDouble("PizzaCost");
						Pizza pizza = new Pizza(pizzaID, size, crustType, OrderID, pizzaState, pizzaDate, custPrice, busPrice);
						//add pizza to arrayList
						order.getPizzaList().add(pizza);

						//each pizza can have many discounts, so loop the discount resultSet
						String disList = "SELECT * FROM pizzadiscount p join discount d on p.DiscountID = d.DiscountID WHERE PizzaID = ?";
						PreparedStatement disStatement = conn.prepareStatement(disList);
						disStatement.setInt(1, pizzaID);
						ResultSet disSet = disStatement.executeQuery();

						while (disSet.next()) {
							int discountID = disSet.getInt("DiscountID");
							String discountName = disSet.getString("DiscountName");
							double amount = disSet.getDouble("DiscountValue");
							boolean isPercent = disSet.getBoolean("DiscountType");
							Discount discount = new Discount(discountID, discountName, amount, isPercent);
							//add discount to arrayList
							pizza.getDiscounts().add(discount);
						}
					}
				}

				return orders;

			} catch(SQLException e){
				e.printStackTrace();
				throw e;
			}finally{
			conn.close();
			}
	}
	
	public static Order getLastOrder(){
		/*
		 * Query the database for the LAST order added
		 * then return an Order object for that order.
		 * NOTE...there should ALWAYS be a "last order"!
		 */

		try {
			connect_to_db();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {

			//find the last order
			String sql = "SELECT * FROM cusorder ORDER BY OrderID DESC LIMIT 1";
			Statement OrderStatement = conn.createStatement();
			ResultSet lastResult = OrderStatement.executeQuery(sql);

			//assign info retrieved from DB to JAVA and return the order
			if (lastResult.next()) {
				int orderID = lastResult.getInt("OrderID");
				int cusID = lastResult.getInt("CusID");
				String orderTimeStamp = lastResult.getString("OrderTimeStamp");
				int states = lastResult.getInt("States");
				double orderCost = lastResult.getDouble("OrderCost");
				double orderPrice = lastResult.getDouble("OrderPrice");
				String orderType = lastResult.getString("OrderType");

				return new Order(orderID, cusID, orderType, orderTimeStamp, orderCost, orderPrice, states);
			}

			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

    }

	public static ArrayList<Order> getOrdersByDate(String date){
			/*
			 * Query the database for ALL the orders placed on a specific date
			 * and return a list of those orders.
			 *
			 */
		try {
			connect_to_db();
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}

        try {
			//create order arraylist to hold all the orders on that date
			ArrayList<Order> orderDate = new ArrayList<>();
			String sql = "Select * from cusorder where OrderTimeStamp = ? Order by OrderID";
			PreparedStatement orderDateStatement = conn.prepareStatement(sql);
			orderDateStatement.setString(1, date);
			ResultSet orderDateSet = orderDateStatement.executeQuery();

			while (orderDateSet.next()) {
				int OrderID = orderDateSet.getInt("OrderID");
				int CusID = orderDateSet.getInt("CusID");
				String OrderTimeStamp = orderDateSet.getString("OrderTimeStamp");
				int States = orderDateSet.getInt("States");
				double OrderCost = orderDateSet.getDouble("OrderCost");
				double OrderPrice = orderDateSet.getDouble("OderPrice");
				String OrderType = orderDateSet.getString("OrderType");

				orderDate.add(new Order(OrderID, CusID, OrderType, OrderTimeStamp, OrderCost, OrderPrice,States));
			}
			return orderDate;

		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

	}
		
	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database for all the available discounts and 
		 * return them in an arrayList of discounts.
		 * 
		*/
		try {
			ArrayList<Discount> discountList = new ArrayList<>();
			String sql = "Select * from discount";
			Statement disStatement = conn.createStatement();
			ResultSet disSet = disStatement.executeQuery(sql);

			while (disSet.next()) {
				int DiscountID = disSet.getInt("DiscountID");
				String DiscountName = disSet.getString("DiscountName");
				Boolean DiscountType = disSet.getBoolean("DiscountType");
				Double DiscountValue = disSet.getDouble("DiscountValue");

				discountList.add(new Discount(DiscountID, DiscountName, DiscountValue, DiscountType));
			}
			return discountList;

		}catch(SQLException e){
				e.printStackTrace();
				return null;
		}finally{
			conn.close();
		}
	}

	public static Discount findDiscountByName(String name){
		/*
		 * Query the database for a discount using it's name.
		 * If found, then return an OrderDiscount object for the discount.
		 * If it's not found....then return null
		 *  
		 */
		try {
			connect_to_db();
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}

        try {
			String sql = "Select * from discount where DiscountName = ?";
			PreparedStatement findDis = conn.prepareStatement(sql);
			findDis.setString(1, name);
			ResultSet disSet = findDis.executeQuery();

			if(disSet.next()) {
				int DiscountID = disSet.getInt("DiscountID");
				String DiscountName = disSet.getString("DiscountName");
				Boolean DiscountType = disSet.getBoolean("DiscountType");
				Double DiscountValue = disSet.getDouble("DiscountValue");

                return new Discount(DiscountID, DiscountName, DiscountValue, DiscountType);

			}else{
				System.out.println("No Discount found");
				return null;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the data for all the customers and return an arrayList of all the customers. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		*/
		try{
			//as required, order by last name, first name and phone number
			ArrayList<Customer> cusList = new ArrayList<>();
			String sql = "Select * from customer Order by CusFname, CusLname, CusPhone";
			Statement cusStatement = conn.createStatement();
			ResultSet cusSet = cusStatement.executeQuery(sql);

			while(cusSet.next()) {
				int CusID = cusSet.getInt("CusID");
				String CusFName = cusSet.getString("CusFName");
				String CusLName = cusSet.getString("CusLName");
				String CusPhone = cusSet.getString("CusPhone");

				cusList.add(new Customer(CusID, CusFName, CusLName, CusPhone));
			}
			return cusList;

		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			conn.close();
		}
	}

	public static Customer findCustomerByPhone(String phoneNumber){
		/*
		 * Query the database for a customer using a phone number.
		 * If found, then return a Customer object for the customer.
		 * If it's not found....then return null
		 *  
		 */

		try {
			connect_to_db();
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}

        try {
			String sql = "SELECT * from customer where CusPhone = ?";
			PreparedStatement phoneStatement = conn.prepareStatement(sql);
			phoneStatement.setString(1, phoneNumber);
			ResultSet phoneSet = phoneStatement.executeQuery();

			if (phoneSet.next()) {
				int CusID = phoneSet.getInt("CusID");
				String CusFName = phoneSet.getString("CusFName");
				String CusLName = phoneSet.getString("CusLName");
				String CusPhone = phoneSet.getString("CusPhone");

                return new Customer(CusID, CusFName, CusLName, CusPhone);

			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}

        return null;
    }


	public static ArrayList<Topping> getToppingList() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the database for the aviable toppings and 
		 * return an arrayList of all the available toppings. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */
		try {
			ArrayList<Topping> topList = new ArrayList<>();
			String sql = "select * from topping Order by ToppingName ";
			Statement topStatement = conn.createStatement();
			ResultSet topSet = topStatement.executeQuery(sql);

			while (topSet.next()) {
				int ToppingID = topSet.getInt("ToppingID");
				String ToppingName = topSet.getString("ToppingName");
				double CustPrice = topSet.getDouble("PricePerUnit");
				double BusPrice = topSet.getDouble("CostPerUnit");
				int CurrentInv = topSet.getInt("CurrentInv");
				int MinimumInv = topSet.getInt("MinimumInv");
				double UnitsUsedS = topSet.getDouble("UnitsUsedS");
				double UnitsUsedM = topSet.getDouble("UnitsUsedM");
				double UnitsUsedL = topSet.getDouble("UnitsUsedL");
				double UnitsUsedXL = topSet.getDouble("UnitsUsedXL");

				topList.add(new Topping(ToppingID, ToppingName, UnitsUsedS, UnitsUsedM, UnitsUsedL, UnitsUsedXL,
						CustPrice, BusPrice, MinimumInv, CurrentInv));
			}
			return topList;

		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			conn.close();
		}
	}

	public static Topping findToppingByName(String name){
		/*
		 * Query the database for the topping using it's name.
		 * If found, then return a Topping object for the topping.
		 * If it's not found....then return null
		 *  
		 */

		try {
			connect_to_db();
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}

        try {
			String sql = "select * from topping where ToppingName = ?";
			PreparedStatement findTop = conn.prepareStatement(sql);
			findTop.setString(1, name);
			ResultSet topSet = findTop.executeQuery();

			if (topSet.next()) {
				int ToppingID = topSet.getInt("ToppingID");
				String ToppingName = topSet.getString("ToppingName");
				double CustPrice = topSet.getDouble("PricePerUnit");
				double BusPrice = topSet.getDouble("CostPerUnit");
				int CurrentInv = topSet.getInt("CurrentInv");
				int MinimumInv = topSet.getInt("MinimumInv");
				double UnitsUsedS = topSet.getDouble("UnitsUsedS");
				double UnitsUsedM = topSet.getDouble("UnitsUsedM");
				double UnitsUsedL = topSet.getDouble("UnitsUsedL");
				double UnitsUsedXL = topSet.getDouble("UnitsUsedXL");

                return new Topping(ToppingID, ToppingName, UnitsUsedS, UnitsUsedM, UnitsUsedL, UnitsUsedXL,
						CustPrice, BusPrice, MinimumInv, CurrentInv);
			} else {
				System.out.println("No Topping Found");
				return null;
			}

		}catch(SQLException e){
			e.printStackTrace();
			return null;

		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}


	public static void addToInventory(Topping t, double quantity) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 * 
		 * */
		try {
			String sql = "update topping set CurrentInv = CurrentInv + ? where ToppingID = ?";
			PreparedStatement invStatement = conn.prepareStatement(sql);
			invStatement.setDouble(1, quantity);
			invStatement.setInt(2, t.getTopID());
			invStatement.executeUpdate();

		}catch(SQLException e){
			e.printStackTrace();
			throw e;

        }finally{
			conn.close();
		}
	}
	
	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database fro the base customer price for that size and crust pizza.
		 * 
		*/
		try {
			//Base price is fixed in our DB
			//we need to retrieve it so that we could calculate our order price
			double cusPrice = 0.00;
			String sql = "select BasePrice from base where Size = ? AND CrustType = ?";
			PreparedStatement baseStatement = conn.prepareStatement(sql);
			baseStatement.setString(1, size);
			baseStatement.setString(2, crust);
			ResultSet baseSet = baseStatement.executeQuery();

			if (baseSet.next()) {
				cusPrice += baseSet.getDouble("BasePrice");
				return cusPrice;
			} else {
				System.out.println("Base not found");
				return 0.0;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally{
			conn.close();
		}
    }

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the database fro the base business price for that size and crust pizza.
		 *
		 */
		try {
			double busPrice = 0.00;
			String sql = "select BaseCost from base where Size = ? AND CrustType = ?";
			PreparedStatement busStatement = conn.prepareStatement(sql);
			busStatement.setString(1, size);
			busStatement.setString(2, crust);
			ResultSet busSet = busStatement.executeQuery();

			if (busSet.next()) {
				busPrice += busSet.getDouble("BaseCost");
				return busPrice;
			} else {
				return 0.0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			conn.close();
		}

	}

	public static void printInventory() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Queries the database and prints the current topping list with quantities.
		 *  
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		try {
			String sql = "select * from topping";
			Statement invStatement = conn.createStatement();
			ResultSet invSet = invStatement.executeQuery(sql);

			System.out.printf("%-15s %-15s %-15s\n", "ID", "Name", "CurINVT");
			while (invSet.next()) {
				String ID = invSet.getString("ToppingID");
				String Name = invSet.getString("ToppingName");
				Double CurINVT = invSet.getDouble("CurrentInv");

				System.out.printf("%-15s %-15s %-15s\n", ID, Name, CurINVT);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			conn.close();
		}

	}
	
	public static void printToppingPopReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */

		try {
			String sql = "select * from ToppingPopularity";
			Statement popStatement = conn.createStatement();
			ResultSet popSet = popStatement.executeQuery(sql);

			System.out.printf("%-25s %-15s\n", "Topping", "ToppingCount");
			while (popSet.next()) {
				String Topping = popSet.getString("Topping");
				int count = popSet.getInt("ToppingCount");

				System.out.printf("%-25s %-15s\n", Topping, count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			conn.close();
		}

	}
	
	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByPizza view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		try {
			String sql = "select * from ProfitByPizza";
			Statement profitStatement = conn.createStatement();
			ResultSet profitSet = profitStatement.executeQuery(sql);

			System.out.printf("%-15s %-15s %-15s %-15s\n", "Pizza Size", "Pizza Crust", "Profit", "LastOrderDate");
			while (profitSet.next()) {
				String size = profitSet.getString("Size");
				String crust = profitSet.getString("Crust");
				Double profit = profitSet.getDouble("Profit");
				String date = profitSet.getString("Order Month");

				System.out.printf("%-15s %-15s %-15s %-15s\n", size, crust, profit, date);
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static void printProfitByOrderType() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		try {
			String sql = "select * from ProfitByOrderType";
			Statement proByTypeStatement = conn.createStatement();
			ResultSet profitSet = proByTypeStatement.executeQuery(sql);

			System.out.printf("%-15s %-15s %-15s %-15s %-15s\n", "OrderType", "Order Month", "TotalOrderPrice", "TotalOrderCost", "Profit");
			while (profitSet.next()) {
				String orderType = profitSet.getString("customerType");
				String orderMonth = profitSet.getString("Order Month");
				double price = profitSet.getDouble("TotalOrderPrice");
				double cost = profitSet.getDouble("TotalOrderCost");
				double profit = profitSet.getDouble("Profit");

				System.out.printf("%-15s %-15s %-15s %-15s %-15s\n", orderType, orderMonth, price, cost, profit);
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			conn.close();
		}

	}
	
	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
	/*
		 * This is a helper method to fetch and format the name of a customer
		 * based on a customer ID. This is an example of how to interact with 
		 * your database from Java.  It's used in the model solution for this project...so the code works!
		 * 
		 * OF COURSE....this code would only work in your application if the table & field names match!
		 *
		 */

		 connect_to_db();

		/* 
		 * an example query using a constructed string...
		 * remember, this style of query construction could be subject to sql injection attacks!
		 * 
		 */
		//String cname1 = "";
		//String query = "Select CusFname, CusLname From customer WHERE CusID=" + CustID + ";";
		//Statement stmt = conn.createStatement();
		//ResultSet rset = stmt.executeQuery(query);
		
		//while(rset.next())
		//{
		//	cname1 = rset.getString(1) + " " + rset.getString(2);
		//}

		/* 
		* an example of the same query using a prepared statement...
		* 
		*/
		String cname2 = "";
		PreparedStatement os;
		ResultSet rset2;
		String query2;
		query2 = "Select CusFname, CusLname From customer WHERE CusID=?;";
		os = conn.prepareStatement(query2);
		os.setInt(1, CustID);
		rset2 = os.executeQuery();
		while(rset2.next())
		{
			cname2 = rset2.getString("CusFname") + " " + rset2.getString("CusLname"); // note the use of field names in the getSting methods
		}

		conn.close();
		return cname2; // OR cname2
	}

	/*
	 * The next 3 private methods help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		if(getYear(dateOfOrder) > year)
			return true;
		else if(getYear(dateOfOrder) < year)
			return false;
		else
		{
			if(getMonth(dateOfOrder) > month)
				return true;
			else if(getMonth(dateOfOrder) < month)
				return false;
			else
			{
				if(getDay(dateOfOrder) >= day)
					return true;
				else
					return false;
			}
		}
	}


}
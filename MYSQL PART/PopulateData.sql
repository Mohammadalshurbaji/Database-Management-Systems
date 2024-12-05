/* Name: Danqi Xu
	     Mohammad O Alshurbaji
*/

USE Pizzeria;


Insert into topping(ToppingName, PricePerUnit, CostPerUnit,CurrentInv, MinimumInv,
UnitsUsedS, UnitsUsedM, UnitsUsedL, UnitsUsedXL)
Values
('Pepperoni', 1.25, 0.2, 100, 50, 2, 2.75, 3.5, 4.5),
('Sausage', 1.25, 0.15, 100, 50, 2.5, 3, 3.5, 4.25),
('Ham', 1.5, 0.15, 78, 25, 2, 2.5, 3.25, 4),
('Chicken', 1.75, 0.25, 56, 25, 1.5, 2, 2.25, 3),
('Green Pepper', 0.5, 0.02, 79, 25, 1, 1.5, 2, 2.5),
('Onion', 0.5, 0.02, 85, 25, 1, 1.5, 2, 2.75),
('Roma Tomato', 0.75, 0.03, 86, 10, 2, 3, 3.5, 4.5),
('Mushrooms', 0.75, 0.1, 52, 50, 1.5, 2, 2.5, 3),
('Black Olives', 0.6, 0.1, 39, 25, 0.75, 1, 1.5, 2),
('Pineapple', 1, 0.25, 15, 0, 1, 1.25, 1.75, 2),
('Jalapenos', 0.5, 0.05, 64, 0, 0.5, 0.75, 1.25, 1.75),
('Banana Peppers', 0.5, 0.05, 36, 0, 0.6, 1, 1.3, 1.75),
('Regular Cheese', 0.5, 0.12, 250, 50, 2, 3.5, 5, 7),
('Four Cheese Blend', 1, 0.15, 150, 25, 2, 3.5, 5, 7),
('Feta Cheese', 1.5, 0.18, 75, 0, 1.75, 3, 4, 5.5),
('Goat Cheese', 1.5, 0.2, 54, 0, 1.6, 2.75, 4, 5.5),
('Bacon', 1.5, 0.25, 89, 0, 1, 1.5, 2, 3);


Insert into discount(DiscountName, DiscountType, DiscountValue)
Values
('Employee', true, 15),
('Lunch Special Medium', false, 1.00),
('Lunch Special Large', false, 1.00),
('Specialty Pizza', false, 1.50),
('Happy Hour', true, 10),
('Gameday Special', true, 20);


Insert into base (Size, CrustType, BasePrice, BaseCost)
Values
('Small', 'Thin', 3, 0.5),
('Small', 'Original', 3, 0.75),
('Small', 'Pan', 3.5, 1),
('Small', 'Gluten-Free', 4, 2),
('Medium', 'Thin', 5, 1),
('Medium', 'Original', 5, 1.5),
('Medium', 'Pan', 6, 2.25),
('Medium', 'Gluten-Free', 6.25, 3),
('Large', 'Thin', 8, 1.25),
('Large', 'Original', 8, 2),
('Large', 'Pan', 9, 3),
('Large', 'Gluten-Free', 9.5, 4),
('XLarge', 'Thin', 10, 2),
('XLarge', 'Original', 10, 3),
('XLarge', 'Pan', 11.5, 4.5),
('XLarge', 'Gluten-Free', 12.5, 6);

Insert into customer(CusFname, CusLname, CusStreetAddress, CusCity, CusState, CusZipCode, CusPhone)
VALUES
('INSTORE','customer','NA','NA','NA','NA','0000000000'),
('Andrew', 'Wilkes-Krier', '115 Party Blvd','Anderson', 'SC', '29621','864-254-5861'),
('Matt', 'Engers','NA','NA','NA','NA','864-474-9953'),
('Frank', 'Turner','6745 Wessex St','Anderson', 'SC','29621','864-232-8944'),
('Milo', 'Auckerman', '8879 Suburban Home', 'Anderson', 'SC', '29621','864-878-5679');

Insert into cusorder(CusID, OrderTimeStamp, States, OrderCost, OrderPrice, OrderType)
Values
(1,'2023-03-05 12:03:00', 1, 3.68, 20.75, 'dinein'),
(1, '2023-04-03 12:05:00', 1, 4.63, 19.78, 'dinein'),
(2, '2023-03-03 21:30:00', 1, 19.8, 89.28, 'pickup'),
(2, '2023-04-20 19:11:00', 1, 23.62, 86.19, 'delivery'),
(3, '2023-03-02 17:30:00', 1, 7.88, 27.45, 'pickup'),
(4, '2023-03-02 18:17:00', 1, 4.24, 25.81, 'delivery'),
(5, '2023-04-13 20:32:00', 1, 6.00, 37.25, 'delivery'); 

Insert into pizza(OrderId, Size, CrustType, States, PizzaCost, PizzaPrice)
Values
(1, 'Large', 'Thin', 'completed', 3.68, 20.75),
(2, 'Medium', 'Pan', 'completed', 3.23, 12.85),
(2, 'Small', 'Original', 'completed', 1.40, 6.93),
(3, 'Large', 'Original', 'completed', 3.3, 14.88),
(3, 'Large', 'Original', 'completed', 3.3, 14.88),
(3, 'Large', 'Original', 'completed', 3.3, 14.88),
(3, 'Large', 'Original', 'completed', 3.3, 14.88),
(3, 'Large', 'Original', 'completed', 3.3, 14.88),
(3, 'Large', 'Original', 'completed', 3.3, 14.88),
(4, 'XLarge', 'Original', 'completed', 9.19, 27.94),
(4, 'XLarge', 'Original', 'completed', 6.25, 31.50),
(4, 'XLarge', 'Original', 'completed', 8.18, 26.75),
(5, 'XLarge', 'Gluten-Free', 'completed', 7.88, 27.45),
(6, 'Large', 'Thin', 'completed', 4.24, 25.81),
(7, 'Large', 'Thin', 'completed', 2.75, 18.00),
(7, 'Large', 'Thin', 'completed', 3.25, 19.25);


Insert into pizzadiscount(PizzaID, DiscountID)
Values
(1, 3), (2, 2), (2, 4), (11, 4),(13, 4);

Insert into orderdiscount(OrderID, DiscountID)
Values
(4, 6), (7, 1);

Insert into pizzatopping(PizzaId, ToppingID, ExtraTopping)
Values
(1, 13, true), (1, 1, false), (1, 2, false),
(2, 15, false), (2, 9, false),(2, 7, false),(2, 8, false),(2, 12, false),
(3, 13, false),(3, 4, false),(3, 12, false),
(4, 13, false),(4, 1, false),
(5, 13, false),(5, 1, false),
(6, 13, false),(6, 1, false),
(7, 13, false),(7, 1, false),
(8, 13, false),(8, 1, false),
(9, 13, false),(9, 1, false),
(10, 1, false), (10, 2, false),(10, 14, false),
(11, 3, true),(11, 10, true),(11, 14, false),
(12, 4, false),(12, 17, false),(12, 14, false),
(13, 5, false),(13, 6, false),(13, 7, false),(13, 8, false),(13, 9, false),(13, 16, false),
(14, 4, false),(14, 5, false), (14, 6, false), (14, 8, false), (14, 14, true),
(15, 14, true), 
(16, 13, false), (16, 1, true);

Insert into dinein(OrderID, TableNum)
Values
(1, 21),
(2, 4);

Insert into pickup(OrderID, CusID)
Values
(3, 2),
(5, 3);

Insert into delivery(OrderID, CusID)
Values
(4, 2),
(6, 4),
(7, 5);







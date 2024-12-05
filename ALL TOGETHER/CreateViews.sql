/* Name: Danqi Xu
	     Mohammad O Alshurbaji
*/

USE Pizzeria;

DROP VIEW IF EXISTS ToppingPopularity;
CREATE VIEW ToppingPopularity AS
	SELECT ToppingName AS Topping, SUM( 1 + ExtraTopping) AS ToppingCount
    from topping left join pizzatopping 
    on topping.ToppingID = pizzatopping.ToppingID
	group by ToppingName
    Order by ToppingCount DESC;
SELECT * FROM ToppingPopularity;

DROP VIEW IF EXISTS ProfitByPizza;
CREATE VIEW ProfitByPizza AS
	SELECT Size, CrustType AS Crust, ROUND(SUM(PizzaPrice - PizzaCost),2) AS Profit,
    DATE_FORMAT(OrderTimeStamp, '%c/%Y') AS 'Order Month'
    from pizza join cusorder on 
    pizza.OrderID = cusorder.OrderID
    group by Size, CrustType
    Order by Profit DESC;
SELECT * FROM ProfitByPizza;

DROP VIEW IF EXISTS ProfitByOrderType;
CREATE VIEW ProfitByOrderType AS
	SELECT OrderType AS customerType, 
    DATE_FORMAT(OrderTimeStamp, '%c/%Y') AS 'Order Month', 
    SUM(OrderPrice) AS TotalOrderPrice,
    SUM(OrderCost) AS TotalOrderCost,
    (SUM(OrderPrice) - SUM(OrderCost)) AS Profit
    from cusorder
	group by OrderType, DATE_FORMAT(OrderTimeStamp, '%c/%Y')
    Union all
    Select '' AS customerType, 
    'Grand Total' as 'Order Month',
    SUM(OrderPrice),
    SUM(OrderCost),
    SUM(OrderPrice) - SUM(OrderCost) 
    from cusorder
    Order by 'Order Month';
SELECT * FROM ProfitByOrderType;
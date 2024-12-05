/* Name: Danqi Xu
	     Mohammad O Alshurbaji
*/

Create SCHEMA Pizzeria;
USE Pizzeria;

create table base(
    Size VARCHAR(30) NOT NULL,
    CrustType VARCHAR(30) NOT NULL,
    BasePrice DECIMAL(10,2) NOT NULL,
    BaseCost DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (Size, CrustType),
    CONSTRAINT SizeCrust UNIQUE(Size, CrustType) 
);

create table customer(
	CusID INT NOT NULL auto_increment PRIMARY KEY,
    CusFname VARCHAR(35) NOT NULL,
    CusLname VARCHAR(35) NOT NULL,
    CusStreetAddress VARCHAR(50),
    CusCity VARCHAR(50),
    CusState VARCHAR(30),
    CusZipCode VARCHAR(15),
    CusPhone CHAR(20) NOT NULL,
    CONSTRAINT CusName UNIQUE(CusFname, CusLname) 
);

create table cusorder(
	OrderID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    CusID INTEGER NOT NULL,
    OrderTimeStamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    States INTEGER NOT NULL,
    OrderCost DECIMAL(10,2) NOT NULL,
    OrderPrice DECIMAL(10,2) NOT NULL,
    OrderType VARCHAR(50) NOT NULL,
    FOREIGN KEY(CusID) REFERENCES customer(CusID) ON UPDATE CASCADE ON DELETE CASCADE
);

create table pizza(
	PizzaID INT auto_increment PRIMARY KEY,
    OrderID INTEGER NOT NULL,
    Size VARCHAR(30) NOT NULL,
    CrustType VARCHAR(30) NOT NULL,
    States VARCHAR(50) NOT NULL,
    PizzaDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PizzaCost DECIMAL(10,2) NOT NULL,
    PizzaPrice DECIMAL(10,2) NOT NULL,
	FOREIGN KEY(OrderID) REFERENCES cusorder(OrderID) ON UPDATE CASCADE ON DELETE CASCADE
);

create table topping(
	ToppingID INTEGER NOT NULL auto_increment PRIMARY KEY,
    ToppingName VARCHAR(25) UNIQUE NOT NULL,
    PricePerUnit DECIMAL(10,2) NOT NULL,
    CostPerUnit DECIMAL(10,2) NOT NULL,
	CurrentInv DECIMAL(10,2) NOT NULL,
    MinimumInv DECIMAL(10,2) NOT NULL,
    UnitsUsedS DECIMAL(10,2) NOT NULL,
    UnitsUsedM DECIMAL(10,2) NOT NULL,
    UnitsUsedL DECIMAL(10,2) NOT NULL,
    UnitsUsedXL DECIMAL(10,2) NOT NULL
);

create table pizzatopping(
	PizzaTopID INT NOT NULL auto_increment PRIMARY KEY,
    PizzaID INTEGER NOT NULL,
    ToppingID INTEGER NOT NULL,
    ExtraTopping BOOL NOT NULL DEFAULT FALSE,
    FOREIGN KEY(PizzaID) REFERENCES pizza(PizzaID) ON UPDATE CASCADE ON DELETE CASCADE, 
	FOREIGN KEY(ToppingID) REFERENCES topping(ToppingID) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT PizzaTop UNIQUE(PizzaTopID, PizzaID) 
);

create table discount(
	DiscountID INTEGER NOT NULL auto_increment PRIMARY KEY,
    DiscountName VARCHAR(35) UNIQUE NOT NULL,
    DiscountType BOOL NOT NULL DEFAULT TRUE,
    DiscountValue   DECIMAL(10,2) DEFAULT 0.00 NOT NULL
);

create table pizzadiscount(
	PizzaDisID INTEGER NOT NULL auto_increment PRIMARY KEY,
    PizzaID INTEGER NOT NULL,
    DiscountID INTEGER NOT NULL,
    FOREIGN KEY(PizzaID) REFERENCES pizza(PizzaID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY(DiscountID) REFERENCES discount(DiscountID) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT PizzaDis UNIQUE(PizzaID, DiscountID) 
);

create table orderdiscount(
	OrderDisID INTEGER NOT NULL auto_increment PRIMARY KEY,
    OrderID INTEGER NOT NULL,
    DiscountID INTEGER NOT NULL,
    FOREIGN KEY(OrderID) REFERENCES cusorder(OrderID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY(DiscountID) REFERENCES discount(DiscountID) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT OrderDis UNIQUE(OrderID, DiscountID)
);

create table dinein(
	OrderID INTEGER PRIMARY KEY NOT NULL,
    TableNum INTEGER NOT NULL,
	FOREIGN KEY(OrderID) REFERENCES cusorder(OrderID) ON UPDATE CASCADE ON DELETE CASCADE
);

create table pickup(
	OrderID INTEGER PRIMARY KEY NOT NULL,
    CusID INTEGER NOT NULL,
	FOREIGN KEY(OrderID) REFERENCES cusorder(OrderID) ON UPDATE CASCADE ON DELETE CASCADE
);

create table delivery(
	OrderID INTEGER PRIMARY KEY NOT NULL,
    CusID INTEGER NOT NULL,
	FOREIGN KEY(OrderID) REFERENCES cusorder(OrderID) ON UPDATE CASCADE ON DELETE CASCADE
);


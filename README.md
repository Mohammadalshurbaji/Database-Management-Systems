# **Pizza Restaurant Database Management System**

## **Overview**

This project is a **relational database management system (RDBMS)** designed for a pizza restaurant as part of our Database Management Systems course. The primary goal was to develop a fully functional system using **MySQL** and **Java**, capable of managing customer orders, inventory, and reporting.

## **Features**

### **1. Relational Database Design**
- Developed using **MySQL** to create and manage the database structure.
- Included tables for customers, orders, pizzas, toppings, and inventory.

### **2. Data Population and Maintenance**
- Populated tables with sample data.
- Supported **CRUD** (Create, Retrieve, Update, Delete) operations.

### **3. Java Integration**
- Implemented a **Java application** to interact with the database, using **JDBC** for seamless communication.
- Designed user-friendly features such as:
  - Adding new customers and orders.
  - Viewing and managing inventory.
  - Generating profitability reports (e.g., topping popularity, order types).

### **4. Advanced Functionality**
- Ensured data consistency with constraints and validations.
- Applied discounts dynamically to pizzas and orders.
- Maintained accurate inventory levels by updating quantities based on orders.

## **Project Highlights**

- **CRUD Operations**: The Java application allows users to manage customers, orders, and inventory efficiently.
- **Reports**: Utilized SQL views to generate insightful profitability reports.
- **Real-Time Updates**: Orders and inventory are synchronized with each transaction to ensure data accuracy.
- **Secure SQL Queries**: Prevented SQL injection attacks by implementing **prepared statements**.

## **Technologies Used**

- **MySQL**: For database creation and management.
- **Java**: To build the application, incorporating **JDBC** for database connectivity.
- **Prepared Statements**: To enhance security against SQL injection.
- **BufferedReader**: For efficient input handling in the Java application.

## **How to Use**

1. Clone this repository.
2. Set up the database by running the provided SQL scripts.
3. Update the `DBConnector.java` file with your MySQL connection details (server name, database name, username, and password).
4. Compile and run the Java application to interact with the database.


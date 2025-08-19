
# Spring Boot E-Commerce REST API

This project is a comprehensive e-commerce platform backend service developed using Spring Boot. It provides all the essential RESTful endpoints for managing customers, products, carts, and orders within a secure, role-based system. The project's main focus is on robust security implemented with JWT (JSON Web Tokens), detailed business logic, and relational database management.

##  Key Features & Business Logic

### Security Architecture:
-   **JWT Integration:** User authentication and authorization are handled using Spring Security and JWT.
-   **Role-Based Access Control (RBAC):** The system defines two distinct user roles: `USER` and `ADMIN`.
-   **Protected Endpoints:** Critical endpoints (e.g., adding a product, viewing user orders) are protected and accessible only by authorized roles.
-   **Data Isolation:** A user can only access their own cart and order information via their JWT token; access to other users' data is strictly prohibited.

### Customer Management:
-   New customer registration (`/auth/register`).
-   Login for registered users (`/auth/login`).

### Product Management:
-   Only users with the `ADMIN` role can create, update, or delete products.
-   All users can browse and list products.
-   **Stock Tracking:** The system prevents ordering a product once its stock runs out.

### Shopping Cart Management (Cart):
-   Each customer has a unique, single shopping cart.
-   Functionality to add products to the cart, remove them, or clear the cart entirely.
-   **Dynamic Price Calculation:** The total price of the cart is instantly recalculated and updated whenever an item is added or removed.

### Order Management (Order):
-   Allows users to place a new order using the items currently in their cart.
-   Users can list all of their past orders.
-   Ability to retrieve a specific order's details using a unique order code.
-   **Historical Price Guarantee:** When an order is placed, the prices of the items are saved specific to that order. Even if the product's price changes later, the user can view the price at which they originally purchased the item in their order history.

## 🛠️ Tech Stack
-   **Java 17**
-   **Spring Boot 3.x**
-   **Spring Security:** For authentication and authorization.
-   **JWT (JSON Web Token):** For securing the API.
-   **Spring Data JPA (Hibernate):** For database operations and ORM.
-   **Microsoft SQL Server:** As the primary database.
-   **Maven:** For project and dependency management.
-   **ModelMapper:** For converting between DTO and Entity classes.




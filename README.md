# BangMapleJDBCRepository
Inspired by the JpaRepository of Spring framework which also provides many capabilities for the operations like CRUD, Pagination and Sorting.

---------------
## 🛠 Features:

- CRUD Operations.
- Paging operations.
- Sorting operations.

## 🕹 Current situation:

- Support for Microsoft SQL Server (MSSQL).

## 🔌 To do:

- Add support for MySQL and PostgreSQL.
- Add support for the Paging and Sorting operations.

## ❓ How to use:
- Create a new project then add this library by download the jar file from the `Release` tab.
- Or you can clone this repository without having download the jar file.
- `Remember to also add the JDBC driver`.
- For Servlet environment, you can configure like this by creating a 
new class `ServletListener` or the class implementing the `ServletContextListener` interface :

![](./assets/1.png)
- For non-Servlet environment, navigate to your Main class and configure like this:

![](./assets/2.png)

## ❗️ Appendix:
- If you want to show the SQL Query while executing the methods, there is
a `JdbcRepository.DEBUG` variable needed to be set as `true`. Default is `false`.
- You may notice there is a `ConnectionManager` class.
    + This class responsible for getting the `Connection` instance.
    + If you use Data Source way, there is a file `context.xml` in the `META-INF` folder.
    + If you chose the way to create a new `context.xml` file, please 
  set the name of the data source by `JDBCRepository` or the `ConnectionManager` won't initialize your connection pool.

| Class  | Variable | Description | 
| ------------- | ------------- | ------------ |
| JdbcRepository  | DEBUG (boolean)  | Responsible for logging SQL queries while running the application.|
| ConnectionManager  | PROTOCOL (String)  | The protocol for connecting to the database vendor. For MSSQL: `jdbc:sqlserver` |
| ConnectionManager | HOST (String) | The host of the SQL server. Example: `localhost` |
| ConnectionManager | PORT (Integer) | The port number of the SQL server. Example: `1433` | 
| ConnectionManager | USERNAME (String) | The username for logging in to the SQL server. Example: `sa` |
| ConnectionManager | PASSWORD (String) | The password for logging in to the SQL server. Example: `IloveFPT` |

----------------
### 💌 Credits

- <a href="https://fb.com/bangmaple">BangMaple</a>
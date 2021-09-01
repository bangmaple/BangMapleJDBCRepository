# BangMapleJDBCRepository
Inspired by the JpaRepository of Spring framework which also provides many capabilities for the operations like CRUD, Pagination and Sorting.

---------------
## 🛠 Features:

- CRUD Operations:
  - FindById(id) -> entity: Find the associated entity by id (primary key).
  - FindAll() -> List<Entity>: Find all the records in the table.
  - Insert(entity) -> void: Performs insertion operation on the table with associated entity.
  - InsertAll(entities) -> void: Performs batch insertion operation on the table with associated entities.
  - DeleteById(id) -> void: Performs deletion operation on the table with associated primary key (id).
  - Count() -> long: Get the total number of records in the table.
  - Update(entity, id): Performs the update operation on the table with associated entity and primary key (id).
  - And many things more!
- Paging operations:
  - Pagable (upcoming).
- Sorting operations:
  - Ascending and Descending Order.


## 🕹 Current situation:

- Support for Microsoft SQL Server (MSSQL).

## 🔌 To do:

- Add support for MySQL and PostgreSQL.
- Add support for the Paging and Sorting operations.

----------------
### 💌 Credits

- <a href="https://fb.com/bangmaple">BangMaple</a>
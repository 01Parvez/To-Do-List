# 📚 Library Management System

A **production-quality full-stack Library Management System** built with **Spring Boot (Java 17)** and **React.js**.

---

## 🏗️ Architecture

```
library-management/
├── backend/                    # Spring Boot application
│   ├── src/main/java/com/library/
│   │   ├── controller/         # REST API endpoints
│   │   ├── service/            # Business logic
│   │   ├── repository/         # Database access (JPA)
│   │   ├── entity/             # JPA entities (DB tables)
│   │   ├── dto/                # Data Transfer Objects
│   │   │   ├── request/        # Incoming request DTOs
│   │   │   └── response/       # Outgoing response DTOs
│   │   ├── security/           # JWT + Spring Security
│   │   ├── config/             # App config, Data seeder
│   │   └── exception/          # Custom exceptions + handler
│   └── src/main/resources/
│       └── application.properties
├── frontend/                   # React application (Vite)
│   └── src/
│       ├── context/            # AuthContext (JWT state)
│       ├── services/           # Axios API calls
│       ├── pages/              # Full page components
│       └── components/         # Reusable components
├── schema.sql                  # Database schema
├── sample_data.sql             # Sample books data
└── postman_collection.json     # API test collection
```

---

## ⚙️ Prerequisites

| Tool     | Version | Download |
|----------|---------|---------|
| Java JDK | 17+     | [adoptium.net](https://adoptium.net) |
| Maven    | 3.8+    | [maven.apache.org](https://maven.apache.org) |
| MySQL    | 8.0+    | [mysql.com](https://dev.mysql.com/downloads/) |
| Node.js  | 18+     | [nodejs.org](https://nodejs.org) |

---

## 🚀 Setup Instructions

### Step 1: Configure MySQL

```sql
CREATE DATABASE library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Or apply the full schema:
```bash
mysql -u root -p < schema.sql
mysql -u root -p library_db < sample_data.sql
```

### Step 2: Configure Backend

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db?...
spring.datasource.username=root        # ← change to your MySQL user
spring.datasource.password=root        # ← change to your MySQL password
app.jwt.secret=YourSecureSecretKey    # ← change in production!
```

### Step 3: Run the Backend

```bash
cd backend
mvn spring-boot:run
```

Backend starts at: **http://localhost:8080**

> On first run, the `DataInitializer` automatically creates:
> - Roles: `ROLE_ADMIN`, `ROLE_USER`
> - Admin user: `admin` / `admin123`
> - Demo user: `user` / `user123`

### Step 4: Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend starts at: **http://localhost:5173**

### Step 5: Test with Postman

Import `postman_collection.json` from the project root into Postman.

---

## 🔑 Default Credentials

| Role  | Username | Password |
|-------|----------|----------|
| Admin | `admin`  | `admin123` |
| User  | `user`   | `user123` |

---

## 📡 API Endpoints

### Authentication (Public)
```
POST /api/auth/register   # Register new user
POST /api/auth/login      # Login → returns JWT token
```

### Books
```
GET    /api/books                    # List all books (public)
GET    /api/books/{id}               # Get book by ID (public)
GET    /api/books/search?keyword=    # Search books (public)
POST   /api/books                    # Add book (ADMIN)
PUT    /api/books/{id}               # Update book (ADMIN)
DELETE /api/books/{id}               # Delete book (ADMIN)
```

### Members (Admin only)
```
GET    /api/members                  # List members
POST   /api/members                  # Create member
PUT    /api/members/{id}             # Update member
DELETE /api/members/{id}             # Delete member
PATCH  /api/members/{id}/toggle-status  # Toggle ACTIVE/INACTIVE
```

### Borrowing
```
POST /api/borrow                     # Borrow a book
POST /api/borrow/return/{borrowId}   # Return a book
GET  /api/borrow/my-borrows          # My active borrows
GET  /api/borrow/user/{userId}       # User borrow history
GET  /api/borrow/overdue             # All overdue (Admin)
```

### Dashboard
```
GET /api/dashboard/admin   # Admin stats (ADMIN)
GET /api/dashboard/user    # Personal stats (any user)
```

---

## 🧪 Testing Flow (Postman)

1. **Register**: `POST /api/auth/register` → `{ "username": "alice", "email": "alice@test.com", "password": "pass123" }`
2. **Login**: `POST /api/auth/login` → copy the `token` from response
3. **Set Header**: `Authorization: Bearer <your-token>`
4. **Browse books**: `GET /api/books`
5. **Borrow a book**: `POST /api/borrow` → `{ "bookId": 1 }`
6. **Check dashboard**: `GET /api/dashboard/user`
7. **Return book**: `POST /api/borrow/return/1`

For admin operations, login with `admin/admin123`.

---

## 💡 Business Rules

| Rule | Detail |
|------|--------|
| Borrow duration | 14 days (configurable) |
| Fine per day    | ₹10 (configurable) |
| Fine formula    | `days_late × ₹10` |
| Borrow limit    | Cannot borrow same book twice (while active) |
| Copy check      | Cannot borrow if `availableCopies = 0` |
| Delete guard    | Cannot delete book with active borrows |

---

## 🔐 Security

- **JWT** - stateless authentication, 24-hour expiry
- **BCrypt** - password hashing with cost factor 10
- **RBAC** - `ROLE_ADMIN` and `ROLE_USER` with `@PreAuthorize`
- **CORS** - configured for `localhost:5173` (React dev server)

---

## 📖 API Documentation (Swagger)

Visit: **http://localhost:8080/swagger-ui.html**

Click **Authorize** → paste your JWT token to test protected endpoints.

---

## 🗄️ Database Schema

| Table       | Description |
|-------------|-------------|
| `users`     | User accounts (login credentials) |
| `roles`     | ROLE_ADMIN / ROLE_USER |
| `user_roles`| Many-to-many user ↔ role join table |
| `books`     | Book catalog |
| `members`   | Library member profiles |
| `borrows`   | Borrow transactions (history + active) |

---

## 🛠️ Technologies

**Backend:** Java 17 · Spring Boot 3.2 · Spring Security · Spring Data JPA · Hibernate · MySQL · JJWT · Lombok · SpringDoc (Swagger)

**Frontend:** React 18 · Vite · React Router 6 · Axios · react-hot-toast

---

## 📌 Learning Notes

This project demonstrates:
- **Layered Architecture** (Controller → Service → Repository → Entity)
- **DTO Pattern** separating API contracts from database entities
- **JWT Authentication** stateless security without server sessions
- **Spring Security** with role-based access control
- **JPA Relationships** (ManyToMany, ManyToOne, OneToOne)
- **Global Exception Handling** with consistent error responses
- **React Context API** for global state management
- **Axios Interceptors** for automatic JWT header injection

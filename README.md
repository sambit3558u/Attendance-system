# 📚 Smart Attendance System

A modern **Location-Based Smart Attendance System** built using **React.js**, **Spring Boot**, and **PostgreSQL**.

The system enables teachers to create live attendance sessions while allowing students to mark attendance securely using location verification and role-based authentication.

---

# 📅 Development Log

## Day 1 - Project Setup & Home Page Design (01 August 2026)

### ✅ Project Setup

- Initialized the project repository using Git.
- Created a React frontend project using Vite.
- Configured the development environment.
- Connected the local Git repository to GitHub.
- Successfully pushed the initial project to GitHub.

---

### 🎨 Frontend Development

Designed the complete landing page for the Smart Attendance System.

Implemented:

- Responsive Navigation Bar
- Professional Hero Section
- Live Attendance Preview Card
- Statistics Section
- Features Section
- How It Works Section
- Teacher & Student Role Section
- Security Section
- Benefits Section
- Call To Action (CTA)
- Professional Footer

---

### 🎨 UI / UX Improvements

Applied a modern dashboard design including:

- Professional Dark Theme
- Glassmorphism Effect
- Blue & Cyan Gradient Theme
- Smooth Hover Effects
- Responsive Layout
- Premium Buttons
- Live Status Indicator
- Custom Scrollbar
- Modern Typography
- Card Shadows
- Section Separators
- Interactive Animations

---

## Day 2 - Authentication UI & Registration System (02 August 2026)

### ✅ Project Structure

- Configured React Router for page navigation.
- Created separate pages:
  - Home
  - Login
  - Register
  - Teacher Dashboard
  - Student Dashboard
  - Not Found
- Connected all pages using React Router.
- Linked Home page Login and Register buttons to their respective routes.

---

### 🔐 Login Page Development

Designed a professional role-based Login page.

Implemented:

- Teacher Login
- Student Login
- Role Selection
- Email / Gmail Input
- Password Input
- Show / Hide Password
- Remember Me
- Forgot Password Button
- Back To Home Button
- Create New Account Button

---

### 📝 Registration Page Development

Created a professional role-based Registration page.

### 👨‍🏫 Teacher Registration

Added:

- Full Name
- Email ID
- Phone Number
- Employee ID
- Department Selection
- Password
- Confirm Password

Department List:

- DRIEMS Institute of Health Sciences and Hospital
- School of Engineering and Technology
- School of Paramedical
- School of Allied and Healthcare Sciences (I)
- School of Allied and Healthcare Sciences (II)
- School of Professional Studies
- School of Nursing
- School of Occupational and Physiotherapy
- School of Pharmacy
- School of Hotel Management
- School of Fashion Design
- School of Natural Sciences
- School of Management
- School of Humanities and Social Sciences
- School of Agriculture

---

### 🎓 Student Registration

Added:

- Full Name
- Gmail ID
- Phone Number
- Roll Number
- Branch Selection
- Semester Selection
- Password
- Confirm Password

Branch List added in alphabetical order.

Semester List:

- 1st Semester
- 2nd Semester
- 3rd Semester
- 4th Semester
- 5th Semester
- 6th Semester
- 7th Semester
- 8th Semester

---

### 🎨 UI / UX Improvements

Applied a modern authentication design including:

- Professional Dark Theme
- Glassmorphism Effect
- Blue & Cyan Gradient Theme
- Responsive Login Page
- Responsive Registration Page
- Professional Role Selection Cards
- Animated Buttons
- Interactive Input Fields
- Password Visibility Toggle
- Custom Dropdown Design
- Better Form Layout
- Responsive Grid Layout
- Mobile Friendly Design
- Professional Card Shadows
- Smooth Hover Animations
- Professional Form Validation UI

---

### 🔧 Git & Version Control

Completed:

- Initialized Git Repository
- Created Multiple Commits
- Connected Repository with GitHub
- Uploaded Latest Source Code
- Updated Project Documentation

---

# 🚀 Technology Stack

## Frontend

- React.js
- Vite
- React Router
- HTML5
- CSS3
- JavaScript (ES6+)

---

## Backend (Upcoming)

- Java 17
- Spring Boot
- Spring Security
- REST API
- JWT Authentication

---

## Database (Upcoming)

- PostgreSQL
- pgAdmin 4

---

## Development Tools

- Visual Studio Code
- Git
- GitHub
- Node.js
- npm

---

# ✨ Planned Features

## Authentication

- Teacher Login
- Student Login
- Role-Based Authentication
- JWT Authentication
- Secure Password Encryption

---

## Teacher Module

- Teacher Dashboard
- Create Live Class
- Manage Active Classes
- Attendance Reports
- Student Attendance History
- Export Attendance
- Teacher Profile

---

## Student Module

- Student Dashboard
- View Active Classes
- Mark Attendance
- Attendance History
- Student Profile

---

## Attendance System

- Live Attendance Session
- QR / Session Based Attendance
- Location Verification
- One Device Login
- Attendance Status
- Automatic Attendance Time
- Duplicate Attendance Prevention

---

## Security

- Role-Based Authorization
- JWT Token Authentication
- Password Encryption
- Location Validation
- Device Validation
- Secure API Access

---

# 📂 Current Project Structure

```text
attendance-system/
│
├── frontend/
│   │
│   ├── public/
│   │
│   ├── src/
│   │   │
│   │   ├── assets/
│   │   │
│   │   ├── pages/
│   │   │   ├── Home.jsx
│   │   │   ├── Home.css
│   │   │   ├── Login.jsx
│   │   │   ├── Login.css
│   │   │   ├── Register.jsx
│   │   │   ├── Register.css
│   │   │   ├── TeacherDashboard.jsx
│   │   │   ├── TeacherDashboard.css
│   │   │   ├── StudentDashboard.jsx
│   │   │   ├── StudentDashboard.css
│   │   │   ├── NotFound.jsx
│   │   │   └── NotFound.css
│   │   │
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css
│   │
│   ├── package.json
│   └── vite.config.js
│
├── README.md
│
└── .gitignore
```

---

# 📅 Day 3 - Backend Authentication System

## ✅ Work Completed

Today, the complete backend authentication system was implemented using Spring Boot, Spring Security, MySQL, and JWT.

---

## 🚀 Features Implemented

### User Registration

- Teacher Registration
- Student Registration
- Role-based Validation
- Duplicate Email Check
- Duplicate Phone Check
- Duplicate Roll Number Check
- Password Confirmation Validation
- BCrypt Password Hashing
- Save User into MySQL

---

### User Login

- Email Validation
- Password Verification
- Role Verification
- JWT Token Generation
- 30 Days Login Session
- Login Response API

---

### One Device Login (Student)

- First Login → Device ID saved into Database
- Same Device → Login Allowed
- Different Device → Login Blocked

---

### Teacher Login

- Multiple Device Login Allowed

---

### Logout

- Manual Logout API
- 5 Minutes Login Cooldown
- Logout Time Saved into Database

---

## 🔐 Security

- BCrypt Password Encryption
- JWT Authentication
- Spring Security Configuration
- Stateless Session
- CORS Configuration
- Custom Exception Handling

---

## 📂 Backend Structure

Implemented Packages

- config
- controller
- dto
- entity
- repository
- security
- service
- exception

---

## 📄 Files Created

- User.java
- Role.java
- UserRepository.java
- RegisterRequest.java
- LoginRequest.java
- AuthResponse.java
- JwtService.java
- AuthService.java
- AuthController.java
- SecurityConfig.java
- ApiException.java
- GlobalExceptionHandler.java

---

## 🗄 Database

Database : MySQL

Table Created

- users

Important Fields

- name
- email
- phone
- password
- role
- department
- rollNumber
- branch
- semester
- deviceId
- lastLogoutAt
- loginBlockedUntil
- createdAt
- updatedAt

---

## ✅ APIs Completed

POST /api/auth/register

POST /api/auth/login

POST /api/auth/logout

---

## 🧪 Testing

✔ Student Registration

✔ Teacher Registration

✔ Password Hashing

✔ Login Authentication

✔ JWT Generation

✔ One Device Login

✔ Duplicate Validation

✔ MySQL Integration

---

## 📌 Day 3 Summary

Successfully completed the backend authentication module with secure registration, login, JWT authentication, one-device login for students, logout cooldown, and MySQL database integration.

---

# 🔗 GitHub Repository

Repository:

**https://github.com/sambit3558u/Attendance-system**

---

## 👨‍💻 Author

**Sambit Kumar Patra**

GitHub: https://github.com/sambit3558u

LinkedIn: https://www.linkedin.com/in/sambit-kumar-patra-387a123a6**
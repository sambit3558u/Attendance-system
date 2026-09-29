# 📚 GEO-ATTEND — Smart Attendance System

## Project Overview

**GEO-ATTEND** is a modern, secure, responsive, and location-based Smart Attendance System developed using **React.js, Spring Boot, Spring Security, JWT Authentication, and MySQL**.

The system provides three separate workspaces:

* 🛡️ **Admin**
* 👨‍🏫 **Teacher**
* 🎓 **Student**

The main purpose of GEO-ATTEND is to provide educational institutions with a single digital platform for managing departments, branches, subjects, timetables, class sessions, attendance locations, teachers, students, and attendance records.

Students can mark their attendance using two primary methods:

* GPS-based location verification
* Secure QR-code scanning during a live class

Teachers are also allowed to manually mark a student as **Present** when the student is unable to mark attendance because of a technical issue.

---

# 🎯 Project Objective

Traditional attendance systems usually require teachers to manually record attendance. Such systems consume time and may also allow proxy attendance.

The GEO-ATTEND Smart Attendance System was developed to reduce these problems by using modern authentication, location verification, QR technology, and validation mechanisms.

The system includes:

* Role-based authentication
* GPS location verification
* Live class sessions
* Short-lived QR tokens
* Branch validation
* Semester validation
* Duplicate attendance prevention
* Student device validation
* Admin approval for new student devices
* Teacher attendance control
* Secure attendance records

The main objective of GEO-ATTEND is to provide administrators, teachers, and students with a secure, reliable, and easy-to-use digital attendance platform.

---

# ✨ Major Features

# 🛡️ Admin Module

The Admin has complete control over the Smart Attendance System.

The Admin module includes:

* Admin dashboard
* Total user count
* Total student count
* Total teacher count
* Total department count
* Total branch count
* Total subject count
* Total timetable count
* Total attendance record count
* User management
* Department management
* Branch management
* Subject management
* Timetable management
* Weekly timetable planner
* Student attendance-location management
* Teacher attendance-location management
* Attendance-record management
* Class-session management
* Student-device approval
* Accepting or rejecting new-device requests
* Automatic expiration of device requests after 24 hours
* Admin logout
* Secure Admin APIs

The Admin therefore acts as the central controller of the entire attendance platform.

---

# 👨‍🏫 Teacher Module

The Teacher workspace provides teachers with tools for managing classes, schedules, attendance, reports, and personal information.

The Teacher module includes:

* Teacher dashboard
* View total assigned classes
* View today's schedule
* View completed classes
* View upcoming classes
* View personal attendance
* View attendance for a specific date
* View monthly attendance
* Create a class session
* Select branch
* Select semester
* Select subject
* Select attendance location
* Select class start time
* Select class end time
* Start attendance
* Complete attendance
* Generate a class QR code
* Automatically regenerate the QR code every 15 seconds
* View registered students
* View students' attendance status
* Manually mark a student as Present
* Change Absent or Not Marked status to Present
* View attendance reports
* View date-wise class reports
* View students who were present in a class
* Teacher calendar
* Weekly timetable
* Teacher settings
* Teacher profile editing
* Forgot-password functionality
* Responsive mobile navigation

This allows the teacher to control the entire attendance process for a class.

---

# 🎓 Student Module

The Student workspace provides attendance, timetable, notification, profile, and live-class features.

The Student module includes:

* Student dashboard
* Student name and academic information
* Current attendance percentage
* Number of present classes
* Number of absent classes
* Semester attendance progress
* Previous-semester information
* Live-class information
* Today's timetable
* Attendance history
* Attendance date and time
* Subject information
* Teacher information
* GPS connection status
* GPS-based attendance
* QR attendance scanner
* Student notifications
* Student profile
* Student settings
* Mobile-responsive navigation drawer
* Forgot-password functionality
* Secure student authentication
* Only one attendance record per class session

The Student module gives students a complete overview of their classes and attendance.

---

# 📅 48-Day Development Log

## Day 1 — Project Planning and Requirement Analysis

### Work Completed

The Smart Attendance System project idea was finalized.

The three major user roles were identified:

* Admin
* Teacher
* Student

The workflow for GPS-based attendance was planned.

An initial concept for QR-based attendance was also designed.

Functional and non-functional requirements were prepared.

The technologies for the frontend, backend, and database were selected.

The project modules and folder structure were planned.

### Result

A complete initial roadmap and development plan for the project was prepared.

---

## Day 2 — React and Vite Frontend Setup

### Work Completed

A React application was created using Vite.

Node.js and npm configurations were verified.

The required project folders were created.

The following structure was prepared:

* `src/pages`
* `src/assets`

Global CSS configuration was added.

The frontend development server was successfully started.

A Git repository was initialized for version control.

### Result

The React frontend was successfully configured and was ready for development.

---

## Day 3 — Landing Page Development

### Work Completed

The main landing page was developed with the following sections:

* Responsive navigation bar
* Hero section
* Introduction to the Smart Attendance System
* Live attendance preview card
* System statistics
* Feature cards
* How It Works section
* Role information
* Call-to-Action section
* Footer

### Result

A professional landing page for the project was successfully completed.

---

## Day 4 — Landing Page UI and Responsive Design

### Work Completed

A dark navy theme was applied to the application.

Blue and cyan gradient colors were added.

Glassmorphism-style cards were created.

Hover animations were added.

Buttons were given professional styling.

Mobile navigation was improved.

Tablet and desktop layouts were optimized.

Typography and spacing were improved.

### Result

The landing page became responsive across mobile phones, tablets, and desktop computers.

---

## Day 5 — React Router Configuration

### Work Completed

React Router was configured.

Routes were created for:

* Home
* Login
* Register
* Admin Dashboard
* Teacher Dashboard
* Student Dashboard
* Not Found page

Navigation buttons were connected to their corresponding pages.

### Result

Proper client-side navigation was successfully implemented in the application.

---

## Day 6 — Login Page UI

### Work Completed

The Login page was developed with:

* Email input
* Password input
* Show/Hide Password option
* Remember Me option
* Forgot Password link
* Login button
* Registration link
* Loading state
* Error-message display
* Form validation

### Result

A professional and responsive login interface was successfully created.

---

## Day 7 — Registration Page UI

### Work Completed

Separate registration forms were created for Teachers and Students.

### Teacher Registration Fields

* Full name
* Email
* Phone number
* Employee ID
* Department
* Password
* Confirm password

### Student Registration Fields

* Full name
* Email
* Phone number
* Roll number
* Department
* Branch
* Semester
* Password
* Confirm password

### Result

A complete role-based registration interface was developed.

---

## Day 8 — Dynamic Department and Branch Selection

### Work Completed

The previously hardcoded department list was removed.

Departments were loaded dynamically from the backend API.

Branches were filtered according to the selected department.

Whenever the department was changed, the branch selection was reset.

Empty-department states and API-error states were handled.

The registration form was connected with dynamic academic data.

### Result

The registration form started using departments and branches created by the Admin instead of using fixed hardcoded data.

---

## Day 9 — Spring Boot Backend Setup

### Work Completed

The Spring Boot backend project was configured.

Java 17 was configured.

Required Maven dependencies were added.

The MySQL driver was added.

Spring Data JPA was configured.

Spring Security was added.

The backend package structure was created.

The major backend packages included:

* `config`
* `controller`
* `dto`
* `entity`
* `exception`
* `repository`
* `security`
* `service`

### Result

The backend project successfully compiled and ran.

---

## Day 10 — MySQL Database Integration

### Work Completed

A MySQL database named:

`attendance_system`

was created.

The database URL was configured.

The MySQL username and password configuration was added.

Hibernate:

`ddl-auto=update`

was enabled.

Entity tables were automatically generated.

The connection between the Spring Boot application and MySQL was tested.

### Result

The Spring Boot backend successfully connected to the MySQL database.

---

## Day 11 — User Registration Backend

### Work Completed

Backend registration functionality was implemented for Teachers and Students.

The following validations were added:

* Duplicate email validation
* Duplicate phone-number validation
* Duplicate Employee ID validation
* Duplicate roll-number validation
* Password confirmation
* Role assignment

BCrypt encryption was used to securely store passwords.

Registered-user information was persisted in the database.

### Result

Teacher and Student registration successfully started working with the backend.

---

## Day 12 — JWT Login Authentication

### Work Completed

A JWT service was created.

A Login Request DTO was created.

An Authentication Response DTO was created.

Email and password validation was added.

JWT tokens were generated after successful authentication.

JWT expiration was configured.

Authenticated-user information was included in the response.

Role-based dashboard redirection was implemented.

### Result

A secure JWT-based login system was successfully completed.

---

## Day 13 — Email-Based Role Detection

### Work Completed

The manual role selector was removed from the Login page.

The backend was updated to identify the user's role using the email address.

The system could automatically determine whether the account belonged to:

* Admin
* Teacher
* Student

Invalid-account and incorrect-password error handling was improved.

Frontend dashboard redirection was updated.

### Result

Users no longer needed to manually select their role while logging in.

---

## Day 14 — Spring Security and API Protection

### Work Completed

Spring Security configuration was implemented.

A JWT authentication filter was created.

Stateless session management was configured.

CORS configuration was added.

Public and protected routes were defined.

Role-based API authorization was implemented.

Unauthorized-access responses were improved.

### Result

Admin, Teacher, and Student APIs became securely protected.

---

## Day 15 — Admin Dashboard Layout

### Work Completed

The Admin dashboard was developed with a responsive sidebar.

The sidebar included:

* Dashboard
* Users
* Academic Structure
* Subjects
* Timetables
* Weekly Planner
* Student Locations
* Teacher Locations
* Attendance
* Sessions
* Logout

The Dashboard tab was configured as the default tab after Admin login.

### Result

The complete Admin Control Panel layout was successfully created.

---

## Day 16 — Admin Dashboard Statistics

### Work Completed

Dynamic dashboard statistic cards were added for:

* Total Users
* Total Students
* Total Teachers
* Total Departments
* Total Branches
* Total Subjects
* Total Timetables
* Attendance Records

Fake or hardcoded counts were not used.

All counts were loaded from the backend or existing application state.

### Result

The Admin started receiving a real-time overview of the system.

---

## Day 17 — Admin Quick Actions

### Work Completed

Quick Action options were added to the Admin Dashboard.

These included:

* Manage Users
* Academic Structure
* Subjects
* Timetables
* Weekly Planner
* Student Locations
* Teacher Locations
* Attendance
* Class Sessions

Each Quick Action was connected to its corresponding existing tab.

### Result

Navigation inside the Admin dashboard became faster and easier.

---

## Day 18 — Academic Structure Management

### Work Completed

The Academic Structure module was developed with:

* Add Department
* Edit Department
* Delete Department
* Add Branch
* Edit Branch
* Delete Branch
* Department-wise branch listing
* Dynamic summary
* Form validation
* API error handling

The following APIs were maintained:

```text
/api/admin/academic/departments
/api/admin/academic/branches
```

### Result

The Admin could dynamically create and manage departments and branches.

---

## Day 19 — User Management

### Work Completed

Admin User Management was developed with:

* View all users
* View Students
* View Teachers
* View individual user details
* Search
* Filtering
* Edit user
* Delete user
* Role display
* Department display
* Branch display
* Responsive user table

### Result

The Admin received complete control over user management.

---

## Day 20 — Subject Management

### Work Completed

The Subject module was developed with:

* Add Subject
* Edit Subject
* Delete Subject
* Subject code
* Subject name
* Department selection
* Branch selection
* Semester selection
* Teacher assignment
* Dynamic branch filtering

While editing a subject, the department was derived from the selected branch.

### Result

Problems related to missing or stale department information were resolved.

---

## Day 21 — Timetable Management

### Work Completed

The Timetable module was developed with:

* Add timetable
* Edit timetable
* Delete timetable
* Select department
* Select branch
* Select semester
* Select subject
* Select teacher
* Select day
* Start time
* End time
* Form validation

During timetable editing, department information was derived from branch data.

### Result

Complete CRUD functionality for timetable management was successfully implemented.

---

## Day 22 — Weekly Timetable Planner

### Work Completed

The existing `WeeklyTimetableManager` was preserved.

A day-wise weekly schedule was created.

The planner displayed:

* Subject details
* Teacher details
* Branch
* Semester
* Class time slots

A responsive timetable layout was implemented.

### Result

The Admin could easily view and manage the weekly academic schedule.

---

## Day 23 — Student Attendance Location Management

### Work Completed

The Student Attendance Location module was developed with:

* Place name
* Department
* Branch
* Latitude
* Longitude
* Radius
* Add Location
* Edit Location
* Delete Location

Whenever the department was changed, the dependent branch field was reset.

### Result

The Admin could configure branch-specific GPS attendance locations.

---

## Day 24 — Teacher Attendance Location Management

### Work Completed

The Teacher Location module was created with:

* Place name
* Department
* Latitude
* Longitude
* Allowed radius
* Location CRUD
* Coordinate validation
* Responsive location table

### Result

Location verification for Teachers' personal attendance was successfully implemented.

---

## Day 25 — Teacher Dashboard

### Work Completed

The Teacher Dashboard was developed with:

* Teacher profile information
* Welcome message
* Total classes
* Classes scheduled today
* Today's schedule
* Completed classes
* Upcoming classes
* Personal attendance
* Location verification
* Recent classes
* Important attendance information
* Responsive navigation

### Result

Teachers started receiving a complete overview of their daily academic activities.

---

## Day 26 — Teacher My Classes Module

### Work Completed

The My Classes section displayed:

* Subject name
* Subject code
* Branch
* Semester
* Class time
* Class status
* Student count
* Attendance action
* View Attendance button
* Complete Attendance button

### Result

Teachers could manage assigned and completed classes.

---

## Day 27 — Teacher Create Class

### Work Completed

The Class Creation form included:

1. Branch
2. Semester
3. Subject
4. Attendance Location
5. Start Time
6. End Time

Dependent selections were implemented.

The selection flow was:

* Select Branch
* Select Semester
* Select Subject

Attendance locations were filtered according to the selected branch.

### Result

Any logged-in Teacher could manually create a class session.

---

## Day 28 — Teacher Personal Attendance

### Work Completed

The Teacher Attendance page displayed:

* Present percentage
* Present count
* Absent count
* Total marked days
* Date-wise attendance history
* Specific-date filter
* Month-and-year filter
* Clear Filter option
* Filter-selection controls
* Attendance-status cards

### Result

Teachers could view their personal attendance for specific dates or months.

---

## Day 29 — Teacher Reports

### Work Completed

The Reports module displayed:

* Class date
* Subject
* Branch
* Semester
* Class timing
* Total students
* Present students
* Absent students
* View More button
* Individual student attendance details

### Result

Teachers could view detailed class-wise attendance reports.

---

## Day 30 — Teacher Calendar and Timetable

### Work Completed

The Teacher Calendar page included:

* Weekly timetable
* Day-wise schedule
* Subject name
* Subject code
* Branch
* Semester
* Start time
* End time
* Teacher information
* Responsive calendar cards

### Result

Teachers could view their complete weekly academic schedule.

---

## Day 31 — Teacher Settings

### Work Completed

The Teacher Settings page included controls for:

* Class reminders
* Attendance notifications
* Timetable-change notifications
* Report notifications
* System notifications
* Notification preferences
* Back to Dashboard button
* Responsive design

### Result

Teachers could control their notification preferences.

---

## Day 32 — Teacher Profile

### Work Completed

The Teacher Profile included:

* Full name
* Email ID
* Phone number
* Department
* Employee ID
* Profile information
* Edit Profile button
* Save Profile button
* Read-only mode
* Edit mode
* Back to Dashboard button

### Result

Teachers could view and edit their personal profile information.

---

## Day 33 — Student Dashboard

### Work Completed

The Student Dashboard included:

* Student avatar
* Student name
* Roll number
* Branch
* Semester
* Current attendance percentage
* Total classes
* Attendance statistics
* Semester progress
* Live-class information
* Today's timetable
* Attendance history
* GPS radar
* Notification button

### Result

Students received a complete overview of their attendance and timetable.

---

## Day 34 — Student GPS Attendance

### Work Completed

GPS attendance functionality was developed using:

* Browser Geolocation API
* Latitude capture
* Longitude capture
* Location accuracy information
* Backend distance validation
* Admin-configured allowed radius
* Live-class validation
* Branch validation
* Semester validation
* Present-status marking
* Error messages

### Result

Students could mark attendance when physically located within the allowed attendance area.

---

## Day 35 — Student Attendance History

### Work Completed

The Student Attendance page displayed:

* Present-class count
* Absent-class count
* Attendance percentage
* Semester progress
* Date
* Subject
* Teacher
* Status
* Attendance time
* Date-wise attendance history
* Responsive attendance table

### Result

Students could check their previous attendance records.

---

## Day 36 — Student Timetable

### Work Completed

The Student Timetable page included:

* Weekly class timetable
* Today's schedule
* Subject
* Subject code
* Teacher
* Start time
* End time
* Branch
* Semester
* Live-class status
* Completed-class status
* Responsive mobile layout

### Result

Students received access to their complete timetable for the current semester.

---

## Day 37 — Student Profile and Notifications

### Work Completed

The Student Profile contained:

* Full name
* Email
* Phone
* Roll number
* Department
* Branch
* Semester
* Profile editing

The Student Notifications module included:

* Attendance notifications
* Timetable notifications
* Class reminders
* System messages
* Read status
* Unread status

### Result

The Student Profile and Notification modules were completed.

---

## Day 38 — QR Attendance Backend

### Work Completed

The QR Attendance backend was developed with:

* QR-token generation
* Active-class linking
* 15-second QR expiration
* Automatic QR regeneration support
* Secure random tokens
* QR validation
* Student QR-claim API
* Attendance-record creation

### Result

A secure, short-lived QR attendance backend was completed.

---

## Day 39 — Teacher QR Interface

### Work Completed

The Teacher Class QR page included:

* Display QR section
* QR refresh timer
* Active-session validation
* Retry QR button
* Student attendance table
* Roll number
* Student name
* Attendance status
* Manual Present button
* Back to Dashboard button

### Result

Teachers could display a QR code for a live class and monitor student attendance.

---

## Day 40 — Student QR Scanner

### Work Completed

The Student QR module included:

* Camera scanner page
* Camera-permission handling
* QR-content reading
* QR-claim request
* Loading state
* Success message
* Invalid QR error
* Expired QR error
* Duplicate-attendance message

### Result

A logged-in Student could scan the Teacher's QR code and mark attendance.

---

## Day 41 — QR Security Validation

### Work Completed

Several security checks were implemented for QR attendance.

The system required:

* An active class
* A valid QR token
* A non-expired QR token
* Correct Student role
* Correct branch
* Correct semester
* Only one attendance record per session
* Duplicate QR claim prevention
* Teacher ownership validation

GPS verification could only be bypassed when a valid QR code was successfully verified.

### Result

The system reduced the possibility of proxy and duplicate QR attendance.

---

## Day 42 — Manual Attendance Override

### Work Completed

The Teacher Manual Attendance feature included:

* Registered Student list
* Roll-number display
* Student name
* Current attendance status
* Mark Present button
* Change Absent to Present
* Change Not Marked to Present
* Automatic list refresh
* Authorization validation

### Result

If a student experienced a technical problem, the Teacher could manually mark that Student as Present.

---

## Day 43 — Forgot Password

### Work Completed

The Forgot Password module included:

* Registered-email input
* Email-based account detection
* Reset-token generation
* 10-minute reset-token expiration
* New password input
* Confirm password
* BCrypt password update
* Invalid-token handling
* Expired-token handling
* Teacher support
* Student support

### Result

Teachers and Students could securely reset forgotten passwords.

---

## Day 44 — Student Device Approval

### Work Completed

A new-device login workflow was implemented.

The system included:

* Student device-ID capture
* First-device registration
* Different-device detection
* Pending approval request
* 24-hour request expiration
* Duplicate pending-request prevention
* Login blocking until approval
* Request-status tracking

The supported statuses were:

```text
PENDING
ACCEPTED
REJECTED
EXPIRED
```

### Result

Unauthorized Student login attempts from unknown devices could be controlled.

---

## Day 45 — Admin Device Approval

### Work Completed

The following Admin APIs were added:

```text
GET /api/admin/device-approvals

PATCH /api/admin/device-approvals/{id}/accept

PATCH /api/admin/device-approvals/{id}/reject
```

The Admin could:

* View pending requests
* Check Student details
* Accept a new device
* Reject a request
* Handle expired requests

### Result

The Admin gained control over Student access from new devices.

---

## Day 46 — Mobile and Tablet Responsiveness

### Work Completed

Admin, Teacher, and Student pages were optimized for multiple screen sizes.

The following areas were improved:

* Mobile typography
* Tablet layouts
* Responsive forms
* Responsive cards
* Button sizes
* Text wrapping
* Table visibility
* Navigation
* Header alignment
* Touch-friendly controls
* Mobile attendance pages
* QR attendance page

### Result

The complete application became suitable for mobile, tablet, and desktop devices.

---

## Day 47 — Student Mobile Navigation

### Work Completed

A mobile navigation drawer was developed for Student pages.

The menu contained:

* Dashboard
* Attendance
* Timetable
* Scan QR
* Profile
* Notifications
* Logout

Additional improvements included:

* Top-right hamburger button
* Slide-in navigation panel
* Active-page highlighting
* Solid navy background
* Menu backdrop
* Correct z-index
* Header-overlap fix
* Mobile notification button

### Result

Student mobile navigation became cleaner, more accessible, and responsive.

---

## Day 48 — Final Testing, Bug Fixing, and Documentation

### Bugs Fixed

The following problems were corrected:

* Admin dashboard compilation problem
* Department dropdown problem
* Dynamic branch-loading problem
* Stale branch selection
* Subject-edit department problem
* Timetable-edit department problem
* Attendance-location filtering
* Teacher-profile data problem
* Student-profile authentication problem
* QR-generation errors
* QR-authorization errors
* Duplicate Student-attendance problem
* Attendance-statistics refresh problem
* Mobile-table overflow
* Button-sizing problems
* Text-wrapping problems
* Navigation z-index problem
* Header and menu overlap
* Mobile responsive-alignment problems

### Final Testing

The following features were tested:

* Teacher registration
* Student registration
* Login
* Email-based role detection
* JWT authentication
* Admin CRUD operations
* Department and branch dependency
* Subject CRUD
* Timetable CRUD
* Location CRUD
* Teacher class creation
* GPS attendance
* QR attendance
* Manual attendance
* Duplicate-attendance prevention
* Forgot password
* Student profile
* Teacher profile
* Device approval
* Mobile responsiveness
* Frontend production build

### Documentation Completed

Documentation was prepared for:

* Project introduction
* Project features
* Setup instructions
* Backend run instructions
* Frontend run instructions
* Database setup
* API overview
* Security
* Attendance workflow
* Testing checklist
* Troubleshooting
* 48-day development log

### Final Result

The Admin, Teacher, and Student workflows of the GEO-ATTEND Smart Attendance System were successfully developed.

The completed system included:

* Secure authentication
* Dynamic academic structure
* Timetable management
* GPS attendance
* QR attendance
* Manual attendance correction
* Attendance reports
* Device approval
* Forgot-password functionality
* Responsive user interface
* Complete project documentation

---

# 🏁 48-Day Project Summary

During the 48-day development period, a complete full-stack Smart Attendance System was developed.

The following major technologies and modules were integrated:

* React frontend
* Spring Boot backend
* MySQL database
* JWT authentication
* Spring Security
* Admin management
* Teacher workspace
* Student workspace
* GPS verification
* QR attendance
* Device security
* Password recovery
* Responsive design

The project can be used for:

* College mini projects
* Final-year academic projects
* Attendance-management demonstrations
* Full-stack development portfolios
* Location-based and QR-based attendance research

---

# 🚀 Technology Stack

## Frontend

The frontend uses:

* React.js
* Vite
* React Router
* JavaScript ES6+
* HTML5
* CSS3
* Browser Geolocation API
* Camera-based QR scanning

React.js is used to create the user interface, while Vite provides the frontend development environment.

React Router handles navigation between pages.

The Browser Geolocation API is used for obtaining a Student's location during GPS attendance.

Camera access is used for scanning QR codes.

---

## Backend

The backend uses:

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* REST APIs
* JWT Authentication
* BCrypt Password Encryption
* Maven

Spring Boot provides the main backend framework.

Spring Security protects APIs and controls authentication and authorization.

JWT tokens are used for stateless authentication.

BCrypt is used for secure password hashing.

Spring Data JPA and Hibernate manage communication between Java entities and the database.

---

## Database

The system uses:

* MySQL
* MySQL Workbench

MySQL stores user details, academic information, class sessions, attendance records, and other system information.

MySQL Workbench can be used to manage and inspect the database.

---

## Development Tools

The project uses or supports:

* Visual Studio Code
* IntelliJ IDEA or Eclipse
* Git
* GitHub
* Postman
* Node.js
* npm
* Maven Wrapper

---

# 📂 Project Structure

```text
attendance-system/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/smartattendance/backend/
│   │   │   │       ├── config/
│   │   │   │       ├── controller/
│   │   │   │       ├── dto/
│   │   │   │       ├── entity/
│   │   │   │       ├── exception/
│   │   │   │       ├── repository/
│   │   │   │       ├── security/
│   │   │   │       └── service/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── assets/
│   │   ├── pages/
│   │   │   ├── AdminDashboard.jsx
│   │   │   ├── ForgotPassword.jsx
│   │   │   ├── Home.jsx
│   │   │   ├── Login.jsx
│   │   │   ├── Register.jsx
│   │   │   ├── StudentAttendance.jsx
│   │   │   ├── StudentDashboard.jsx
│   │   │   ├── StudentMobileMenu.jsx
│   │   │   ├── StudentNotifications.jsx
│   │   │   ├── StudentProfile.jsx
│   │   │   ├── StudentQrAttendance.jsx
│   │   │   ├── StudentSettings.jsx
│   │   │   ├── StudentTimetable.jsx
│   │   │   ├── TeacherAttendance.jsx
│   │   │   ├── TeacherClassAttendance.jsx
│   │   │   ├── TeacherClasses.jsx
│   │   │   ├── TeacherCreateClass.jsx
│   │   │   ├── TeacherDashboard.jsx
│   │   │   ├── TeacherProfile.jsx
│   │   │   ├── TeacherSettings.jsx
│   │   │   └── WeeklyTimetableManager.jsx
│   │   ├── App.jsx
│   │   ├── Responsive.css
│   │   ├── index.css
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
│
├── README.md
└── .gitignore
```

---

# ⚙️ System Requirements

Before running GEO-ATTEND, install the following software:

* Java 17 or later
* Node.js 18 or later
* npm
* MySQL Server
* MySQL Workbench
* Git

Check the installed versions using:

```bash
java -version
node -v
npm -v
git --version
```

---

# 🗄️ Database Setup

Create the MySQL database:

```sql
CREATE DATABASE attendance_system;
```

Open:

```text
backend/src/main/resources/application.properties
```

Configure the database:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/attendance_system
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

For security reasons, the actual MySQL password should not be directly stored in a GitHub repository.

Instead, use an environment variable.

For PowerShell:

```powershell
$env:DB_PASSWORD="your_mysql_password"
```

---

# ▶️ Running the Backend

Open PowerShell.

Navigate to the backend directory:

```powershell
cd "D:\Mini Project\attendance-system\backend"
```

Run the Spring Boot application:

```powershell
.\mvnw.cmd spring-boot:run
```

To clean the project before running:

```powershell
.\mvnw.cmd clean spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

---

# ▶️ Running the Frontend

Open another PowerShell terminal.

Navigate to the frontend directory:

```powershell
cd "D:\Mini Project\attendance-system\frontend"
```

Install required dependencies:

```powershell
npm install
```

Start the Vite development server:

```powershell
npm run dev
```

The frontend runs at:

```text
http://localhost:5173
```

To create a production build:

```powershell
npm run build
```

---

# 🔗 API Overview

## Authentication APIs

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/forgot-password
POST /api/auth/reset-password
```

These APIs manage registration, authentication, logout, and password recovery.

---

## Admin APIs

```text
/api/admin
/api/admin/academic/departments
/api/admin/academic/branches
/api/admin/device-approvals
```

Admin APIs are responsible for administrative operations, academic structures, and device approvals.

---

## Teacher APIs

```text
/api/teacher
```

Teacher APIs manage:

* Dashboard
* Classes
* Attendance
* QR generation
* Manual attendance
* Reports
* Profile
* Settings

---

## Student APIs

```text
/api/student
```

Student APIs manage:

* Dashboard
* Live class
* GPS attendance
* QR claim
* Timetable
* Attendance history
* Profile
* Notifications
* Settings

---

# 🔐 Authentication Flow

The login process works as follows:

```text
User enters email and password
            ↓
Backend searches for the account using email
            ↓
Password is verified using BCrypt
            ↓
Account role is identified
            ↓
Student device is validated
            ↓
JWT token is generated
            ↓
User is redirected to the appropriate dashboard
```

### Explanation

First, the user provides an email address and password.

The backend searches for an account associated with that email.

If an account exists, the password is compared with the securely encrypted BCrypt password.

The system then determines whether the account belongs to an Admin, Teacher, or Student.

For a Student account, device validation is also performed.

After successful authentication, a JWT token is generated.

The authenticated user is then redirected to the appropriate dashboard.

---

# 📍 GPS Attendance Flow

GPS attendance works according to the following process:

```text
Admin configures attendance location
                ↓
Teacher starts a live class session
                ↓
Student connects device GPS
                ↓
Frontend sends latitude and longitude
                ↓
Backend calculates location distance
                ↓
Student inside allowed radius?
          ↓ Yes              ↓ No
Attendance Present      Attendance Rejected
```

### Explanation

The Admin first creates an attendance location and specifies its latitude, longitude, and allowed radius.

The Teacher starts a live class session using the appropriate attendance location.

The Student enables GPS on the device.

The browser retrieves the Student's latitude and longitude.

The coordinates are sent to the backend.

The backend calculates the distance between the Student's current location and the location configured by the Admin.

If the Student is within the allowed radius, attendance is marked as Present.

If the Student is outside the permitted area, the attendance request is rejected.

---

# ▣ QR Attendance Flow

QR attendance works as follows:

```text
Teacher starts class
        ↓
Teacher generates QR
        ↓
QR regenerates every 15 seconds
        ↓
Logged-in Student scans QR
        ↓
Backend validates token and class
        ↓
Backend validates branch and semester
        ↓
Backend checks duplicate attendance
        ↓
Attendance is marked Present
```

### Explanation

First, the Teacher starts a class.

The Teacher generates a QR code for the active session.

The QR token is valid for a short period and is regenerated every 15 seconds.

The logged-in Student scans the QR code using the Student QR Scanner.

The QR information is sent to the backend.

The backend verifies:

* Whether the token is valid
* Whether the class is active
* Whether the Student belongs to the correct branch
* Whether the Student belongs to the correct semester
* Whether attendance has already been recorded

If all validations are successful, the Student's attendance is marked as Present.

---

# 🔒 Security Features

GEO-ATTEND includes several security mechanisms.

## JWT Authentication

JWT authentication is used for securely identifying authenticated users.

## BCrypt Password Encryption

Passwords are not stored in plain text. BCrypt encryption is used to securely store passwords.

## Role-Based Authorization

Different APIs are protected according to the role of the authenticated user.

Admin, Teacher, and Student users therefore receive different access permissions.

## Stateless Authentication

The backend uses stateless authentication so that authentication information is handled using JWT tokens instead of traditional server sessions.

## CORS Configuration

CORS is configured so that frontend-backend communication can be securely managed.

## Protected REST APIs

Sensitive REST endpoints are protected by Spring Security.

## Duplicate Attendance Prevention

A Student is prevented from marking attendance multiple times for the same class session.

## GPS Location Validation

The Student's current location is verified against the configured attendance location.

## QR Token Expiration

QR codes are short-lived and expire quickly.

## Active Session Validation

Attendance can only be marked against an appropriate active class session.

## Branch and Semester Validation

The system verifies that the Student belongs to the correct branch and semester.

## Student Device Validation

Student devices are validated during authentication.

## Admin Device Approval

When a Student attempts to log in from a different device, Admin approval may be required.

## Password Reset Token Expiration

Password-reset tokens expire after a limited period.

## Error Handling

Errors are handled and appropriate responses are returned.

## Input Validation

User input is validated before important operations are processed.

---

# 🧪 Testing Requirements

The following tests should be maintained for the project.

## Authentication Tests

* Admin login
* Teacher login
* Student login
* Invalid password
* Unknown email
* Registration
* Duplicate email
* Duplicate roll number
* Forgot password
* Expired reset token

## Admin Tests

* Department CRUD
* Branch CRUD
* Subject CRUD
* Timetable CRUD
* Location CRUD
* User management
* Device approval
* Unauthorized Admin API access

## Teacher Tests

* Create class
* Start attendance
* Complete attendance
* Generate QR
* Expired QR
* Manual Present override
* View Student list
* View reports

## Student Tests

* GPS attendance
* Out-of-range attendance
* QR attendance
* Duplicate attendance
* Wrong branch
* Wrong semester
* Attendance history
* Profile update

---

# End-to-End QR Attendance Test

The complete QR attendance process should be tested as follows:

```text
Teacher starts class
        ↓
Teacher displays QR
        ↓
Student scans QR
        ↓
Attendance record is created
        ↓
Teacher Student list is refreshed
        ↓
Student status shows PRESENT
```

This verifies that QR attendance works from the Teacher side to the Student side and that the final attendance record is properly updated.

---

# 🛠️ Troubleshooting

## Backend Does Not Start

Check whether:

* MySQL Server is running
* The database exists
* The MySQL username is correct
* The MySQL password is correct
* Port 8080 is available
* Java 17 is installed

---

## Frontend Cannot Connect to Backend

Confirm that the backend is running at:

```text
http://localhost:8080
```

If the backend is not running, frontend API requests will fail.

---

## Department Dropdown Is Empty

Check whether:

* The Admin has created a department
* The Admin has created branches inside the department
* Academic APIs are responding correctly
* The frontend API URL is correct

---

## Attendance Location Is Not Showing

Check whether:

* The location was configured by the Admin
* The location branch matches the selected class branch
* The correct semester and subject are selected
* The backend successfully returned location data

---

## Changes Are Not Visible

Perform a hard refresh in the browser:

```text
Ctrl + Shift + R
```

Restart Vite if necessary:

```powershell
npm run dev
```

---

## Frontend Build Fails

Run the build command from inside the frontend folder:

```powershell
cd "D:\Mini Project\attendance-system\frontend"

npm run build
```

---

# 🚀 Future Improvements

The following improvements are planned for future versions of the project:

* Real email delivery for password resets
* SMS OTP
* Email OTP
* Push notifications
* Leave-request workflow
* Attendance CSV export
* Attendance PDF reports
* Academic-year management
* Advanced timetable-conflict detection
* Face verification
* Improved offline support
* Progressive Web Application (PWA)
* Automated deployment
* Docker support
* Complete automated testing suite
* Performance optimization
* Audit logs

These improvements can extend the current system and make it more suitable for larger institutional use.

---

# ✅ Project Status

The project currently includes working modules for:

* Authentication
* Registration
* Forgot Password
* Admin Dashboard
* Academic Structure
* User Management
* Subject Management
* Timetable Management
* Weekly Planner
* Attendance Locations
* Teacher Dashboard
* Teacher Classes
* Teacher Attendance
* Teacher Reports
* Teacher Profile
* Student Dashboard
* Student Attendance
* Student Timetable
* Student Profile
* GPS Attendance
* QR Attendance
* Manual Attendance Override
* Device Approval
* Responsive Design

---

# 🔗 GitHub Repository

The source code for the project is available in the following GitHub repository:

```text
https://github.com/sambit3558u/Attendance-system
```

---

# 👨‍💻 Author

**Sambit Kumar Patra**

GitHub:

```text
https://github.com/sambit3558u
```

LinkedIn:

```text
https://www.linkedin.com/in/sambit-kumar-patra-387a123a6
```

---

# 📄 License

This project was developed for **educational and academic purposes**.

---

# Final Project Explanation

GEO-ATTEND is a complete full-stack Smart Attendance System that combines traditional attendance management with modern security and verification mechanisms.

The frontend is developed using React.js, while the backend is developed using Spring Boot. MySQL is used for persistent data storage.

The system separates users into Admin, Teacher, and Student roles.

The Admin manages the overall academic structure, users, subjects, timetables, attendance locations, sessions, records, and Student device approvals.

Teachers can manage their classes, generate secure QR codes, monitor Student attendance, manually correct attendance when required, view reports, and monitor their own personal attendance.

Students can view their timetable, attendance history, live classes, profile information, and mark attendance using either GPS verification or secure QR scanning.

The GPS attendance system verifies whether the Student is physically located within the attendance radius configured by the Admin.

The QR attendance system uses short-lived tokens that are regenerated every 15 seconds. This improves security by reducing the usefulness of copied or previously captured QR codes.

Branch, semester, active-session, role, device, and duplicate-attendance validations provide additional protection.

JWT and Spring Security protect the application APIs, while BCrypt protects stored passwords.

The Student-device approval workflow provides an additional security layer by controlling access when a Student attempts to sign in using a different device.

As a result, GEO-ATTEND combines **academic management, attendance tracking, GPS verification, QR attendance, user authentication, security, reporting, and responsive design** in a single full-stack application.

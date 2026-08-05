# Smart Attendance Backend (Short Notes)

## 📁 config/

### SecurityConfig.java
Purpose:
- Spring Security Configure
- BCrypt Enable
- Public APIs
- Protected APIs
- CORS
- CSRF Disable
- Stateless Session

Functions:
- passwordEncoder() → BCrypt Object
- securityFilterChain() → Security Rules

---

## 📁 controller/

### AuthController.java
Purpose:
- Receive Frontend Request
- Call AuthService

APIs:
- POST /register
- POST /login
- POST /logout

---

## 📁 dto/

### RegisterRequest.java
Purpose:
Receive Registration Data

Fields:
Name, Email, Phone, Password, ConfirmPassword, Role, Department, RollNumber, Branch, Semester

### LoginRequest.java
Purpose:
Receive Login Data

Fields:
Email, Password, Role, DeviceId

### AuthResponse.java
Purpose:
Send Response

Fields:
Success, Message, AccessToken, ExpiresIn, UserId, Name, Email, Role

---

## 📁 entity/

### User.java
Purpose:
Represents users Table

Fields:
id, name, email, phone, password, role, department, rollNumber, branch, semester, deviceId, lastLogoutAt, loginBlockedUntil, createdAt, updatedAt

Annotations:
- @Entity → Database Table
- @Table → Table Name
- @Id → Primary Key
- @GeneratedValue → Auto Increment
- @Column → Column Settings
- @Enumerated → Store Enum as String
- @CreationTimestamp → Created Time
- @UpdateTimestamp → Updated Time

### Role.java
Purpose:
Stores Allowed Roles

Values:
- TEACHER
- STUDENT

---

## 📁 repository/

### UserRepository.java
Purpose:
Connect Java with MySQL

Functions:
- save() → Save User
- findByEmail() → Find User
- existsByEmail() → Duplicate Email
- existsByPhone() → Duplicate Phone
- existsByRollNumber() → Duplicate Roll Number

---

## 📁 service/

### AuthService.java
Purpose:
Authentication Logic

Functions:

register()
- Validate Data
- Duplicate Check
- Hash Password
- Role Validation
- Save User

login()
- Email Check
- Password Check
- Role Check
- Cooldown Check
- Device Check
- Generate JWT

logout()
- Save Logout Time
- Block Login 5 Minutes

validatePasswords()
- Password Validation

validateLoginCooldown()
- Check Login Cooldown

normalizeEmail()
- Trim + Lowercase

normalizePhone()
- Validate 10 Digits

normalizeRequiredText()
- Check Blank
- Trim Spaces

---

## 📁 security/

### JwtService.java
Purpose:
JWT Management

Functions:
- generateToken() → Create JWT
- extractEmail() → Get Email
- extractRole() → Get Role
- extractUserId() → Get User ID
- extractExpiration() → Get Expiry
- isTokenExpired() → Expiry Check
- isTokenValid() → Token Validation

---

## 📁 exception/

### ApiException.java
Purpose:
Custom Error

### GlobalExceptionHandler.java
Purpose:
Return JSON Error Response

---

## application.properties

Purpose:
Project Configuration

Contains:
- DB URL
- Username
- Password
- Port
- JWT Secret
- JWT Expiration
- Cooldown
- Hibernate Config

---

## pom.xml

Purpose:
Project Dependencies

Dependencies:
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Validation
- MySQL
- Lombok
- JJWT

---

## Register Flow

Frontend
↓
Controller
↓
Service
↓
Repository
↓
Database
↓
Response

---

## Login Flow

Email
↓
Password
↓
Role
↓
Student?
↓
Device Check
↓
JWT
↓
Success

---

## Logout Flow

Logout
↓
Save Time
↓
5 Min Block
↓
Database
↓
Success
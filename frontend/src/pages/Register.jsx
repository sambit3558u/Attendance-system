import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Register.css";

function Register() {
    const navigate = useNavigate();

    const [role, setRole] = useState("teacher");
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        phone: "",
        department: "",
        rollNumber: "",
        branch: "",
        semester: "",
        password: "",
        confirmPassword: "",
    });

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((previousData) => ({
            ...previousData,
            [name]: value,
        }));
    };

    const handleRoleChange = (selectedRole) => {
        setRole(selectedRole);

        setFormData((previousData) => ({
            ...previousData,

            // Teacher field reset
            department:
                selectedRole === "teacher"
                    ? previousData.department
                    : "",

            // Student fields reset
            rollNumber:
                selectedRole === "student"
                    ? previousData.rollNumber
                    : "",

            branch:
                selectedRole === "student"
                    ? previousData.branch
                    : "",

            semester:
                selectedRole === "student"
                    ? previousData.semester
                    : "",
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (isLoading) {
            return;
        }

        if (formData.password !== formData.confirmPassword) {
            alert("Password and Confirm Password do not match.");
            return;
        }

        const registrationData = {
            name: formData.name.trim(),
            email: formData.email.trim(),
            phone: formData.phone.trim(),
            password: formData.password,
            confirmPassword: formData.confirmPassword,
            role: role.toUpperCase(),

            ...(role === "teacher" && {
                department: formData.department,
            }),

            ...(role === "student" && {
                rollNumber: formData.rollNumber.trim(),
                branch: formData.branch,
                semester: Number(formData.semester),
            }),
        };

        setIsLoading(true);

        try {
            const response = await fetch(
                "http://localhost:8080/api/auth/register",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(registrationData),
                }
            );

            const data = await response.json();

            if (!response.ok) {
                throw new Error(
                    data.message ||
                    "Registration failed. Please try again."
                );
            }

            alert(data.message || "Registration successful");

            navigate("/login");

        } catch (error) {
            console.error("Registration error:", error);

            if (error instanceof TypeError) {
                alert(
                    "Unable to connect to backend. Make sure Spring Boot is running on port 8080."
                );
            } else {
                alert(error.message);
            }

        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="register-page">
            <div className="register-background-circle register-circle-one"></div>
            <div className="register-background-circle register-circle-two"></div>

            <header className="register-navbar">
                <div
                    className="register-logo"
                    onClick={() => navigate("/")}
                    role="button"
                    tabIndex={0}
                    onKeyDown={(event) => {
                        if (
                            event.key === "Enter" ||
                            event.key === " "
                        ) {
                            navigate("/");
                        }
                    }}
                >
                    <div className="register-logo-icon">
                        SA
                    </div>

                    <div>
                        <h2>Smart Attendance</h2>
                        <p>Location Based Attendance System</p>
                    </div>
                </div>

                <button
                    type="button"
                    className="register-back-home-button"
                    onClick={() => navigate("/")}
                >
                    ← Back to Home
                </button>
            </header>

            <main className="register-main">
                <section className="register-information">
                    <span className="register-tag">
                        CREATE ACCOUNT
                    </span>

                    <h1>Join Smart Attendance</h1>

                    <p>
                        Create your teacher or student account
                        to access the attendance system.
                    </p>
                </section>

                <section className="register-card">
                    <div className="register-card-heading">
                        <span>NEW ACCOUNT</span>

                        <h2>Create your account</h2>

                        <p>
                            Select your role and complete the
                            registration form.
                        </p>
                    </div>

                    <div className="register-role-selector">
                        <button
                            type="button"
                            className={`register-role-option ${role === "teacher"
                                    ? "active register-teacher-active"
                                    : ""
                                }`}
                            onClick={() =>
                                handleRoleChange("teacher")
                            }
                            aria-pressed={role === "teacher"}
                            disabled={isLoading}
                        >
                            <span className="register-role-icon">
                                👨‍🏫
                            </span>

                            <span>
                                <strong>Teacher</strong>
                                <small>
                                    Create and manage classes
                                </small>
                            </span>
                        </button>

                        <button
                            type="button"
                            className={`register-role-option ${role === "student"
                                    ? "active register-student-active"
                                    : ""
                                }`}
                            onClick={() =>
                                handleRoleChange("student")
                            }
                            aria-pressed={role === "student"}
                            disabled={isLoading}
                        >
                            <span className="register-role-icon">
                                🎓
                            </span>

                            <span>
                                <strong>Student</strong>
                                <small>
                                    Join classes and mark attendance
                                </small>
                            </span>
                        </button>
                    </div>

                    <form
                        className="register-form"
                        onSubmit={handleSubmit}
                    >
                        <div className="register-form-grid">
                            {/* Full Name */}

                            <div className="register-form-group">
                                <label htmlFor="name">
                                    Full Name
                                </label>

                                <div className="register-input-wrapper">
                                    <span className="register-input-icon">
                                        👤
                                    </span>

                                    <input
                                        id="name"
                                        name="name"
                                        type="text"
                                        value={formData.name}
                                        onChange={handleChange}
                                        placeholder="Enter your full name"
                                        autoComplete="name"
                                        minLength={2}
                                        disabled={isLoading}
                                        required
                                    />
                                </div>
                            </div>

                            {/* Email */}

                            <div className="register-form-group">
                                <label htmlFor="email">
                                    {role === "student"
                                        ? "Gmail ID"
                                        : "Email ID"}
                                </label>

                                <div className="register-input-wrapper">
                                    <span className="register-input-icon">
                                        ✉
                                    </span>

                                    <input
                                        id="email"
                                        name="email"
                                        type="email"
                                        value={formData.email}
                                        onChange={handleChange}
                                        placeholder={
                                            role === "student"
                                                ? "Enter your Gmail ID"
                                                : "Enter your email ID"
                                        }
                                        autoComplete="email"
                                        disabled={isLoading}
                                        required
                                    />
                                </div>
                            </div>

                            {/* Phone Number */}

                            <div className="register-form-group">
                                <label htmlFor="phone">
                                    Phone Number
                                </label>

                                <div className="register-input-wrapper">
                                    <span className="register-input-icon">
                                        📞
                                    </span>

                                    <input
                                        id="phone"
                                        name="phone"
                                        type="tel"
                                        value={formData.phone}
                                        onChange={handleChange}
                                        placeholder="Enter 10-digit phone number"
                                        autoComplete="tel"
                                        inputMode="numeric"
                                        pattern="[0-9]{10}"
                                        maxLength={10}
                                        disabled={isLoading}
                                        required
                                    />
                                </div>
                            </div>

                            {/* Teacher Department */}

                            {role === "teacher" && (
                                <div className="register-form-group">
                                    <label htmlFor="department">
                                        Department
                                    </label>

                                    <div className="register-input-wrapper">
                                        <span className="register-input-icon">
                                            🏫
                                        </span>

                                        <select
                                            id="department"
                                            name="department"
                                            value={formData.department}
                                            onChange={handleChange}
                                            disabled={isLoading}
                                            required
                                        >
                                            <option value="">
                                                Select Department
                                            </option>

                                            <option value="DRIEMS Institute of Health Sciences and Hospital">
                                                DRIEMS Institute of Health Sciences and Hospital
                                            </option>

                                            <option value="School of Engineering and Technology">
                                                School of Engineering and Technology
                                            </option>

                                            <option value="School of Paramedical">
                                                School of Paramedical
                                            </option>

                                            <option value="School of Allied and Healthcare Sciences (I)">
                                                School of Allied and Healthcare Sciences (I)
                                            </option>

                                            <option value="School of Allied and Healthcare Sciences (II)">
                                                School of Allied and Healthcare Sciences (II)
                                            </option>

                                            <option value="School of Professional Studies">
                                                School of Professional Studies
                                            </option>

                                            <option value="School of Nursing">
                                                School of Nursing
                                            </option>

                                            <option value="School of Occupational and Physiotherapy">
                                                School of Occupational and Physiotherapy
                                            </option>

                                            <option value="School of Pharmacy">
                                                School of Pharmacy
                                            </option>

                                            <option value="School of Hotel Management">
                                                School of Hotel Management
                                            </option>

                                            <option value="School of Fashion Design">
                                                School of Fashion Design
                                            </option>

                                            <option value="School of Natural Sciences">
                                                School of Natural Sciences
                                            </option>

                                            <option value="School of Management">
                                                School of Management
                                            </option>

                                            <option value="School of Humanities and Social Sciences">
                                                School of Humanities and Social Sciences
                                            </option>

                                            <option value="School of Agriculture">
                                                School of Agriculture
                                            </option>
                                        </select>
                                    </div>
                                </div>
                            )}

                            {/* Student Roll Number */}

                            {role === "student" && (
                                <>
                                    <div className="register-form-group">
                                        <label htmlFor="rollNumber">
                                            Roll Number
                                        </label>

                                        <div className="register-input-wrapper">
                                            <span className="register-input-icon">
                                                #
                                            </span>

                                            <input
                                                id="rollNumber"
                                                name="rollNumber"
                                                type="text"
                                                value={formData.rollNumber}
                                                onChange={handleChange}
                                                placeholder="Enter your roll number"
                                                disabled={isLoading}
                                                required
                                            />
                                        </div>
                                    </div>

                                    {/* Branch */}

                                    <div className="register-form-group">
                                        <label htmlFor="branch">
                                            Branch
                                        </label>

                                        <div className="register-input-wrapper">
                                            <span className="register-input-icon">
                                                🏫
                                            </span>

                                            <select
                                                id="branch"
                                                name="branch"
                                                value={formData.branch}
                                                onChange={handleChange}
                                                disabled={isLoading}
                                                required
                                            >
                                                <option value="">
                                                    Select Branch
                                                </option>

                                                <option value="Aeronautical Engineering">
                                                    Aeronautical Engineering
                                                </option>

                                                <option value="Aerospace Engineering">
                                                    Aerospace Engineering
                                                </option>

                                                <option value="Agricultural Engineering">
                                                    Agricultural Engineering
                                                </option>

                                                <option value="Automobile Engineering">
                                                    Automobile Engineering
                                                </option>

                                                <option value="Bachelor of Business Administration">
                                                    Bachelor of Business Administration (BBA)
                                                </option>

                                                <option value="Bachelor of Computer Applications">
                                                    Bachelor of Computer Applications (BCA)
                                                </option>

                                                <option value="Biomedical Engineering">
                                                    Biomedical Engineering
                                                </option>

                                                <option value="Biotechnology">
                                                    Biotechnology
                                                </option>

                                                <option value="Chemical Engineering">
                                                    Chemical Engineering
                                                </option>

                                                <option value="Civil Engineering">
                                                    Civil Engineering
                                                </option>

                                                <option value="Computer Science & Engineering">
                                                    Computer Science & Engineering (CSE)
                                                </option>

                                                <option value="Computer Science & Engineering - AI and ML">
                                                    Computer Science & Engineering (AI & ML)
                                                </option>

                                                <option value="Computer Science & Engineering - Data Science">
                                                    Computer Science & Engineering (Data Science)
                                                </option>

                                                <option value="Electrical & Electronics Engineering">
                                                    Electrical & Electronics Engineering
                                                </option>

                                                <option value="Electrical Engineering">
                                                    Electrical Engineering
                                                </option>

                                                <option value="Electronics & Communication Engineering">
                                                    Electronics & Communication Engineering
                                                </option>

                                                <option value="Food Technology">
                                                    Food Technology
                                                </option>

                                                <option value="Information Technology">
                                                    Information Technology
                                                </option>

                                                <option value="Master of Business Administration">
                                                    Master of Business Administration (MBA)
                                                </option>

                                                <option value="Master of Computer Applications">
                                                    Master of Computer Applications (MCA)
                                                </option>

                                                <option value="Mechanical Engineering">
                                                    Mechanical Engineering
                                                </option>

                                                <option value="Mining Engineering">
                                                    Mining Engineering
                                                </option>

                                                <option value="Petroleum Engineering">
                                                    Petroleum Engineering
                                                </option>

                                                <option value="Textile Engineering">
                                                    Textile Engineering
                                                </option>
                                            </select>
                                        </div>
                                    </div>

                                    {/* Semester */}

                                    <div className="register-form-group">
                                        <label htmlFor="semester">
                                            Semester
                                        </label>

                                        <div className="register-input-wrapper">
                                            <span className="register-input-icon">
                                                📚
                                            </span>

                                            <select
                                                id="semester"
                                                name="semester"
                                                value={formData.semester}
                                                onChange={handleChange}
                                                disabled={isLoading}
                                                required
                                            >
                                                <option value="">
                                                    Select Semester
                                                </option>

                                                <option value="1">
                                                    1st Semester
                                                </option>

                                                <option value="2">
                                                    2nd Semester
                                                </option>

                                                <option value="3">
                                                    3rd Semester
                                                </option>

                                                <option value="4">
                                                    4th Semester
                                                </option>

                                                <option value="5">
                                                    5th Semester
                                                </option>

                                                <option value="6">
                                                    6th Semester
                                                </option>

                                                <option value="7">
                                                    7th Semester
                                                </option>

                                                <option value="8">
                                                    8th Semester
                                                </option>
                                            </select>
                                        </div>
                                    </div>
                                </>
                            )}

                            {/* Password */}

                            <div className="register-form-group">
                                <label htmlFor="password">
                                    Password
                                </label>

                                <div className="register-input-wrapper">
                                    <span className="register-input-icon">
                                        🔒
                                    </span>

                                    <input
                                        id="password"
                                        name="password"
                                        type={
                                            showPassword
                                                ? "text"
                                                : "password"
                                        }
                                        value={formData.password}
                                        onChange={handleChange}
                                        placeholder="Minimum 6 characters"
                                        autoComplete="new-password"
                                        minLength={6}
                                        disabled={isLoading}
                                        required
                                    />

                                    <button
                                        type="button"
                                        className="register-show-password-button"
                                        onClick={() =>
                                            setShowPassword(
                                                (currentValue) =>
                                                    !currentValue
                                            )
                                        }
                                        aria-label={
                                            showPassword
                                                ? "Hide password"
                                                : "Show password"
                                        }
                                    >
                                        {showPassword
                                            ? "Hide"
                                            : "Show"}
                                    </button>
                                </div>
                            </div>

                            {/* Confirm Password */}

                            <div className="register-form-group">
                                <label htmlFor="confirmPassword">
                                    Confirm Password
                                </label>

                                <div className="register-input-wrapper">
                                    <span className="register-input-icon">
                                        🔐
                                    </span>

                                    <input
                                        id="confirmPassword"
                                        name="confirmPassword"
                                        type={
                                            showConfirmPassword
                                                ? "text"
                                                : "password"
                                        }
                                        value={formData.confirmPassword}
                                        onChange={handleChange}
                                        placeholder="Enter password again"
                                        autoComplete="new-password"
                                        minLength={6}
                                        disabled={isLoading}
                                        required
                                    />

                                    <button
                                        type="button"
                                        className="register-show-password-button"
                                        onClick={() =>
                                            setShowConfirmPassword(
                                                (currentValue) =>
                                                    !currentValue
                                            )
                                        }
                                        aria-label={
                                            showConfirmPassword
                                                ? "Hide confirm password"
                                                : "Show confirm password"
                                        }
                                    >
                                        {showConfirmPassword
                                            ? "Hide"
                                            : "Show"}
                                    </button>
                                </div>
                            </div>
                        </div>

                        <label className="register-terms-option">
                            <input
                                type="checkbox"
                                disabled={isLoading}
                                required
                            />

                            <span>
                                I confirm that the entered
                                information is correct.
                            </span>
                        </label>

                        <button
                            type="submit"
                            className="register-submit-button"
                            disabled={isLoading}
                        >
                            {isLoading
                                ? "Creating Account..."
                                : `Register as ${role === "teacher"
                                    ? "Teacher"
                                    : "Student"
                                }`}
                        </button>
                    </form>

                    <div className="register-divider">
                        <span>
                            Already have an account?
                        </span>
                    </div>

                    <button
                        type="button"
                        className="register-login-button"
                        onClick={() => navigate("/login")}
                        disabled={isLoading}
                    >
                        Login to Existing Account
                    </button>

                    <p className="register-security-note">
                        🔐 Your registration information will
                        be stored securely.
                    </p>
                </section>
            </main>
        </div>
    );
}

export default Register;
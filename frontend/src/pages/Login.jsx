import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Login.css";

function Login() {
    const navigate = useNavigate();

    const [role, setRole] = useState("teacher");
    const [showPassword, setShowPassword] = useState(false);

    const [formData, setFormData] = useState({
        email: "",
        password: "",
        rememberMe: false,
    });

    const handleChange = (event) => {
        const { name, value, type, checked } = event.target;

        setFormData((previousData) => ({
            ...previousData,
            [name]: type === "checkbox" ? checked : value,
        }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        console.log({
            role,
            ...formData,
        });

        alert(`Login submitted as ${role}`);

        // Backend connect hone ke baad yahan API call hogi.
    };

    return (
        <div className="login-page">
            <div className="login-background-circle circle-one"></div>
            <div className="login-background-circle circle-two"></div>

            <header className="login-navbar">
                <div
                    className="login-logo"
                    onClick={() => navigate("/")}
                    role="button"
                    tabIndex={0}
                    onKeyDown={(event) => {
                        if (event.key === "Enter") {
                            navigate("/");
                        }
                    }}
                >
                    <div className="login-logo-icon">SA</div>

                    <div>
                        <h2>Smart Attendance</h2>
                        <p>Location Based Attendance System</p>
                    </div>
                </div>

                <button
                    type="button"
                    className="back-home-button"
                    onClick={() => navigate("/")}
                >
                    ← Back to Home
                </button>
            </header>

            <main className="login-main">
                <section className="login-information">
                    <span className="login-tag">SECURE LOGIN</span>

                    <h1>Welcome back to Smart Attendance</h1>

                    <p>
                        Access your attendance dashboard securely as a teacher or student.
                    </p>

                </section>

                <section className="login-card">
                    <div className="login-card-heading">
                        <span>ACCOUNT ACCESS</span>
                        <h2>Login to your account</h2>
                        <p>Select your role and enter your login details.</p>
                    </div>

                    <div className="role-selector">
                        <button
                            type="button"
                            className={`role-option ${role === "teacher" ? "active teacher-active" : ""
                                }`}
                            onClick={() => setRole("teacher")}
                        >
                            <span className="role-option-icon">👨‍🏫</span>

                            <span>
                                <strong>Teacher</strong>
                                <small>Manage classes</small>
                            </span>
                        </button>

                        <button
                            type="button"
                            className={`role-option ${role === "student" ? "active student-active" : ""
                                }`}
                            onClick={() => setRole("student")}
                        >
                            <span className="role-option-icon">🎓</span>

                            <span>
                                <strong>Student</strong>
                                <small>Mark attendance</small>
                            </span>
                        </button>
                    </div>

                    <form className="login-form" onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="email">
                                {role === "student" ? "Gmail ID" : "Email ID"}
                            </label>

                            <div className="input-wrapper">
                                <span className="input-icon">✉</span>

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
                                    required
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <div className="password-label-row">
                                <label htmlFor="password">Password</label>

                                <button
                                    type="button"
                                    className="forgot-password-button"
                                    onClick={() => alert("Forgot password page will be added later.")}
                                >
                                    Forgot Password?
                                </button>
                            </div>

                            <div className="input-wrapper">
                                <span className="input-icon">🔒</span>

                                <input
                                    id="password"
                                    name="password"
                                    type={showPassword ? "text" : "password"}
                                    value={formData.password}
                                    onChange={handleChange}
                                    placeholder="Enter your password"
                                    autoComplete="current-password"
                                    minLength={6}
                                    required
                                />

                                <button
                                    type="button"
                                    className="show-password-button"
                                    onClick={() => setShowPassword((currentValue) => !currentValue)}
                                    aria-label={
                                        showPassword ? "Hide password" : "Show password"
                                    }
                                >
                                    {showPassword ? "Hide" : "Show"}
                                </button>
                            </div>
                        </div>

                        <label className="remember-option">
                            <input
                                name="rememberMe"
                                type="checkbox"
                                checked={formData.rememberMe}
                                onChange={handleChange}
                            />

                            <span>Remember me on this device</span>
                        </label>

                        <button type="submit" className="login-submit-button">
                            Login as {role === "teacher" ? "Teacher" : "Student"}
                        </button>
                    </form>

                    <div className="login-divider">
                        <span>New to Smart Attendance?</span>
                    </div>

                    <button
                        type="button"
                        className="create-account-button"
                        onClick={() => navigate("/register")}
                    >
                        Create New Account
                    </button>

                    <p className="login-security-note">
                        🔐 Your login information is protected and encrypted.
                    </p>
                </section>
            </main>
        </div>
    );
}

export default Login;
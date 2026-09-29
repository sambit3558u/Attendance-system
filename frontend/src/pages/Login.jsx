import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Login.css";

function Login() {
    const navigate = useNavigate();

    const [showPassword, setShowPassword] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

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

    /**
     * Student ke browser ke liye ek UUID create karta hai.
     * Same browser me next login par existing UUID return hoga.
     */
    const getOrCreateDeviceId = () => {
        let deviceId = localStorage.getItem("deviceId");

        if (!deviceId) {
            deviceId = crypto.randomUUID();
            localStorage.setItem("deviceId", deviceId);
        }

        return deviceId;
    };

    /**
     * Login form ko Spring Boot backend par send karta hai.
     */
    const handleSubmit = async (event) => {
        event.preventDefault();

        if (isLoading) {
            return;
        }

        setIsLoading(true);

        try {
            const loginData = {
                email: formData.email.trim(),
                password: formData.password,
                // Backend email se account role identify karega.
                deviceId: getOrCreateDeviceId(),
            };

            console.log("Login request:", loginData);

            const response = await fetch(
                "http://localhost:8080/api/auth/login",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(loginData),
                }
            );

            const data = await response.json();

            if (!response.ok) {
                throw new Error(
                    data.message ||
                    "Login failed. Please try again."
                );
            }

            /*
             * JWT token save karo.
             * Abhi development ke liye localStorage use kar rahe hain.
             */
            localStorage.setItem(
                "accessToken",
                data.accessToken
            );

            /*
             * Logged-in user information save karo.
             */
            localStorage.setItem(
                "user",
                JSON.stringify({
                    userId: data.userId,
                    name: data.name,
                    email: data.email,
                    role: data.role,
                })
            );

            /*
             * Token expiry aur login time save karo.
             */
            localStorage.setItem(
                "tokenExpiresIn",
                String(data.expiresIn)
            );

            localStorage.setItem(
                "loginTime",
                new Date().toISOString()
            );

            localStorage.setItem(
                "rememberMe",
                String(formData.rememberMe)
            );

            alert(data.message || "Login successful");

            if (data.role === "TEACHER") {
                navigate("/teacher-dashboard");
            } else if (data.role === "STUDENT") {
                navigate("/student-dashboard");
            } else if (data.role === "ADMIN") {
                navigate("/admin-dashboard");
            } else {
                throw new Error(
                    "Invalid user role received from backend."
                );
            }

        } catch (error) {
            console.error("Login error:", error);

            if (error instanceof TypeError) {
                alert(
                    "Backend se connection nahi ho pa raha. Check karo Spring Boot port 8080 par run ho raha hai."
                );
            } else {
                alert(error.message);
            }

        } finally {
            setIsLoading(false);
        }
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
                        if (
                            event.key === "Enter" ||
                            event.key === " "
                        ) {
                            navigate("/");
                        }
                    }}
                >
                    <div className="login-logo-icon">
                        SA
                    </div>

                    <div>
                        <h2>Smart Attendance</h2>

                        <p>
                            Location Based Attendance System
                        </p>
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
                    <span className="login-tag">
                        SECURE LOGIN
                    </span>

                    <h1>
                        Welcome back to Smart Attendance
                    </h1>

                    <p>
                        Access your attendance dashboard
                        securely as a teacher or student.
                    </p>
                </section>

                <section className="login-card">
                    <div className="login-card-heading">
                        <span>ACCOUNT ACCESS</span>

                        <h2>Login to your account</h2>

                        <p>
                            Enter your email and password to continue.
                        </p>
                    </div>

                    <form
                        className="login-form"
                        onSubmit={handleSubmit}
                    >
                        <div className="form-group">
                            <label htmlFor="email">
                                Email ID
                            </label>

                            <div className="input-wrapper">
                                <span className="input-icon">
                                    ✉
                                </span>

                                <input
                                    id="email"
                                    name="email"
                                    type="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    placeholder="Enter your email ID"
                                    autoComplete="email"
                                    disabled={isLoading}
                                    required
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <div className="password-label-row">
                                <label htmlFor="password">
                                    Password
                                </label>

                                <button
                                    type="button"
                                    className="forgot-password-button"
                                    onClick={() => navigate("/forgot-password")}
                                    disabled={isLoading}
                                >
                                    Forgot Password?
                                </button>
                            </div>

                            <div className="input-wrapper">
                                <span className="input-icon">
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
                                    placeholder="Enter your password"
                                    autoComplete="current-password"
                                    minLength={6}
                                    disabled={isLoading}
                                    required
                                />

                                <button
                                    type="button"
                                    className="show-password-button"
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
                                    disabled={isLoading}
                                >
                                    {showPassword
                                        ? "Hide"
                                        : "Show"}
                                </button>
                            </div>
                        </div>

                        <label className="remember-option">
                            <input
                                name="rememberMe"
                                type="checkbox"
                                checked={formData.rememberMe}
                                onChange={handleChange}
                                disabled={isLoading}
                            />

                            <span>
                                Remember me on this device
                            </span>
                        </label>

                        <button
                            type="submit"
                            className="login-submit-button"
                            disabled={isLoading}
                        >
                            {isLoading
                                ? "Logging in..."
                                : "Login"}
                        </button>
                    </form>

                    <div className="login-divider">
                        <span>
                            New to Smart Attendance?
                        </span>
                    </div>

                    <button
                        type="button"
                        className="create-account-button"
                        onClick={() => navigate("/register")}
                        disabled={isLoading}
                    >
                        Create New Account
                    </button>

                    <p className="login-security-note">
                        🔐 Your login information is protected
                        and encrypted.
                    </p>
                </section>
            </main>

        </div>

    );
}

export default Login;

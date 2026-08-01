import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Register.css";

function Register() {
    const navigate = useNavigate();

    const [role, setRole] = useState("teacher");
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        phone: "",
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
            rollNumber: "",
            branch: "",
            semester: "",
        }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        if (formData.password !== formData.confirmPassword) {
            alert("Password and Confirm Password do not match.");
            return;
        }

        const registrationData = {
            role,
            name: formData.name.trim(),
            email: formData.email.trim(),
            phone: formData.phone.trim(),
            password: formData.password,
            ...(role === "student" && {
                rollNumber: formData.rollNumber.trim(),
                branch: formData.branch,
                semester: formData.semester,
            }),
        };

        console.log(registrationData);

        alert(
            `${role === "teacher" ? "Teacher" : "Student"
            } registration submitted successfully.`
        );

        // Backend connect hone ke baad yahan registration API call hogi.
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
                        if (event.key === "Enter") {
                            navigate("/");
                        }
                    }}
                >
                    <div className="register-logo-icon">SA</div>

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
                    <span className="register-tag">CREATE ACCOUNT</span>

                    <h1>Join Smart Attendance</h1>

                    <p>
                        Create your teacher or student account to continue.
                    </p>
                </section>

                <section className="register-card">
                    <div className="register-card-heading">
                        <span>NEW ACCOUNT</span>
                        <h2>Create your account</h2>
                        <p>Select your role and complete the registration form.</p>
                    </div>

                    <div className="register-role-selector">
                        <button
                            type="button"
                            className={`register-role-option ${role === "teacher"
                                ? "active register-teacher-active"
                                : ""
                                }`}
                            onClick={() => handleRoleChange("teacher")}
                        >
                            <span className="register-role-icon">👨‍🏫</span>

                            <span>
                                <strong>Teacher</strong>
                                <small>Create and manage classes</small>
                            </span>
                        </button>

                        <button
                            type="button"
                            className={`register-role-option ${role === "student"
                                ? "active register-student-active"
                                : ""
                                }`}
                            onClick={() => handleRoleChange("student")}
                        >
                            <span className="register-role-icon">🎓</span>

                            <span>
                                <strong>Student</strong>
                                <small>Join classes and mark attendance</small>
                            </span>
                        </button>
                    </div>

                    <form className="register-form" onSubmit={handleSubmit}>
                        <div className="register-form-grid">
                            <div className="register-form-group ">
                                <label htmlFor="name">Full Name</label>

                                <div className="register-input-wrapper">
                                    <span className="register-input-icon">👤</span>

                                    <input
                                        id="name"
                                        name="name"
                                        type="text"
                                        value={formData.name}
                                        onChange={handleChange}
                                        placeholder="Enter your full name"
                                        autoComplete="name"
                                        minLength={2}
                                        required
                                    />
                                </div>
                            </div>

                            <div className="register-form-group " >
                                <label htmlFor="email">
                                    {role === "student" ? "Gmail ID" : "Email ID"}
                                </label>

                                <div className="register-input-wrapper ">
                                    <span className="register-input-icon">✉</span>

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

                            <div className="register-form-group">
                                <label htmlFor="phone">Phone Number</label>

                                <div className="register-input-wrapper">
                                    <span className="register-input-icon">📞</span>

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
                                        required
                                    />
                                </div>
                            </div>

                            {role === "student" && (
                                <>
                                    <div className="register-form-group">
                                        <label htmlFor="rollNumber">Roll Number</label>

                                        <div className="register-input-wrapper">
                                            <span className="register-input-icon">#</span>

                                            <input
                                                id="rollNumber"
                                                name="rollNumber"
                                                type="text"
                                                value={formData.rollNumber}
                                                onChange={handleChange}
                                                placeholder="Enter your roll number"
                                                required
                                            />
                                        </div>
                                    </div>

                                    <div className="register-form-group">
                                        <label htmlFor="branch">Branch</label>

                                        <div className="register-input-wrapper">
                                            <span className="register-input-icon">🏫</span>

                                            <select
                                                id="branch"
                                                name="branch"
                                                value={formData.branch}
                                                onChange={handleChange}
                                                required
                                            >
                                                <option value="">Select Branch</option>

                                                <option value="AE">Automobile Engineering</option>
                                                <option value="AERO">Aeronautical Engineering</option>
                                                <option value="AEROSPACE">Aerospace Engineering</option>
                                                <option value="AGRI">Agricultural Engineering</option>
                                                <option value="BBA">Bachelor of Business Administration (BBA)</option>
                                                <option value="BCA">Bachelor of Computer Applications (BCA)</option>
                                                <option value="BME">Biomedical Engineering</option>
                                                <option value="BT">Biotechnology</option>
                                                <option value="CHE">Chemical Engineering</option>
                                                <option value="CE">Civil Engineering (CE)</option>
                                                <option value="CSE">Computer Science & Engineering (CSE)</option>
                                                <option value="CSE-AIML">Computer Science & Engineering (AI & ML)</option>
                                                <option value="CSE-DS">Computer Science & Engineering (Data Science)</option>
                                                <option value="ECE">Electronics & Communication Engineering (ECE)</option>
                                                <option value="EEE">Electrical & Electronics Engineering (EEE)</option>
                                                <option value="EE">Electrical Engineering (EE)</option>
                                                <option value="FOOD">Food Technology</option>
                                                <option value="IT">Information Technology (IT)</option>
                                                <option value="MBA">Master of Business Administration (MBA)</option>
                                                <option value="MCA">Master of Computer Applications (MCA)</option>
                                                <option value="ME">Mechanical Engineering (ME)</option>
                                                <option value="MINING">Mining Engineering</option>
                                                <option value="PETROLEUM">Petroleum Engineering</option>
                                                <option value="TEXTILE">Textile Engineering</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div className="register-form-group">
                                        <label htmlFor="semester">Semester</label>

                                        <div className="register-input-wrapper">
                                            <span className="register-input-icon">📚</span>

                                            <select
                                                id="semester"
                                                name="semester"
                                                value={formData.semester}
                                                onChange={handleChange}
                                                required
                                            >
                                                <option value="" disabled>
                                                    Select your semester
                                                </option>

                                                <option value="1">1st Semester</option>
                                                <option value="2">2nd Semester</option>
                                                <option value="3">3rd Semester</option>
                                                <option value="4">4th Semester</option>
                                                <option value="5">5th Semester</option>
                                                <option value="6">6th Semester</option>
                                                <option value="7">7th Semester</option>
                                                <option value="8">8th Semester</option>
                                            </select>
                                        </div>
                                    </div>
                                </>
                            )}
                            {role === "teacher" && (
                                <>

                                    <div className="register-form-group">
                                        <label htmlFor="department">Department</label>

                                        <div className="register-input-wrapper">
                                            <span className="register-input-icon">🏫</span>

                                            <select
                                                id="department"
                                                name="department"
                                                value={formData.department}
                                                onChange={handleChange}
                                                required
                                            >
                                                <option value="">Select Department</option>

                                                <option value="DRIEMS Institute of Health Sciences and Hospital">
                                                    DRIEMS Institute of Health Sciences and Hospital
                                                </option>

                                                <option value="School of Engineering and Technology">
                                                    School of Engineering and Technology (SOET)
                                                </option>

                                                <option value="School of Paramedical">
                                                    School of Paramedical (SOPM)
                                                </option>

                                                <option value="School of Allied and Healthcare Sciences (I)">
                                                    School of Allied and Healthcare Sciences (I)
                                                </option>


                                                <option value="School of Professional Studies">
                                                    School of Professional Studies (SOPS)
                                                </option>

                                                <option value="School of Nursing">
                                                    School of Nursing (SON)
                                                </option>

                                                <option value="School of Occupational and Physiotherapy">
                                                    School of Occupational and Physiotherapy (SOAP)
                                                </option>

                                                <option value="School of Pharmacy">
                                                    School of Pharmacy (SOP)
                                                </option>

                                                <option value="School of Hotel Management">
                                                    School of Hotel Management (SOHM)
                                                </option>

                                                <option value="School of Fashion Design">
                                                    School of Fashion Design (SOFD)
                                                </option>

                                                <option value="School of Natural Sciences">
                                                    School of Natural Sciences (SONS)
                                                </option>

                                                <option value="School of Management">
                                                    School of Management (SOM)
                                                </option>

                                                <option value="School of Humanities and Social Sciences">
                                                    School of Humanities and Social Sciences (SOHSS)
                                                </option>

                                                <option value="School of Agriculture">
                                                    School of Agriculture (SOA)
                                                </option>
                                            </select>
                                        </div>
                                    </div>
                                </>
                            )}

                            <div className="register-form-group">
                                <label htmlFor="password">Password</label>

                                <div className="register-input-wrapper">
                                    <span className="register-input-icon">🔒</span>

                                    <input
                                        id="password"
                                        name="password"
                                        type={showPassword ? "text" : "password"}
                                        value={formData.password}
                                        onChange={handleChange}
                                        placeholder="Minimum 6 characters"
                                        autoComplete="new-password"
                                        minLength={6}
                                        required
                                    />

                                    <button
                                        type="button"
                                        className="register-show-password-button"
                                        onClick={() =>
                                            setShowPassword((currentValue) => !currentValue)
                                        }
                                        aria-label={
                                            showPassword ? "Hide password" : "Show password"
                                        }
                                    >
                                        {showPassword ? "Hide" : "Show"}
                                    </button>
                                </div>
                            </div>

                            <div className="register-form-group">
                                <label htmlFor="confirmPassword">
                                    Confirm Password
                                </label>

                                <div className="register-input-wrapper">
                                    <span className="register-input-icon">🔐</span>

                                    <input
                                        id="confirmPassword"
                                        name="confirmPassword"
                                        type={showConfirmPassword ? "text" : "password"}
                                        value={formData.confirmPassword}
                                        onChange={handleChange}
                                        placeholder="Enter password again"
                                        autoComplete="new-password"
                                        minLength={6}
                                        required
                                    />

                                    <button
                                        type="button"
                                        className="register-show-password-button"
                                        onClick={() =>
                                            setShowConfirmPassword(
                                                (currentValue) => !currentValue
                                            )
                                        }
                                        aria-label={
                                            showConfirmPassword
                                                ? "Hide confirm password"
                                                : "Show confirm password"
                                        }
                                    >
                                        {showConfirmPassword ? "Hide" : "Show"}
                                    </button>
                                </div>
                            </div>
                        </div>

                        <label className="register-terms-option">
                            <input type="checkbox" required />
                            <span>
                                I confirm that the entered information is correct.
                            </span>
                        </label>

                        <button type="submit" className="register-submit-button">
                            Register as{" "}
                            {role === "teacher" ? "Teacher" : "Student"}
                        </button>
                    </form>

                    <div className="register-divider">
                        <span>Already have an account?</span>
                    </div>

                    <button
                        type="button"
                        className="register-login-button"
                        onClick={() => navigate("/login")}
                    >
                        Login to Existing Account
                    </button>

                    <p className="register-security-note">
                        🔐 Your registration information will be stored securely.
                    </p>
                </section>
            </main>
        </div>
    );
}

export default Register;
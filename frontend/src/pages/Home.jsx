import "./Home.css";
import React from "react";
import { useNavigate } from "react-router-dom";

function Home() {
    const navigate = useNavigate();

    return (
        <div className="website">
            <header className="navbar">
                <div className="logo">
                    <div className="logo-icon">SA</div>

                    <div>
                        <h2>Smart Attendance</h2>
                        <p>Location Based Attendance System</p>
                    </div>
                </div>

                <nav>
                    <a href="#home">Home</a>
                    <a href="#features">Features</a>
                    <a href="#how-it-works">How It Works</a>
                    <a href="#security">Security</a>
                    <a href="#about">About</a>
                </nav>

                <div className="nav-buttons">
                    <button
                        className="login-button"
                        onClick={() => navigate("/login")}
                    >
                        Login
                    </button>
                    <button
                        className="register-button"
                        onClick={() => navigate("/register")}
                    >
                        Register
                    </button>
                </div>
            </header>

            <main>
                <section className="hero" id="home">
                    <div className="hero-content">
                        <span className="tag">Smart • Secure • Location Verified</span>

                        <h1>
                            A Modern Attendance System for Teachers and Students
                        </h1>

                        <p>
                            Teachers can start classes, manage subjects and track attendance
                            in real time. Students can mark their attendance using their
                            registered phone with location verification.
                        </p>

                        <div className="hero-buttons">
                            <button className="primary-button">Get Started</button>
                            <button className="secondary-button">View Features</button>
                        </div>

                        <div className="hero-highlights">
                            <div>
                                <strong>100%</strong>
                                <span>Digital Attendance</span>
                            </div>

                            <div>
                                <strong>Real-Time</strong>
                                <span>Attendance Tracking</span>
                            </div>

                            <div>
                                <strong>Secure</strong>
                                <span>One Device Login</span>
                            </div>
                        </div>
                    </div>

                    <div className="attendance-preview">
                        <div className="preview-top">
                            <div>
                                <p>Currently Active Class</p>
                                <h2>Data Structures</h2>
                            </div>

                            <span className="live-badge">
                                <span className="live-dot"></span>
                                Live
                            </span>
                        </div>

                        <div className="class-details">
                            <div className="info-box">
                                <span>Teacher</span>
                                <strong>Mr. Sharma</strong>
                            </div>

                            <div className="info-box">
                                <span>Branch</span>
                                <strong>CSE</strong>
                            </div>

                            <div className="info-box">
                                <span>Date</span>
                                <strong>31 July 2026</strong>
                            </div>

                            <div className="info-box">
                                <span>Time</span>
                                <strong>10:00 AM – 11:00 AM</strong>
                            </div>
                        </div>

                        <div className="attendance-summary">
                            <div className="summary-box">
                                <span>Total Students</span>
                                <strong>50</strong>
                            </div>

                            <div className="summary-box present-box">
                                <span>Present</span>
                                <strong>42</strong>
                            </div>

                            <div className="summary-box absent-box">
                                <span>Absent</span>
                                <strong>8</strong>
                            </div>
                        </div>

                        <div className="location-status">
                            <span>📍</span>

                            <div>
                                <strong>Location Verification Enabled</strong>
                                <p>Allowed attendance radius: 100 metres</p>
                            </div>
                        </div>
                    </div>
                </section>

                <section className="statistics">
                    <div className="stat-item">
                        <strong>500+</strong>
                        <span>Registered Students</span>
                    </div>

                    <div className="stat-item">
                        <strong>25+</strong>
                        <span>Registered Teachers</span>
                    </div>

                    <div className="stat-item">
                        <strong>1,000+</strong>
                        <span>Classes Completed</span>
                    </div>

                    <div className="stat-item">
                        <strong>98%</strong>
                        <span>Attendance Accuracy</span>
                    </div>
                </section>

                <section className="features section" id="features">
                    <div className="section-heading">
                        <span>CORE FEATURES</span>
                        <h2>Everything required for smart attendance</h2>
                        <p>
                            A complete attendance solution designed for teachers, students
                            and educational institutions.
                        </p>
                    </div>

                    <div className="feature-grid">
                        <article className="feature-card">
                            <div className="feature-icon">👨‍🏫</div>
                            <h3>Teacher Dashboard</h3>
                            <p>
                                Teachers can start and close classes, select subjects and view
                                attendance in real time.
                            </p>
                        </article>

                        <article className="feature-card">
                            <div className="feature-icon">📍</div>
                            <h3>Location Verification</h3>
                            <p>
                                Student location is checked before attendance is accepted by
                                the system.
                            </p>
                        </article>

                        <article className="feature-card">
                            <div className="feature-icon">📱</div>
                            <h3>One Device Login</h3>
                            <p>
                                One student account can remain connected with only one
                                registered mobile device.
                            </p>
                        </article>

                        <article className="feature-card">
                            <div className="feature-icon">⏱️</div>
                            <h3>Attendance Time Limit</h3>
                            <p>
                                Students can mark attendance only while the class attendance
                                session is active.
                            </p>
                        </article>

                        <article className="feature-card">
                            <div className="feature-icon">📊</div>
                            <h3>Attendance Reports</h3>
                            <p>
                                Teachers can view attendance history and generate subject-wise
                                reports.
                            </p>
                        </article>

                        <article className="feature-card">
                            <div className="feature-icon">🔐</div>
                            <h3>Secure Authentication</h3>
                            <p>
                                Role-based login protects teacher and student dashboards from
                                unauthorized access.
                            </p>
                        </article>
                    </div>
                </section>

                <section className="how-it-works section" id="how-it-works">
                    <div className="section-heading">
                        <span>HOW IT WORKS</span>
                        <h2>Attendance in four simple steps</h2>
                        <p>
                            A simple process that saves classroom time and prevents false
                            attendance.
                        </p>
                    </div>

                    <div className="steps">
                        <article className="step-card">
                            <div className="step-number">01</div>
                            <h3>Teacher starts class</h3>
                            <p>
                                Teacher selects the subject, class, date, time and attendance
                                radius.
                            </p>
                        </article>

                        <article className="step-card">
                            <div className="step-number">02</div>
                            <h3>Student views active class</h3>
                            <p>
                                The active class automatically appears on the student
                                dashboard.
                            </p>
                        </article>

                        <article className="step-card">
                            <div className="step-number">03</div>
                            <h3>Location is verified</h3>
                            <p>
                                The system checks the student location and registered device.
                            </p>
                        </article>

                        <article className="step-card">
                            <div className="step-number">04</div>
                            <h3>Attendance is recorded</h3>
                            <p>
                                Attendance is stored with date, time, location and subject
                                details.
                            </p>
                        </article>
                    </div>
                </section>

                <section className="role-section section">
                    <div className="role-card teacher-card">
                        <span className="role-label">FOR TEACHERS</span>
                        <h2>Manage classroom attendance easily</h2>

                        <ul>
                            <li>Start and close attendance sessions</li>
                            <li>Select class, branch and subject</li>
                            <li>Set attendance time and allowed radius</li>
                            <li>View present and absent students</li>
                            <li>Download attendance reports</li>
                        </ul>

                        <button
                            className="primary-button"
                            onClick={() => navigate("/register")}
                        >
                            Teacher Registration
                        </button>
                    </div>

                    <div className="role-card student-card">
                        <span className="role-label">FOR STUDENTS</span>
                        <h2>Mark attendance securely from your phone</h2>

                        <ul>
                            <li>View currently active classes</li>
                            <li>Mark attendance using phone location</li>
                            <li>Use one account on one registered phone</li>
                            <li>View subject-wise attendance history</li>
                            <li>Check attendance percentage</li>
                        </ul>

                        <button
                            className="primary-button"
                            onClick={() => navigate("/register")}
                        >
                            Student Registration
                        </button>
                    </div>
                </section>

                <section className="security section" id="security">
                    <div className="security-content">
                        <span className="section-label">SECURITY</span>
                        <h2>Built to prevent false attendance</h2>

                        <p>
                            Multiple verification methods help ensure that attendance is
                            marked only by the correct student present inside the classroom.
                        </p>

                        <div className="security-list">
                            <div>
                                <span>✓</span>
                                <p>Location radius verification</p>
                            </div>

                            <div>
                                <span>✓</span>
                                <p>One device per student account</p>
                            </div>

                            <div>
                                <span>✓</span>
                                <p>One attendance entry per class</p>
                            </div>

                            <div>
                                <span>✓</span>
                                <p>Attendance session time validation</p>
                            </div>

                            <div>
                                <span>✓</span>
                                <p>Password encryption and secure login</p>
                            </div>

                            <div>
                                <span>✓</span>
                                <p>Teacher-controlled active sessions</p>
                            </div>
                        </div>
                    </div>

                    <div className="security-card">
                        <div className="shield-icon">🛡️</div>
                        <h3>Attendance Verification</h3>

                        <div className="verification-row">
                            <span>Registered device</span>
                            <strong>Verified</strong>
                        </div>

                        <div className="verification-row">
                            <span>Student location</span>
                            <strong>Inside radius</strong>
                        </div>

                        <div className="verification-row">
                            <span>Class session</span>
                            <strong>Active</strong>
                        </div>

                        <div className="verification-row">
                            <span>Attendance status</span>
                            <strong className="success-text">Present</strong>
                        </div>
                    </div>
                </section>

                <section className="benefits section">
                    <div className="section-heading">
                        <span>WHY CHOOSE US</span>
                        <h2>Better than traditional attendance</h2>
                    </div>

                    <div className="benefit-grid">
                        <div className="benefit-card">
                            <h3>Save classroom time</h3>
                            <p>
                                Teachers do not need to call each student roll number manually.
                            </p>
                        </div>

                        <div className="benefit-card">
                            <h3>Reduce proxy attendance</h3>
                            <p>
                                Location and device verification reduce false attendance
                                attempts.
                            </p>
                        </div>

                        <div className="benefit-card">
                            <h3>Instant reports</h3>
                            <p>
                                Attendance records are stored automatically and available
                                anytime.
                            </p>
                        </div>

                        <div className="benefit-card">
                            <h3>Mobile friendly</h3>
                            <p>
                                Students can access and mark attendance directly from their
                                phones.
                            </p>
                        </div>
                    </div>
                </section>

                <section className="cta-section">
                    <div>
                        <span>START USING SMART ATTENDANCE</span>
                        <h2>Make classroom attendance simple and secure</h2>
                        <p>
                            Create your teacher or student account and experience digital
                            attendance management.
                        </p>
                    </div>

                    <div className="cta-buttons">
                        <button className="white-button" onClick={() => navigate("/register")}>
                            Create Account
                        </button>
                        <button className="outline-white-button" onClick={() => navigate("/login")}>
                            Login
                        </button>
                    </div>
                </section>
            </main>

            <footer id="about">
                <div className="footer-grid">
                    <div>
                        <h2>Smart Attendance</h2>
                        <p>
                            A location-based attendance management system for teachers and
                            students.
                        </p>
                    </div>

                    <div>
                        <h3>Quick Links</h3>
                        <a href="#home">Home</a>
                        <a href="#features">Features</a>
                        <a href="#how-it-works">How It Works</a>
                    </div>

                    <div>
                        <h3>Account</h3>
                        <button onClick={() => navigate("/login")}>Teacher Login</button>
                        <button onClick={() => navigate("/login")}>Student Login</button>
                        <button
                            onClick={() => navigate("/register")}
                        >
                            Register
                        </button>
                    </div>

                    <div>
                        <h3>Contact</h3>
                        <p>Email: support@smartattendance.com</p>
                        <p>Phone: +91 98765 43210</p>
                        <p>Odisha, India</p>
                    </div>
                </div>

                <div className="footer-bottom">
                    <p>© 2026 Smart Attendance System. All rights reserved.</p>
                    <p>Developed by Sambit</p>
                </div>
            </footer>
        </div>
    );
}

export default Home;
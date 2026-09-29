import StudentMobileMenu from "./StudentMobileMenu";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./StudentSettings.css";

function StudentSettings() {
    const navigate = useNavigate();

    // =====================================================
    // Logged-in User
    // =====================================================

    const storedUser =
        JSON.parse(localStorage.getItem("user")) || null;

    // =====================================================
    // State
    // =====================================================

    const [settingsData, setSettingsData] = useState(null);

    const [formData, setFormData] = useState({
        notificationsEnabled: true,
        classReminderEnabled: true,
        attendanceAlertEnabled: true,
        timetableAlertEnabled: true,
        theme: "DARK",
    });

    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);

    const [errorMessage, setErrorMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    // =====================================================
    // Load Settings From Backend
    // =====================================================

    useEffect(() => {
        const loadSettings = async () => {
            try {
                if (!storedUser?.email) {
                    throw new Error(
                        "Logged-in student information not found."
                    );
                }

                const response = await fetch(
                    `http://localhost:8080/api/student/settings?email=${encodeURIComponent(
                        storedUser.email
                    )}`,
                    {
                        method: "GET",
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
                        },
                    }
                );

                const data = await response.json();

                if (!response.ok) {
                    throw new Error(
                        data.message ||
                        "Failed to load settings."
                    );
                }

                setSettingsData(data);

                setFormData({
                    notificationsEnabled:
                        data.notificationsEnabled ?? true,

                    classReminderEnabled:
                        data.classReminderEnabled ?? true,

                    attendanceAlertEnabled:
                        data.attendanceAlertEnabled ?? true,

                    timetableAlertEnabled:
                        data.timetableAlertEnabled ?? true,

                    theme:
                        data.theme || "DARK",
                });

                setErrorMessage("");

            } catch (error) {
                console.error(
                    "Student Settings Error:",
                    error
                );

                setErrorMessage(
                    error.message ||
                    "Unable to load settings."
                );

            } finally {
                setIsLoading(false);
            }
        };

        loadSettings();

    }, [storedUser?.email]);

    // =====================================================
    // Navigation
    // =====================================================

    const goToDashboard = () => {
        navigate("/student-dashboard");
    };

    const goToAttendance = () => {
        navigate("/student/attendance");
    };

    const goToTimetable = () => {
        navigate("/student/timetable");
    };

    const goToProfile = () => {
        navigate("/student/profile");
    };

    const goToSettings = () => {
        navigate("/student/settings");
    };

    // =====================================================
    // Toggle Setting
    // =====================================================

    const handleToggle = (fieldName) => {
        setFormData((previous) => ({
            ...previous,
            [fieldName]: !previous[fieldName],
        }));

        setSuccessMessage("");
        setErrorMessage("");
    };

    // =====================================================
    // Theme Change
    // =====================================================

    const handleThemeChange = (event) => {
        setFormData((previous) => ({
            ...previous,
            theme: event.target.value,
        }));

        setSuccessMessage("");
        setErrorMessage("");
    };

    // =====================================================
    // Reset Changes
    // =====================================================

    const handleReset = () => {
        if (!settingsData) {
            return;
        }

        setFormData({
            notificationsEnabled:
                settingsData.notificationsEnabled ?? true,

            classReminderEnabled:
                settingsData.classReminderEnabled ?? true,

            attendanceAlertEnabled:
                settingsData.attendanceAlertEnabled ?? true,

            timetableAlertEnabled:
                settingsData.timetableAlertEnabled ?? true,

            theme:
                settingsData.theme || "DARK",
        });

        setSuccessMessage("");
        setErrorMessage("");
    };

    // =====================================================
    // Save Settings
    // =====================================================

    const handleSaveSettings = async () => {
        try {
            if (!storedUser?.email) {
                throw new Error(
                    "Logged-in student information not found."
                );
            }

            setIsSaving(true);
            setSuccessMessage("");
            setErrorMessage("");

            const response = await fetch(
                `http://localhost:8080/api/student/settings?email=${encodeURIComponent(
                    storedUser.email
                )}`,
                {
                    method: "PUT",

                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
                },

                    body: JSON.stringify({
                        notificationsEnabled:
                            formData.notificationsEnabled,

                        classReminderEnabled:
                            formData.classReminderEnabled,

                        attendanceAlertEnabled:
                            formData.attendanceAlertEnabled,

                        timetableAlertEnabled:
                            formData.timetableAlertEnabled,

                        theme:
                            formData.theme,
                    }),
                }
            );

            const data = await response.json();

            if (!response.ok) {
                throw new Error(
                    data.message ||
                    "Failed to update settings."
                );
            }

            setSettingsData(data);

            setFormData({
                notificationsEnabled:
                    data.notificationsEnabled,

                classReminderEnabled:
                    data.classReminderEnabled,

                attendanceAlertEnabled:
                    data.attendanceAlertEnabled,

                timetableAlertEnabled:
                    data.timetableAlertEnabled,

                theme:
                    data.theme,
            });

            /*
             * Theme ko localStorage me bhi save kar rahe hain.
             * Later global theme system ke saath connect kar sakte hain.
             */
            localStorage.setItem(
                "studentTheme",
                data.theme
            );

            setSuccessMessage(
                "Settings updated successfully."
            );

        } catch (error) {
            console.error(
                "Settings Update Error:",
                error
            );

            setErrorMessage(
                error.message ||
                "Unable to update settings."
            );

        } finally {
            setIsSaving(false);
        }
    };

    // =====================================================
    // Logout
    // =====================================================

    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("user");
        localStorage.removeItem("tokenExpiresIn");
        localStorage.removeItem("loginTime");
        localStorage.removeItem("rememberMe");

        /*
         * deviceId remove nahi karenge.
         */

        navigate("/login");
    };

    // =====================================================
    // Loading
    // =====================================================

    if (isLoading) {
        return (
            <div className="student-settings-page">

                <div className="student-settings-state">

                    <h2>
                        Loading Settings...
                    </h2>

                </div>

            </div>
        );
    }

    // =====================================================
    // Error
    // =====================================================

    if (!settingsData && errorMessage) {
        return (
            <div className="student-settings-page">

                <div className="student-settings-state">

                    <h2>
                        Unable to load settings
                    </h2>

                    <p>
                        {errorMessage}
                    </p>

                    <button
                        type="button"
                        onClick={() =>
                            window.location.reload()
                        }
                    >
                        Try Again
                    </button>

                    <button
                        type="button"
                        onClick={goToDashboard}
                    >
                        Back to Dashboard
                    </button>

                </div>

            </div>
        );
    }

    // =====================================================
    // UI
    // =====================================================

    return (
        <div className="student-settings-page">

            {/* ============================================
                HEADER
            ============================================ */}

            <header className="student-settings-header">
                <StudentMobileMenu />

                <div>

                    <span className="student-settings-tag">
                        SETTINGS
                    </span>

                    <h1>
                        Student Settings
                    </h1>

                    <p>
                        Manage your notifications and
                        application preferences.
                    </p>

                </div>

                <button
                    type="button"
                    className="student-settings-back-button"
                    onClick={goToDashboard}
                >
                    ← Dashboard
                </button>

            </header>

            {/* ============================================
                MESSAGE
            ============================================ */}

            {successMessage && (

                <div className="student-settings-success">
                    {successMessage}
                </div>

            )}

            {errorMessage && settingsData && (

                <div className="student-settings-error">
                    {errorMessage}
                </div>

            )}

            {/* ============================================
                ACCOUNT INFORMATION
            ============================================ */}

            <section className="student-settings-section">

                <div className="student-settings-section-heading">

                    <div>

                        <span>
                            ACCOUNT
                        </span>

                        <h2>
                            Account Information
                        </h2>

                        <p>
                            Your logged-in student account.
                        </p>

                    </div>

                </div>

                <div className="student-settings-account-grid">

                    <div className="student-settings-account-item">

                        <span>
                            Student Name
                        </span>

                        <strong>
                            {settingsData?.studentName ||
                                "Student"}
                        </strong>

                    </div>

                    <div className="student-settings-account-item">

                        <span>
                            Email
                        </span>

                        <strong>
                            {settingsData?.email ||
                                storedUser?.email ||
                                "-"}
                        </strong>

                    </div>

                    <div className="student-settings-account-item">

                        <span>
                            Student ID
                        </span>

                        <strong>
                            {settingsData?.studentId ??
                                "-"}
                        </strong>

                    </div>

                </div>

            </section>

            {/* ============================================
                NOTIFICATION SETTINGS
            ============================================ */}

            <section className="student-settings-section">

                <div className="student-settings-section-heading">

                    <div>

                        <span>
                            NOTIFICATIONS
                        </span>

                        <h2>
                            Notification Preferences
                        </h2>

                        <p>
                            Choose which student alerts
                            you want to receive.
                        </p>

                    </div>

                </div>

                <div className="student-settings-list">

                    {/* MAIN NOTIFICATIONS */}

                    <div className="student-setting-row">

                        <div>

                            <h3>
                                Notifications
                            </h3>

                            <p>
                                Enable or disable all
                                student notifications.
                            </p>

                        </div>

                        <button
                            type="button"
                            className={
                                formData.notificationsEnabled
                                    ? "student-setting-toggle active"
                                    : "student-setting-toggle"
                            }
                            onClick={() =>
                                handleToggle(
                                    "notificationsEnabled"
                                )
                            }
                        >
                            <span></span>
                        </button>

                    </div>

                    {/* CLASS REMINDER */}

                    <div className="student-setting-row">

                        <div>

                            <h3>
                                Class Reminder
                            </h3>

                            <p>
                                Receive reminders about
                                upcoming classes.
                            </p>

                        </div>

                        <button
                            type="button"
                            className={
                                formData.classReminderEnabled
                                    ? "student-setting-toggle active"
                                    : "student-setting-toggle"
                            }
                            onClick={() =>
                                handleToggle(
                                    "classReminderEnabled"
                                )
                            }
                            disabled={
                                !formData.notificationsEnabled
                            }
                        >
                            <span></span>
                        </button>

                    </div>

                    {/* ATTENDANCE ALERT */}

                    <div className="student-setting-row">

                        <div>

                            <h3>
                                Attendance Alert
                            </h3>

                            <p>
                                Receive attendance and low
                                attendance alerts.
                            </p>

                        </div>

                        <button
                            type="button"
                            className={
                                formData.attendanceAlertEnabled
                                    ? "student-setting-toggle active"
                                    : "student-setting-toggle"
                            }
                            onClick={() =>
                                handleToggle(
                                    "attendanceAlertEnabled"
                                )
                            }
                            disabled={
                                !formData.notificationsEnabled
                            }
                        >
                            <span></span>
                        </button>

                    </div>

                    {/* TIMETABLE ALERT */}

                    <div className="student-setting-row">

                        <div>

                            <h3>
                                Timetable Update Alert
                            </h3>

                            <p>
                                Receive alerts when timetable
                                changes are made.
                            </p>

                        </div>

                        <button
                            type="button"
                            className={
                                formData.timetableAlertEnabled
                                    ? "student-setting-toggle active"
                                    : "student-setting-toggle"
                            }
                            onClick={() =>
                                handleToggle(
                                    "timetableAlertEnabled"
                                )
                            }
                            disabled={
                                !formData.notificationsEnabled
                            }
                        >
                            <span></span>
                        </button>

                    </div>

                </div>

            </section>

            {/* ============================================
                APPEARANCE
            ============================================ */}

            <section className="student-settings-section">

                <div className="student-settings-section-heading">

                    <div>

                        <span>
                            APPEARANCE
                        </span>

                        <h2>
                            Theme
                        </h2>

                        <p>
                            Select your preferred application
                            appearance.
                        </p>

                    </div>

                </div>

                <div className="student-theme-options">

                    <label
                        className={
                            formData.theme === "DARK"
                                ? "student-theme-card active"
                                : "student-theme-card"
                        }
                    >

                        <input
                            type="radio"
                            name="theme"
                            value="DARK"
                            checked={
                                formData.theme === "DARK"
                            }
                            onChange={handleThemeChange}
                        />

                        <span className="student-theme-icon">
                            🌙
                        </span>

                        <div>

                            <strong>
                                Dark Theme
                            </strong>

                            <p>
                                Dark dashboard appearance.
                            </p>

                        </div>

                    </label>

                    <label
                        className={
                            formData.theme === "LIGHT"
                                ? "student-theme-card active"
                                : "student-theme-card"
                        }
                    >

                        <input
                            type="radio"
                            name="theme"
                            value="LIGHT"
                            checked={
                                formData.theme === "LIGHT"
                            }
                            onChange={handleThemeChange}
                        />

                        <span className="student-theme-icon">
                            ☀️
                        </span>

                        <div>

                            <strong>
                                Light Theme
                            </strong>

                            <p>
                                Light application appearance.
                            </p>

                        </div>

                    </label>

                </div>

            </section>

            {/* ============================================
                SAVE ACTIONS
            ============================================ */}

            <section className="student-settings-actions">

                <button
                    type="button"
                    className="student-settings-reset-button"
                    onClick={handleReset}
                    disabled={isSaving}
                >
                    Reset Changes
                </button>

                <button
                    type="button"
                    className="student-settings-save-button"
                    onClick={handleSaveSettings}
                    disabled={isSaving}
                >
                    {isSaving
                        ? "Saving..."
                        : "Save Settings"}
                </button>

            </section>

            {/* ============================================
                BOTTOM NAVIGATION
            ============================================ */}

            <nav className="student-settings-bottom-nav">

                <button
                    type="button"
                    className="student-settings-nav-item"
                    onClick={goToDashboard}
                >
                    <span>▦</span>
                    Dashboard
                </button>

                <button
                    type="button"
                    className="student-settings-nav-item"
                    onClick={goToAttendance}
                >
                    <span>▣</span>
                    Attendance
                </button>

                <button
                    type="button"
                    className="student-settings-nav-item"
                    onClick={goToTimetable}
                >
                    <span>📅</span>
                    Timetable
                </button>

                <button
                    type="button"
                    className="student-settings-nav-item"
                    onClick={goToProfile}
                >
                    <span>♙</span>
                    Profile
                </button>

                <button
                    type="button"
                    className="student-settings-nav-item active"
                    onClick={goToSettings}
                >
                    <span>⚙</span>
                    Settings
                </button>

                <button
                    type="button"
                    className="student-settings-nav-item student-settings-logout"
                    onClick={handleLogout}
                >
                    <span>↪</span>
                    Logout
                </button>

            </nav>

        </div>
    );
}

export default StudentSettings;

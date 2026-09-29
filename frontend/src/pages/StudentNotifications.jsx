import StudentMobileMenu from "./StudentMobileMenu";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./StudentNotifications.css";

function StudentNotifications() {
    const navigate = useNavigate();

    // =====================================================
    // Logged-in User
    // =====================================================

    const storedUser =
        JSON.parse(localStorage.getItem("user")) || null;

    // =====================================================
    // State
    // =====================================================

    const [notifications, setNotifications] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    // =====================================================
    // Load Notifications
    // =====================================================

    useEffect(() => {
        const loadNotifications = async () => {
            try {
                if (!storedUser?.email) {
                    throw new Error(
                        "Logged-in student information not found."
                    );
                }

                /*
                 * Notification backend banne ke baad
                 * ye API use hogi:
                 *
                 * GET /api/student/notifications?email=...
                 */

                const response = await fetch(
                    `http://localhost:8080/api/student/notifications?email=${encodeURIComponent(
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
                            "Failed to load notifications."
                    );
                }

                setNotifications(
                    Array.isArray(data)
                        ? data
                        : data.notifications || []
                );
                setErrorMessage("");

            } catch (error) {
                console.error(
                    "Student Notifications Error:",
                    error
                );

                setErrorMessage(
                    error.message ||
                    "Unable to load notifications."
                );

            } finally {
                setIsLoading(false);
            }
        };

        loadNotifications();

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
    // Unread Count
    // =====================================================

    const unreadCount = useMemo(() => {
        return notifications.filter(
            (notification) => !notification.read
        ).length;
    }, [notifications]);

    // =====================================================
    // Notification Icon
    // =====================================================

    const getNotificationIcon = (type) => {
        switch (type) {
            case "LIVE_CLASS":
                return "📡";

            case "ATTENDANCE":
                return "✅";

            case "LOW_ATTENDANCE":
                return "⚠️";

            case "TIMETABLE":
                return "📅";

            default:
                return "🔔";
        }
    };

    // =====================================================
    // Notification Class
    // =====================================================

    const getNotificationClass = (type) => {
        switch (type) {
            case "LIVE_CLASS":
                return "notification-live-class";

            case "ATTENDANCE":
                return "notification-attendance";

            case "LOW_ATTENDANCE":
                return "notification-warning";

            case "TIMETABLE":
                return "notification-timetable";

            default:
                return "";
        }
    };

    // =====================================================
    // Format Date
    // =====================================================

    const formatNotificationTime = (dateTime) => {
        if (!dateTime) {
            return "";
        }

        const date = new Date(dateTime);

        if (Number.isNaN(date.getTime())) {
            return dateTime;
        }

        return date.toLocaleString(
            "en-IN",
            {
                day: "2-digit",
                month: "short",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
                hour12: true,
            }
        );
    };

    // =====================================================
    // Mark Single Notification As Read
    // =====================================================

    const handleNotificationClick = async (
        notification
    ) => {
            try {
            /*
             * Backend ready hone ke baad:
             *
             * PATCH
             * /api/student/notifications/{id}/read
             */

            const response = await fetch(
                `http://localhost:8080/api/student/notifications/${notification.id}/read?email=${encodeURIComponent(storedUser.email)}`,
                {method: "PATCH", headers: {Authorization: `Bearer ${localStorage.getItem("accessToken")}`}}
            );
            if (!response.ok) throw new Error("Unable to mark notification as read.");

            setNotifications((previous) =>
                previous.map((item) =>
                    item.id === notification.id
                        ? {
                            ...item,
                            read: true,
                        }
                        : item
                )
            );

            /*
             * Notification type ke according
             * related page open kar sakte hain.
             */

            if (
                notification.type === "ATTENDANCE" ||
                notification.type === "LOW_ATTENDANCE"
            ) {
                navigate("/student/attendance");
                return;
            }

            if (
                notification.type === "TIMETABLE"
            ) {
                navigate("/student/timetable");
                return;
            }

            if (
                notification.type === "LIVE_CLASS"
            ) {
                navigate("/student-dashboard");
            }

        } catch (error) {
            console.error(
                "Notification Read Error:",
                error
            );
        }
    };

    // =====================================================
    // Mark All As Read
    // =====================================================

    const handleMarkAllAsRead = async () => {
        try {
            if (notifications.length === 0) {
                return;
            }

            const response = await fetch(
                `http://localhost:8080/api/student/notifications/read-all?email=${encodeURIComponent(storedUser.email)}`,
                {method: "PATCH", headers: {Authorization: `Bearer ${localStorage.getItem("accessToken")}`}}
            );
            if (!response.ok) throw new Error("Unable to mark notifications as read.");

            /*
             * Backend ready hone ke baad:
             *
             * PATCH
             * /api/student/notifications/read-all?email=...
             */

            setNotifications((previous) =>
                previous.map((notification) => ({
                    ...notification,
                    read: true,
                }))
            );

            setSuccessMessage(
                "All notifications marked as read."
            );

            setTimeout(() => {
                setSuccessMessage("");
            }, 2500);

        } catch (error) {
            console.error(
                "Mark All Read Error:",
                error
            );

            setErrorMessage(
                "Unable to mark notifications as read."
            );
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
            <div className="student-notifications-page">

                <div className="student-notifications-state">

                    <h2>
                        Loading Notifications...
                    </h2>

                </div>

            </div>
        );
    }

    // =====================================================
    // Error
    // =====================================================

    if (errorMessage && notifications.length === 0) {
        return (
            <div className="student-notifications-page">

                <div className="student-notifications-state">

                    <h2>
                        Unable to load notifications
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
        <div className="student-notifications-page">

            {/* ============================================
                HEADER
            ============================================ */}

            <header className="student-notifications-header">
                <StudentMobileMenu />

                <div>

                    <span className="student-notifications-tag">
                        NOTIFICATIONS
                    </span>

                    <h1>
                        Notifications
                    </h1>

                    <p>
                        Stay updated with your classes,
                        attendance and timetable.
                    </p>

                </div>

                <button
                    type="button"
                    className="student-notifications-back-button"
                    onClick={goToDashboard}
                >
                    ← Dashboard
                </button>

            </header>

            {/* ============================================
                SUMMARY
            ============================================ */}

            <section className="student-notifications-summary">

                <div>

                    <span>
                        TOTAL
                    </span>

                    <strong>
                        {notifications.length}
                    </strong>

                </div>

                <div>

                    <span>
                        UNREAD
                    </span>

                    <strong>
                        {unreadCount}
                    </strong>

                </div>

                <div>

                    <span>
                        READ
                    </span>

                    <strong>
                        {notifications.length - unreadCount}
                    </strong>

                </div>

            </section>

            {/* ============================================
                MESSAGES
            ============================================ */}

            {successMessage && (

                <div className="student-notifications-success">
                    {successMessage}
                </div>

            )}

            {errorMessage && (

                <div className="student-notifications-error">
                    {errorMessage}
                </div>

            )}

            {/* ============================================
                NOTIFICATION LIST
            ============================================ */}

            <section className="student-notifications-section">

                <div className="student-notifications-section-heading">

                    <div>

                        <h2>
                            Recent Notifications
                        </h2>

                        <p>
                            Latest student alerts and updates.
                        </p>

                    </div>

                    <button
                        type="button"
                        className="mark-all-read-button"
                        onClick={handleMarkAllAsRead}
                        disabled={
                            notifications.length === 0 ||
                            unreadCount === 0
                        }
                    >
                        Mark All as Read
                    </button>

                </div>

                <div className="student-notification-list">

                    {notifications.length === 0 ? (

                        <div className="student-notification-empty">

                            <span>
                                🔕
                            </span>

                            <h3>
                                No Notifications
                            </h3>

                            <p>
                                You don't have any notifications
                                right now.
                            </p>

                        </div>

                    ) : (

                        notifications.map(
                            (notification) => (

                                <button
                                    type="button"
                                    key={notification.id}
                                    className={`student-notification-card ${!notification.read
                                        ? "unread"
                                        : ""
                                        } ${getNotificationClass(
                                            notification.type
                                        )}`}
                                    onClick={() =>
                                        handleNotificationClick(
                                            notification
                                        )
                                    }
                                >

                                    <div className="student-notification-icon">

                                        {getNotificationIcon(
                                            notification.type
                                        )}

                                    </div>

                                    <div className="student-notification-content">

                                        <div className="student-notification-title-row">

                                            <h3>
                                                {notification.title ||
                                                    "Notification"}
                                            </h3>

                                            {!notification.read && (
                                                <span className="student-notification-unread-dot"></span>
                                            )}

                                        </div>

                                        <p>
                                            {notification.message ||
                                                ""}
                                        </p>

                                        <small>
                                            {formatNotificationTime(
                                                notification.createdAt
                                            )}
                                        </small>

                                    </div>

                                </button>

                            )
                        )

                    )}

                </div>

            </section>

            {/* ============================================
                INFORMATION
            ============================================ */}

            <section className="student-notifications-info">

                <span>
                    ℹ
                </span>

                <p>
                    Notification delivery depends on your
                    preferences in Student Settings.
                </p>

            </section>

            {/* ============================================
                BOTTOM NAVIGATION
            ============================================ */}

            <nav className="student-notifications-bottom-nav">

                <button
                    type="button"
                    className="student-notifications-nav-item"
                    onClick={goToDashboard}
                >
                    <span>▦</span>
                    Dashboard
                </button>

                <button
                    type="button"
                    className="student-notifications-nav-item"
                    onClick={goToAttendance}
                >
                    <span>▣</span>
                    Attendance
                </button>

                <button
                    type="button"
                    className="student-notifications-nav-item"
                    onClick={goToTimetable}
                >
                    <span>📅</span>
                    Timetable
                </button>

                <button
                    type="button"
                    className="student-notifications-nav-item"
                    onClick={goToProfile}
                >
                    <span>♙</span>
                    Profile
                </button>

                <button
                    type="button"
                    className="student-notifications-nav-item"
                    onClick={goToSettings}
                >
                    <span>⚙</span>
                    Settings
                </button>

                <button
                    type="button"
                    className="student-notifications-nav-item student-notifications-logout"
                    onClick={handleLogout}
                >
                    <span>↪</span>
                    Logout
                </button>

            </nav>

        </div>
    );
}

export default StudentNotifications;

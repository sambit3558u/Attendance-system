import StudentMobileMenu from "./StudentMobileMenu";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./StudentAttendance.css";

function StudentAttendance() {
    const navigate = useNavigate();

    // =====================================================
    // Logged-in User
    // =====================================================

    const storedUser =
        JSON.parse(localStorage.getItem("user")) || null;

    // =====================================================
    // State
    // =====================================================

    const [attendanceData, setAttendanceData] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");

    // =====================================================
    // Load Attendance Data From Backend
    // =====================================================

    useEffect(() => {
        const loadAttendance = async () => {
            try {
                if (!storedUser?.email) {
                    throw new Error(
                        "Logged-in student information not found."
                    );
                }

                const response = await fetch(
                    `http://localhost:8080/api/student/attendance?email=${encodeURIComponent(
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
                        "Failed to load attendance data."
                    );
                }

                setAttendanceData(data);
                setErrorMessage("");

            } catch (error) {
                console.error(
                    "Student Attendance Error:",
                    error
                );

                setErrorMessage(
                    error.message ||
                    "Unable to load attendance."
                );

            } finally {
                setIsLoading(false);
            }
        };

        loadAttendance();

    }, [storedUser?.email]);

    // =====================================================
    // Navigation Functions
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
         * One-device login ke liye same browser ID
         * localStorage me rehna chahiye.
         */

        navigate("/login");
    };

    // =====================================================
    // Loading Screen
    // =====================================================

    if (isLoading) {
        return (
            <div className="student-attendance-page">
                <div className="attendance-page-state">
                    <h2>
                        Loading Attendance...
                    </h2>
                </div>
            </div>
        );
    }

    // =====================================================
    // Error Screen
    // =====================================================

    if (errorMessage) {
        return (
            <div className="student-attendance-page">

                <div className="attendance-page-state">

                    <h2>
                        Unable to load attendance
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
    // Backend Data
    // =====================================================

    const attendancePercentage =
        attendanceData?.attendancePercentage ?? 0;

    const totalClasses =
        attendanceData?.totalClasses ?? 0;

    const presentClasses =
        attendanceData?.presentClasses ?? 0;

    const absentClasses =
        attendanceData?.absentClasses ?? 0;

    const subjectAttendance =
        attendanceData?.subjectAttendance ?? [];

    const attendanceHistory =
        attendanceData?.attendanceHistory ?? [];

    // =====================================================
    // Format Attendance Date
    // =====================================================

    const formatAttendanceDate = (
        attendanceTime
    ) => {
        if (!attendanceTime) {
            return "-";
        }

        const date = new Date(
            attendanceTime
        );

        if (Number.isNaN(date.getTime())) {
            return attendanceTime;
        }

        return date.toLocaleDateString(
            "en-IN",
            {
                day: "2-digit",
                month: "short",
                year: "numeric",
            }
        );
    };

    // =====================================================
    // UI
    // =====================================================

    return (
        <div className="student-attendance-page">

            {/* ============================================
                HEADER
            ============================================ */}

            <header className="student-attendance-header">
                <StudentMobileMenu />

                <div>

                    <span className="attendance-page-tag">
                        ATTENDANCE
                    </span>

                    <h1>
                        My Attendance
                    </h1>

                    <p>
                        {attendanceData?.name ||
                            "Student"}

                        {attendanceData?.rollNumber
                            ? ` • ${attendanceData.rollNumber}`
                            : ""}

                        {attendanceData?.semester
                            ? ` • Semester ${attendanceData.semester}`
                            : ""}
                    </p>

                </div>

                <button
                    type="button"
                    className="attendance-back-button"
                    onClick={goToDashboard}
                >
                    ← Dashboard
                </button>

            </header>

            {/* ============================================
                SUMMARY
            ============================================ */}

            <section className="attendance-summary-grid">

                {/* OVERALL */}

                <div className="attendance-summary-card overall-attendance-card">

                    <div className="attendance-card-heading">

                        <div>

                            <span>
                                OVERALL ATTENDANCE
                            </span>

                            <h3>
                                Current Semester
                            </h3>

                        </div>

                        <span className="attendance-card-icon">
                            ◔
                        </span>

                    </div>

                    <div className="attendance-percentage-area">

                        <div
                            className="attendance-percentage-circle"
                            style={{
                                background: `conic-gradient(
                                    #00dff5 0deg,
                                    #00dff5 ${Math.min(
                                    attendancePercentage,
                                    100
                                ) * 3.6
                                    }deg,
                                    rgba(255,255,255,0.08) ${Math.min(
                                        attendancePercentage,
                                        100
                                    ) * 3.6
                                    }deg
                                )`,
                            }}
                        >

                            <div className="attendance-percentage-inner">

                                <strong>
                                    {Number(
                                        attendancePercentage
                                    ).toFixed(1)}
                                    %
                                </strong>

                                <span>
                                    ATTENDANCE
                                </span>

                            </div>

                        </div>

                    </div>

                </div>

                {/* TOTAL */}

                <div className="attendance-summary-card">

                    <span className="summary-label">
                        TOTAL CLASSES
                    </span>

                    <strong className="summary-number">
                        {totalClasses}
                    </strong>

                    <p>
                        Classes counted this semester
                    </p>

                </div>

                {/* PRESENT */}

                <div className="attendance-summary-card present-summary-card">

                    <span className="summary-label">
                        PRESENT
                    </span>

                    <strong className="summary-number">
                        {presentClasses}
                    </strong>

                    <p>
                        Classes attended
                    </p>

                </div>

                {/* ABSENT */}

                <div className="attendance-summary-card absent-summary-card">

                    <span className="summary-label">
                        ABSENT
                    </span>

                    <strong className="summary-number">
                        {absentClasses}
                    </strong>

                    <p>
                        Classes missed
                    </p>

                </div>

            </section>

            {/* ============================================
                SUBJECT-WISE ATTENDANCE
            ============================================ */}

            <section className="student-attendance-section">

                <div className="attendance-section-heading">

                    <div>

                        <h2>
                            Subject-wise Attendance
                        </h2>

                        <p>
                            {attendanceData?.branch ||
                                "Your branch"}

                            {attendanceData?.semester
                                ? ` • Semester ${attendanceData.semester}`
                                : ""}
                        </p>

                    </div>

                </div>

                <div className="subject-attendance-table-wrapper">

                    <table className="subject-attendance-table">

                        <thead>

                            <tr>

                                <th>
                                    Subject
                                </th>

                                <th>
                                    Total
                                </th>

                                <th>
                                    Present
                                </th>

                                <th>
                                    Absent
                                </th>

                                <th>
                                    Percentage
                                </th>

                            </tr>

                        </thead>

                        <tbody>

                            {subjectAttendance.length === 0 ? (

                                <tr>

                                    <td colSpan="5">
                                        No subjects found for
                                        this branch and semester.
                                    </td>

                                </tr>

                            ) : (

                                subjectAttendance.map(
                                    (subject, index) => (

                                        <tr
                                            key={
                                                subject.subjectName ||
                                                index
                                            }
                                        >

                                            <td>

                                                <strong>
                                                    {
                                                        subject.subjectName
                                                    }
                                                </strong>

                                            </td>

                                            <td>
                                                {
                                                    subject.totalClasses
                                                }
                                            </td>

                                            <td className="attendance-present-text">
                                                {
                                                    subject.presentClasses
                                                }
                                            </td>

                                            <td className="attendance-absent-text">
                                                {
                                                    subject.absentClasses
                                                }
                                            </td>

                                            <td>

                                                <div className="subject-percentage-cell">

                                                    <div className="subject-progress-bar">

                                                        <div
                                                            className="subject-progress-fill"
                                                            style={{
                                                                width: `${Math.min(
                                                                    Number(
                                                                        subject.percentage
                                                                    ) ||
                                                                    0,
                                                                    100
                                                                )}%`,
                                                            }}
                                                        ></div>

                                                    </div>

                                                    <strong>
                                                        {Number(
                                                            subject.percentage ||
                                                            0
                                                        ).toFixed(
                                                            1
                                                        )}
                                                        %
                                                    </strong>

                                                </div>

                                            </td>

                                        </tr>

                                    )
                                )

                            )}

                        </tbody>

                    </table>

                </div>

            </section>

            {/* ============================================
                ATTENDANCE HISTORY
            ============================================ */}

            <section className="student-attendance-section">

                <div className="attendance-section-heading">

                    <div>

                        <h2>
                            Attendance History
                        </h2>

                        <p>
                            Your recent attendance records.
                        </p>

                    </div>

                </div>

                <div className="attendance-history-table-wrapper">

                    <table className="attendance-history-table">

                        <thead>

                            <tr>

                                <th>
                                    Date
                                </th>

                                <th>
                                    Subject
                                </th>

                                <th>
                                    Status
                                </th>

                            </tr>

                        </thead>

                        <tbody>

                            {attendanceHistory.length === 0 ? (

                                <tr>

                                    <td colSpan="3">
                                        No attendance history
                                        available.
                                    </td>

                                </tr>

                            ) : (

                                attendanceHistory.map(
                                    (record, index) => (

                                        <tr
                                            key={
                                                record.attendanceId ||
                                                index
                                            }
                                        >

                                            <td>
                                                {formatAttendanceDate(
                                                    record.attendanceTime
                                                )}
                                            </td>

                                            <td>
                                                {
                                                    record.subjectName
                                                }
                                            </td>

                                            <td>

                                                <span
                                                    className={
                                                        record.status ===
                                                            "PRESENT"
                                                            ? "attendance-status-badge present-status"
                                                            : "attendance-status-badge absent-status"
                                                    }
                                                >
                                                    {
                                                        record.status
                                                    }
                                                </span>

                                            </td>

                                        </tr>

                                    )
                                )

                            )}

                        </tbody>

                    </table>

                </div>

            </section>

            {/* ============================================
                BOTTOM NAVIGATION
            ============================================ */}

            <nav className="student-attendance-bottom-nav">

                <button
                    type="button"
                    className="attendance-nav-item"
                    onClick={goToDashboard}
                >
                    <span>▦</span>
                    Dashboard
                </button>

                <button
                    type="button"
                    className="attendance-nav-item active"
                    onClick={goToAttendance}
                >
                    <span>▣</span>
                    Attendance
                </button>

                <button
                    type="button"
                    className="attendance-nav-item"
                    onClick={goToTimetable}
                >
                    <span>📅</span>
                    Timetable
                </button>

                <button
                    type="button"
                    className="attendance-nav-item"
                    onClick={goToProfile}
                >
                    <span>♙</span>
                    Profile
                </button>

                <button
                    type="button"
                    className="attendance-nav-item"
                    onClick={goToSettings}
                >
                    <span>⚙</span>
                    Settings
                </button>

                <button
                    type="button"
                    className="attendance-nav-item logout-attendance-nav"
                    onClick={handleLogout}
                >
                    <span>↪</span>
                    Logout
                </button>

            </nav>

        </div>
    );
}

export default StudentAttendance;

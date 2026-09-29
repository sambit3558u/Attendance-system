import StudentMobileMenu from "./StudentMobileMenu";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./StudentTimetable.css";

function StudentTimetable() {
    const navigate = useNavigate();

    // =====================================================
    // Logged-in User
    // =====================================================

    const storedUser =
        JSON.parse(localStorage.getItem("user")) || null;

    // =====================================================
    // State
    // =====================================================

    const [studentInfo, setStudentInfo] = useState(null);
    const [timetableData, setTimetableData] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");

    // =====================================================
    // Current Day
    // =====================================================

    const todayName =
        new Date().toLocaleDateString("en-US", {
            weekday: "long",
        });

    const [selectedDay, setSelectedDay] =
        useState(todayName);

    // =====================================================
    // Days
    // =====================================================

    const days = [
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
    ];

    // =====================================================
    // Load Timetable From Backend
    // =====================================================

    useEffect(() => {
        const loadTimetable = async () => {
            try {
                if (!storedUser?.email) {
                    throw new Error(
                        "Logged-in student information not found."
                    );
                }

                const response = await fetch(
                    `http://localhost:8080/api/student/timetable?email=${encodeURIComponent(
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
                        "Failed to load timetable."
                    );
                }

                /*
                 * Backend Response:
                 *
                 * {
                 *   userId,
                 *   name,
                 *   rollNumber,
                 *   branch,
                 *   semester,
                 *   timetable: []
                 * }
                 */

                setStudentInfo({
                    userId: data.userId,
                    name: data.name,
                    rollNumber: data.rollNumber,
                    branch: data.branch,
                    semester: data.semester,
                });

                setTimetableData(
                    Array.isArray(data.timetable)
                        ? data.timetable
                        : []
                );

                setErrorMessage("");

            } catch (error) {
                console.error(
                    "Student Timetable Error:",
                    error
                );

                setErrorMessage(
                    error.message ||
                    "Unable to load timetable."
                );

            } finally {
                setIsLoading(false);
            }
        };

        loadTimetable();

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
         * rehna chahiye.
         */

        navigate("/login");
    };

    // =====================================================
    // Normalize Backend Day
    // -----------------------------------------------------
    // Backend:
    // MONDAY
    //
    // Frontend:
    // Monday
    // =====================================================

    const normalizeDay = (day) => {
        if (!day) {
            return "";
        }

        const lowerDay =
            String(day).toLowerCase();

        return (
            lowerDay.charAt(0).toUpperCase() +
            lowerDay.slice(1)
        );
    };

    // =====================================================
    // Selected Day Classes
    // =====================================================

    const selectedDayClasses = useMemo(() => {
        return timetableData
            .filter(
                (item) =>
                    normalizeDay(item.day) ===
                    selectedDay
            )
            .sort((a, b) =>
                String(a.startTime).localeCompare(
                    String(b.startTime)
                )
            );
    }, [timetableData, selectedDay]);

    // =====================================================
    // Today's Classes
    // =====================================================

    const todayClasses = useMemo(() => {
        return timetableData
            .filter(
                (item) =>
                    normalizeDay(item.day) ===
                    todayName
            )
            .sort((a, b) =>
                String(a.startTime).localeCompare(
                    String(b.startTime)
                )
            );
    }, [timetableData, todayName]);

    // =====================================================
    // Time Format
    // =====================================================

    const formatTime = (time) => {
        if (!time) {
            return "-";
        }

        const [hours, minutes] =
            String(time).split(":");

        const date = new Date();

        date.setHours(
            Number(hours),
            Number(minutes || 0),
            0,
            0
        );

        return date.toLocaleTimeString(
            "en-IN",
            {
                hour: "2-digit",
                minute: "2-digit",
                hour12: true,
            }
        );
    };

    // =====================================================
    // Loading
    // =====================================================

    if (isLoading) {
        return (
            <div className="student-timetable-page">

                <div className="timetable-page-state">

                    <h2>
                        Loading Timetable...
                    </h2>

                </div>

            </div>
        );
    }

    // =====================================================
    // Error
    // =====================================================

    if (errorMessage) {
        return (
            <div className="student-timetable-page">

                <div className="timetable-page-state">

                    <h2>
                        Unable to load timetable
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
    // Student Information
    // -----------------------------------------------------
    // IMPORTANT:
    //
    // Branch + Semester ab timetable ke first record se
    // nahi liya ja raha.
    //
    // Backend users table se latest student information
    // bhej raha hai.
    //
    // Student ka semester database me update hua:
    //
    // 3 -> 4
    //
    // Page reload/API call ke baad:
    //
    // Semester 4
    // + Semester 4 timetable automatically show hoga.
    // =====================================================

    const studentBranch =
        studentInfo?.branch ||
        "Not Available";

    const studentSemester =
        studentInfo?.semester ?? "-";

    // =====================================================
    // UI
    // =====================================================

    return (
        <div className="student-timetable-page">

            {/* ============================================
                HEADER
            ============================================ */}

            <header className="student-timetable-header">
                <StudentMobileMenu />

                <div>

                    <span className="timetable-page-tag">
                        TIMETABLE
                    </span>

                    <h1>
                        My Timetable
                    </h1>

                    <p>
                        {studentInfo?.name ||
                            "Student"}

                        {studentInfo?.rollNumber
                            ? ` • Roll No: ${studentInfo.rollNumber}`
                            : ""}
                    </p>

                </div>

                <button
                    type="button"
                    className="timetable-back-button"
                    onClick={goToDashboard}
                >
                    ← Dashboard
                </button>

            </header>

            {/* ============================================
                STUDENT INFO
            ============================================ */}

            <section className="timetable-student-info">

                <div>

                    <span>
                        Branch
                    </span>

                    <strong>
                        {studentBranch}
                    </strong>

                </div>

                <div>

                    <span>
                        Semester
                    </span>

                    <strong>
                        {studentSemester}
                    </strong>

                </div>

                <div>

                    <span>
                        Today
                    </span>

                    <strong>
                        {todayName}
                    </strong>

                </div>

            </section>

            {/* ============================================
                TODAY'S TIMETABLE
            ============================================ */}

            <section className="student-timetable-section">

                <div className="timetable-section-heading">

                    <div>

                        <h2>
                            Today's Timetable
                        </h2>

                        <p>
                            Semester {studentSemester} scheduled
                            classes for today.
                        </p>

                    </div>

                    <span className="timetable-heading-icon">
                        📅
                    </span>

                </div>

                <div className="today-timetable-list">

                    {todayClasses.length === 0 ? (

                        <div className="timetable-empty-state">

                            <span>
                                📭
                            </span>

                            <h3>
                                No classes scheduled
                            </h3>

                            <p>
                                No timetable entries found
                                for {todayName}.
                            </p>

                        </div>

                    ) : (

                        todayClasses.map(
                            (item) => (

                                <div
                                    className="today-timetable-card"
                                    key={item.id}
                                >

                                    <div className="today-class-time">

                                        <strong>
                                            {formatTime(
                                                item.startTime
                                            )}
                                        </strong>

                                        <span>
                                            to
                                        </span>

                                        <strong>
                                            {formatTime(
                                                item.endTime
                                            )}
                                        </strong>

                                    </div>

                                    <div className="today-class-details">

                                        <h3>
                                            {
                                                item.subjectName
                                            }
                                        </h3>

                                        <p>
                                            Subject Code:{" "}
                                            {item.subjectCode ||
                                                "-"}
                                        </p>

                                        <p>
                                            Teacher:{" "}
                                            {item.teacherName ||
                                                "Not Assigned"}
                                        </p>

                                    </div>

                                    <div className="today-class-day">

                                        {normalizeDay(
                                            item.day
                                        )}

                                    </div>

                                </div>

                            )
                        )

                    )}

                </div>

            </section>

            {/* ============================================
                WEEKLY TIMETABLE
            ============================================ */}

            <section className="student-timetable-section">

                <div className="timetable-section-heading">

                    <div>

                        <h2>
                            Weekly Timetable
                        </h2>

                        <p>
                            Semester {studentSemester} weekly
                            class schedule.
                        </p>

                    </div>

                </div>

                {/* DAY BUTTONS */}

                <div className="timetable-day-tabs">

                    {days.map((day) => (

                        <button
                            key={day}
                            type="button"
                            className={
                                selectedDay === day
                                    ? "timetable-day-button active"
                                    : "timetable-day-button"
                            }
                            onClick={() =>
                                setSelectedDay(day)
                            }
                        >
                            {day.slice(0, 3)}
                        </button>

                    ))}

                </div>

                {/* SELECTED DAY */}

                <div className="selected-day-header">

                    <h3>
                        {selectedDay}
                    </h3>

                    <span>
                        {selectedDayClasses.length}{" "}

                        {selectedDayClasses.length === 1
                            ? "Class"
                            : "Classes"}
                    </span>

                </div>

                {/* WEEKLY TABLE */}

                <div className="student-weekly-table-wrapper">

                    <table className="student-weekly-table">

                        <thead>

                            <tr>

                                <th>
                                    Subject
                                </th>

                                <th>
                                    Subject Code
                                </th>

                                <th>
                                    Start Time
                                </th>

                                <th>
                                    End Time
                                </th>

                                <th>
                                    Teacher
                                </th>

                            </tr>

                        </thead>

                        <tbody>

                            {selectedDayClasses.length ===
                                0 ? (

                                <tr>

                                    <td colSpan="5">

                                        No classes scheduled
                                        for {selectedDay}.

                                    </td>

                                </tr>

                            ) : (

                                selectedDayClasses.map(
                                    (item) => (

                                        <tr key={item.id}>

                                            <td>

                                                <strong>
                                                    {
                                                        item.subjectName
                                                    }
                                                </strong>

                                            </td>

                                            <td>
                                                {
                                                    item.subjectCode ||
                                                    "-"
                                                }
                                            </td>

                                            <td>
                                                {formatTime(
                                                    item.startTime
                                                )}
                                            </td>

                                            <td>
                                                {formatTime(
                                                    item.endTime
                                                )}
                                            </td>

                                            <td>
                                                {
                                                    item.teacherName ||
                                                    "Not Assigned"
                                                }
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
                INFORMATION
            ============================================ */}

            <section className="timetable-info-card">

                <span>
                    ℹ
                </span>

                <div>

                    <h3>
                        Timetable Information
                    </h3>

                    <p>
                        Your timetable is automatically loaded
                        using your current branch and semester
                        stored in the database. When your
                        semester changes, the timetable for
                        the new semester will automatically
                        load after refresh.
                    </p>

                </div>

            </section>

            {/* ============================================
                BOTTOM NAVIGATION
            ============================================ */}

            <nav className="student-timetable-bottom-nav">

                <button
                    type="button"
                    className="timetable-nav-item"
                    onClick={goToDashboard}
                >
                    <span>▦</span>
                    Dashboard
                </button>

                <button
                    type="button"
                    className="timetable-nav-item"
                    onClick={goToAttendance}
                >
                    <span>▣</span>
                    Attendance
                </button>

                <button
                    type="button"
                    className="timetable-nav-item active"
                    onClick={goToTimetable}
                >
                    <span>📅</span>
                    Timetable
                </button>

                <button
                    type="button"
                    className="timetable-nav-item"
                    onClick={goToProfile}
                >
                    <span>♙</span>
                    Profile
                </button>

                <button
                    type="button"
                    className="timetable-nav-item"
                    onClick={goToSettings}
                >
                    <span>⚙</span>
                    Settings
                </button>

                <button
                    type="button"
                    className="timetable-nav-item timetable-logout-nav"
                    onClick={handleLogout}
                >
                    <span>↪</span>
                    Logout
                </button>

            </nav>

        </div>
    );
}

export default StudentTimetable;

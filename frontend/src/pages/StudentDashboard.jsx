import StudentMobileMenu from "./StudentMobileMenu";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./StudentDashboard.css";

const API = "http://localhost:8080/api/student";

function StudentDashboard() {
    const navigate = useNavigate();

    // =====================================================
    // LOGGED-IN USER
    // =====================================================

    const storedUser = useMemo(() => {
        try {
            const value = localStorage.getItem("user");

            return value
                ? JSON.parse(value)
                : null;
        } catch {
            return null;
        }
    }, []);

    const accessToken =
        localStorage.getItem("accessToken") || "";

    const studentEmail =
        storedUser?.email || "";

    // =====================================================
    // STATE
    // =====================================================

    const [dashboardData, setDashboardData] =
        useState(null);

    const [isLoading, setIsLoading] =
        useState(true);

    const [errorMessage, setErrorMessage] =
        useState("");

    const [gpsStatus, setGpsStatus] =
        useState("NOT_CONNECTED");

    const [currentLocation, setCurrentLocation] =
        useState(null);

    const [gpsError, setGpsError] =
        useState("");

    const [isMarking, setIsMarking] =
        useState(false);

    const [attendanceMessage, setAttendanceMessage] =
        useState("");

    const [attendanceResult, setAttendanceResult] =
        useState(null);

    // =====================================================
    // DEVICE ID
    //
    // Login.jsx bhi same "deviceId" use karta hai.
    // =====================================================

    const getDeviceId = () => {
        let deviceId =
            localStorage.getItem("deviceId");

        if (!deviceId) {
            deviceId = crypto.randomUUID();

            localStorage.setItem(
                "deviceId",
                deviceId
            );
        }

        return deviceId;
    };

    // =====================================================
    // CLEAR SESSION
    // =====================================================

    const clearSession = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("user");
        localStorage.removeItem("tokenExpiresIn");
        localStorage.removeItem("loginTime");
        localStorage.removeItem("rememberMe");

        /*
         * deviceId ko remove mat karo.
         * Same browser/device identity rehni chahiye.
         */
    };

    // =====================================================
    // API HELPER
    // =====================================================

    const apiCall = async (
        url,
        options = {}
    ) => {
        if (!accessToken) {
            clearSession();

            navigate(
                "/login",
                {
                    replace: true,
                }
            );

            throw new Error(
                "Login session not found."
            );
        }

        const response = await fetch(
            url,
            {
                ...options,

                headers: {
                    "Content-Type":
                        "application/json",

                    Authorization:
                        `Bearer ${accessToken}`,

                    ...(options.headers || {}),
                },
            }
        );

        const text =
            await response.text();

        let data = null;

        if (text) {
            try {
                data = JSON.parse(text);
            } catch {
                data = text;
            }
        }

        if (
            response.status === 401 ||
            response.status === 403
        ) {
            clearSession();

            navigate(
                "/login",
                {
                    replace: true,
                }
            );

            throw new Error(
                "Session expired. Please login again."
            );
        }

        if (!response.ok) {
            const message =
                data?.message ||
                data?.error ||
                (
                    typeof data === "string"
                        ? data
                        : "Something went wrong."
                );

            throw new Error(message);
        }

        return data;
    };

    // =====================================================
    // LOAD DASHBOARD
    // =====================================================

    const loadDashboard = async (
        silent = false
    ) => {
        if (!studentEmail) {
            if (!silent) {
                setErrorMessage(
                    "Logged-in student information not found."
                );

                setIsLoading(false);
            }

            return;
        }

        try {
            if (!silent) {
                setIsLoading(true);
            }

            const data = await apiCall(
                `${API}/dashboard?email=${encodeURIComponent(
                    studentEmail
                )}`
            );

            setDashboardData(data);

            if (!silent) {
                setErrorMessage("");
            }
        } catch (error) {
            console.error(
                "Student Dashboard Error:",
                error
            );

            if (!silent) {
                setErrorMessage(
                    error?.message ||
                    "Unable to load dashboard."
                );
            }
        } finally {
            if (!silent) {
                setIsLoading(false);
            }
        }
    };

    // =====================================================
    // INITIAL LOAD
    // =====================================================

    useEffect(() => {
        if (
            !studentEmail ||
            !accessToken
        ) {
            clearSession();

            navigate(
                "/login",
                {
                    replace: true,
                }
            );

            return;
        }

        loadDashboard();
    }, []);

    // =====================================================
    // LIVE DATA POLLING
    //
    // Teacher Start/End,
    // notification count,
    // attendance summary update.
    // =====================================================

    useEffect(() => {
        if (
            !studentEmail ||
            !accessToken
        ) {
            return;
        }

        const intervalId =
            setInterval(() => {
                loadDashboard(true);
            }, 5000);

        return () => {
            clearInterval(intervalId);
        };
    }, [
        studentEmail,
        accessToken,
    ]);

    // =====================================================
    // NAVIGATION
    // =====================================================

    const goToDashboard = () => {
        navigate("/student-dashboard");
    };

    const goToAttendance = () => {
        navigate("/student/attendance");
    };

    const goToQrScanner = () => {
        navigate("/student/attendance/scan-qr");
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

    const goToNotifications = () => {
        navigate("/student/notifications");
    };

    // =====================================================
    // LOGOUT
    // =====================================================

    const handleLogout = () => {
        clearSession();

        navigate(
            "/login",
            {
                replace: true,
            }
        );
    };

    // =====================================================
    // GPS
    // =====================================================

    const requestCurrentLocation = () => {
        setGpsError("");

        if (!navigator.geolocation) {
            setGpsStatus(
                "NOT_SUPPORTED"
            );

            setGpsError(
                "GPS is not supported in this browser."
            );

            return;
        }

        setGpsStatus(
            "CONNECTING"
        );

        navigator.geolocation.getCurrentPosition(
            (position) => {
                setCurrentLocation({
                    latitude:
                        position.coords.latitude,

                    longitude:
                        position.coords.longitude,

                    accuracy:
                        position.coords.accuracy,
                });

                setGpsStatus(
                    "CONNECTED"
                );

                setGpsError("");
            },

            (error) => {
                setCurrentLocation(null);

                setGpsStatus(
                    "NOT_CONNECTED"
                );

                if (
                    error.code ===
                    error.PERMISSION_DENIED
                ) {
                    setGpsError(
                        "Location permission denied. Please allow location permission."
                    );
                } else if (
                    error.code ===
                    error.POSITION_UNAVAILABLE
                ) {
                    setGpsError(
                        "Current GPS location is unavailable."
                    );
                } else if (
                    error.code ===
                    error.TIMEOUT
                ) {
                    setGpsError(
                        "GPS request timed out. Please try again."
                    );
                } else {
                    setGpsError(
                        "Unable to get current GPS location."
                    );
                }
            },

            {
                enableHighAccuracy: true,
                timeout: 15000,
                maximumAge: 0,
            }
        );
    };

    // =====================================================
    // AUTO GPS WHEN LIVE CLASS STARTS
    // =====================================================

    useEffect(() => {
        if (
            dashboardData?.liveClass &&
            gpsStatus === "NOT_CONNECTED"
        ) {
            requestCurrentLocation();
        }
    }, [
        dashboardData
            ?.liveClass
            ?.sessionId,
    ]);

    // =====================================================
    // MARK ATTENDANCE
    // =====================================================

    const handleMarkAttendance =
        async () => {
            const liveClass =
                dashboardData?.liveClass;

            setAttendanceMessage("");
            setAttendanceResult(null);

            if (!liveClass?.sessionId) {
                setAttendanceMessage(
                    "No live attendance session available."
                );

                return;
            }

            if (!currentLocation) {
                setAttendanceMessage(
                    "GPS connect karo, phir attendance mark karo."
                );

                requestCurrentLocation();

                return;
            }

            setIsMarking(true);

            try {
                const deviceId =
                    getDeviceId();

                const result =
                    await apiCall(
                        `${API}/attendance/mark`,
                        {
                            method: "POST",

                            body: JSON.stringify({
                                sessionId:
                                    liveClass.sessionId,

                                latitude:
                                    currentLocation.latitude,

                                longitude:
                                    currentLocation.longitude,

                                accuracy:
                                    currentLocation.accuracy,

                                deviceId,
                            }),
                        }
                    );

                setAttendanceResult(
                    result
                );

                setAttendanceMessage(
                    result?.message ||
                    "Attendance marked successfully."
                );

                await loadDashboard(true);
            } catch (error) {
                setAttendanceMessage(
                    error?.message ||
                    "Unable to mark attendance."
                );
            } finally {
                setIsMarking(false);
            }
        };

    // =====================================================
    // FORMAT HELPERS
    // =====================================================

    const formatTime = (
        value
    ) => {
        if (!value) {
            return "--";
        }

        try {
            const parts =
                value.split(":");

            const hour =
                Number(parts[0]);

            const minute =
                parts[1];

            const suffix =
                hour >= 12
                    ? "PM"
                    : "AM";

            const formattedHour =
                hour % 12 || 12;

            return `${formattedHour}:${minute} ${suffix}`;
        } catch {
            return value;
        }
    };

    const formatDate = (
        value
    ) => {
        if (!value) {
            return "--";
        }

        try {
            return new Date(
                `${value}T00:00:00`
            ).toLocaleDateString(
                "en-IN"
            );
        } catch {
            return value;
        }
    };

    // =====================================================
    // LOADING
    // =====================================================

    if (isLoading) {
        return (
            <div className="student-dashboard-page">
                <div className="student-loading-screen">
                    <div className="student-loading-spinner"></div>

                    <h2>
                        Loading Student Dashboard...
                    </h2>
                </div>
            </div>
        );
    }

    // =====================================================
    // ERROR
    // =====================================================

    if (errorMessage) {
        return (
            <div className="student-dashboard-page">
                <div className="student-error-screen">

                    <h2>
                        Unable to load dashboard
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
                        onClick={() => {
                            clearSession();

                            navigate(
                                "/login",
                                {
                                    replace: true,
                                }
                            );
                        }}
                    >
                        Back to Login
                    </button>

                </div>
            </div>
        );
    }

    // =====================================================
    // BACKEND DATA
    // =====================================================

    const attendancePercentage =
        Number(
            dashboardData
                ?.attendancePercentage ??
            0
        );

    const presentClasses =
        dashboardData
            ?.presentClasses ??
        0;

    const absentClasses =
        dashboardData
            ?.absentClasses ??
        0;

    const totalClasses =
        dashboardData
            ?.totalClasses ??
        0;

    const liveClass =
        dashboardData
            ?.liveClass ??
        null;

    const todayTimetable =
        dashboardData
            ?.todayTimetable ??
        [];

    const recentAttendance =
        dashboardData
            ?.recentAttendance ??
        [];

    const unreadNotificationCount =
        dashboardData
            ?.unreadNotificationCount ??
        0;

    // =====================================================
    // UI
    // =====================================================

    return (
        <div className="student-dashboard-page">

            {/* ============================================
                HEADER
            ============================================ */}

            <header className="student-dashboard-header">
                <StudentMobileMenu />

                <div className="student-profile-area">

                    <div
                        className="student-avatar"
                        onClick={goToProfile}
                        title="View Profile"
                    >
                        {dashboardData?.name
                            ?.charAt(0)
                            ?.toUpperCase() || "S"}
                    </div>

                    <div>

                        <h2>
                            {dashboardData?.name ||
                                "Student"}
                        </h2>

                        <p>
                            🎓 Roll No:{" "}
                            {dashboardData?.rollNumber ||
                                "N/A"}
                        </p>

                        <p>
                            {dashboardData?.branch ||
                                "Branch N/A"}

                            {dashboardData?.semester
                                ? ` • Semester ${dashboardData.semester}`
                                : ""}
                        </p>

                    </div>

                </div>

                <div className="student-header-actions">

                    <button
                        type="button"
                        className="student-icon-button"
                        onClick={goToNotifications}
                        title="Notifications"
                    >
                        🔔

                        {unreadNotificationCount > 0 && (
                            <span className="notification-count">
                                {unreadNotificationCount > 99
                                    ? "99+"
                                    : unreadNotificationCount}
                            </span>
                        )}
                    </button>

                </div>

            </header>

            {/* ============================================
                MAIN
            ============================================ */}

            <main className="student-dashboard-main">

                {/* ========================================
                    LEFT COLUMN
                ======================================== */}

                <section className="student-dashboard-left">

                    {/* CURRENT ATTENDANCE */}

                    <div
                        className="dashboard-card attendance-card"
                        onClick={goToAttendance}
                        title="View Attendance"
                    >

                        <div className="dashboard-card-heading">

                            <div>

                                <h3>
                                    Current Attendance
                                </h3>

                                <p>
                                    Overall semester attendance
                                </p>

                            </div>

                            <span className="card-icon">
                                ◔
                            </span>

                        </div>

                        <div className="attendance-circle-wrapper">

                            <div
                                className="attendance-circle"
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

                                <div className="attendance-circle-inner">

                                    <strong>
                                        {attendancePercentage.toFixed(
                                            1
                                        )}
                                        %
                                    </strong>

                                    <span>
                                        ATTENDANCE
                                    </span>

                                </div>

                            </div>

                        </div>

                        <p className="attendance-growth">
                            Total Classes: {totalClasses}
                        </p>

                    </div>

                    {/* ====================================
                        GEO-FENCED RADAR
                    ==================================== */}

                    <div className="dashboard-card geo-card">

                        <div className="dashboard-card-heading">

                            <div>

                                <h3>
                                    Geo-Fenced Radar
                                </h3>

                                <p>
                                    Attendance location verification
                                </p>

                            </div>

                            <span className="card-icon">
                                ➤
                            </span>

                        </div>

                        <div
                            className={`radar-box ${gpsStatus === "CONNECTED"
                                ? "radar-connected"
                                : ""
                                }`}
                        >

                            <div className="radar-circle radar-circle-one"></div>

                            <div className="radar-circle radar-circle-two"></div>

                            <div className="radar-circle radar-circle-three"></div>

                            <div className="radar-center"></div>

                            {currentLocation && (
                                <div className="student-location-dot"></div>
                            )}

                        </div>

                        <div className="geo-status-row">

                            <span
                                className={
                                    gpsStatus === "CONNECTED"
                                        ? "gps-active"
                                        : "gps-inactive"
                                }
                            >
                                ● GPS:{" "}
                                {gpsStatus === "CONNECTED"
                                    ? "Connected"
                                    : gpsStatus === "CONNECTING"
                                        ? "Connecting..."
                                        : "Not Connected"}
                            </span>

                            <span>
                                Accuracy:
                                <strong>
                                    {" "}
                                    {currentLocation
                                        ? `${Math.round(
                                            currentLocation.accuracy
                                        )} m`
                                        : "--"}
                                </strong>
                            </span>

                        </div>

                        {attendanceResult?.allowedRadiusMeters != null && (
                            <div className="geo-reading-row">

                                <span>
                                    Distance:
                                    <strong>
                                        {" "}
                                        {attendanceResult.distanceMeters ??
                                            "--"}{" "}
                                        m
                                    </strong>
                                </span>

                                <span>
                                    Allowed:
                                    <strong>
                                        {" "}
                                        {
                                            attendanceResult
                                                .allowedRadiusMeters
                                        }{" "}
                                        m
                                    </strong>
                                </span>

                            </div>
                        )}

                        {attendanceResult?.placeName && (
                            <div className="geo-place-name">
                                📍 {attendanceResult.placeName}
                            </div>
                        )}

                        {gpsError && (
                            <div className="geo-error">
                                {gpsError}
                            </div>
                        )}

                        <div className="geo-eligible">

                            {liveClass
                                ? gpsStatus === "CONNECTED"
                                    ? "GPS connected. Location will be verified by the server while marking attendance."
                                    : "Live class detected. Connect GPS to mark attendance."
                                : "Location verification will activate during live class."}

                        </div>

                        <button
                            type="button"
                            className="radar-gps-button"
                            onClick={requestCurrentLocation}
                            disabled={gpsStatus === "CONNECTING"}
                        >
                            {gpsStatus === "CONNECTED"
                                ? "↻ Refresh GPS"
                                : gpsStatus === "CONNECTING"
                                    ? "Connecting GPS..."
                                    : "◎ Connect GPS"}
                        </button>

                    </div>

                    {/* ====================================
                        STATISTICS
                    ==================================== */}

                    <div
                        className="dashboard-card statistics-card"
                        onClick={goToAttendance}
                        title="View Attendance Details"
                    >

                        <div className="dashboard-card-heading">

                            <div>

                                <h3>
                                    Attendance Statistics
                                </h3>

                                <p>
                                    Semester attendance overview
                                </p>

                            </div>

                            <span className="card-icon">
                                ▥
                            </span>

                        </div>

                        <div className="statistics-grid">

                            <div className="stat-box present-stat">

                                <span>
                                    PRESENT
                                </span>

                                <strong>
                                    {presentClasses} classes
                                </strong>

                            </div>

                            <div className="stat-box absent-stat">

                                <span>
                                    ABSENT
                                </span>

                                <strong>
                                    {absentClasses} classes
                                </strong>

                            </div>

                        </div>

                        <div className="semester-progress">

                            <h4>
                                Semester Progress
                            </h4>

                            <div className="progress-item">

                                <div className="progress-label">

                                    <span>
                                        Current Sem

                                        {dashboardData?.semester
                                            ? ` (Sem ${dashboardData.semester})`
                                            : ""}
                                    </span>

                                    <strong>
                                        {attendancePercentage.toFixed(
                                            1
                                        )}
                                        %
                                    </strong>

                                </div>

                                <div className="progress-bar">

                                    <div
                                        className="progress-fill current-progress"
                                        style={{
                                            width: `${Math.min(
                                                attendancePercentage,
                                                100
                                            )}%`,
                                        }}
                                    ></div>

                                </div>

                            </div>

                            <div className="progress-item">

                                <div className="progress-label">

                                    <span>
                                        Previous Semester
                                    </span>

                                    <strong>
                                        N/A
                                    </strong>

                                </div>

                                <div className="progress-bar">

                                    <div
                                        className="progress-fill previous-progress"
                                        style={{
                                            width: "0%",
                                        }}
                                    ></div>

                                </div>

                            </div>

                        </div>

                    </div>

                </section>

                {/* ========================================
                    RIGHT COLUMN
                ======================================== */}

                <section className="student-dashboard-right">

                    {/* ====================================
                        LIVE CLASS
                    ==================================== */}

                    {liveClass ? (

                        <div className="dashboard-card live-class-card">

                            <div className="live-class-top">

                                <span className="live-status">
                                    ● LIVE NOW
                                </span>

                                <span className="class-time-left">
                                    {formatTime(
                                        liveClass.startTime
                                    )}

                                    {" - "}

                                    {formatTime(
                                        liveClass.endTime
                                    )}
                                </span>

                            </div>

                            <h2>
                                {liveClass.subjectName}
                            </h2>

                            <div className="live-class-details">

                                <span>
                                    👨‍🏫{" "}
                                    {liveClass.teacherName ||
                                        "Teacher"}
                                </span>

                                <span>
                                    🏫{" "}
                                    {dashboardData?.branch ||
                                        "--"}

                                    {" • Semester "}

                                    {dashboardData?.semester ||
                                        "--"}
                                </span>

                            </div>

                            <div className="live-attendance-zone">

                                <div>

                                    <span className="live-gps-label">
                                        GPS STATUS
                                    </span>

                                    <strong>
                                        {gpsStatus === "CONNECTED"
                                            ? "● Ready"
                                            : "○ GPS Required"}
                                    </strong>

                                </div>

                                {liveClass.attendanceStatus === "PRESENT" || attendanceResult?.status === "PRESENT" ? (
                                    <strong className="live-attendance-complete">✓ Present</strong>
                                ) : (
                                    <button
                                        type="button"
                                        className="live-mark-button"
                                        onClick={handleMarkAttendance}
                                        disabled={isMarking}
                                    >
                                        {isMarking ? "Marking..." : "◎ Mark Attendance"}
                                    </button>
                                )}

                            </div>

                            {attendanceMessage && (
                                <div
                                    className={`attendance-result-message ${attendanceResult?.status ===
                                        "PRESENT"
                                        ? "attendance-success"
                                        : ""
                                        }`}
                                >
                                    {attendanceMessage}
                                </div>
                            )}

                        </div>

                    ) : (

                        <div className="dashboard-card">

                            <div className="dashboard-card-heading">

                                <div>

                                    <h3>
                                        Live Class
                                    </h3>

                                    <p>
                                        No class is currently active.
                                    </p>

                                </div>

                                <span className="card-icon">
                                    ◷
                                </span>

                            </div>

                        </div>
                    )}

                    {/* ====================================
                        TODAY'S TIMETABLE
                    ==================================== */}

                    <div
                        className="dashboard-card timetable-card"
                        onClick={goToTimetable}
                        title="View Timetable"
                    >

                        <div className="dashboard-card-heading">

                            <div>

                                <h3>
                                    Today's Timetable
                                </h3>

                                <p>
                                    Schedule flow
                                </p>

                            </div>

                            <span className="card-icon">
                                📅
                            </span>

                        </div>

                        <div className="timetable-list">

                            {todayTimetable.length === 0 ? (

                                <div className="timetable-empty">
                                    No classes scheduled today.
                                </div>

                            ) : (

                                todayTimetable.map(
                                    (item) => (

                                        <div
                                            className="timetable-item"
                                            key={item.timetableId}
                                        >

                                            <div className="timetable-subject-area">

                                                <strong>
                                                    {item.subjectName}
                                                </strong>

                                                <span>
                                                    {item.subjectCode
                                                        ? `${item.subjectCode} • `
                                                        : ""}

                                                    {item.teacherName ||
                                                        "Teacher"}
                                                </span>

                                                <span>
                                                    {formatTime(
                                                        item.startTime
                                                    )}

                                                    {" - "}

                                                    {formatTime(
                                                        item.endTime
                                                    )}
                                                </span>

                                            </div>

                                            <div className="timetable-right">

                                                <small>
                                                    Semester{" "}
                                                    {item.semester ??
                                                        dashboardData?.semester ??
                                                        "-"}
                                                </small>

                                                <span
                                                    className={`timetable-status timetable-${(
                                                        item.status ||
                                                        "UPCOMING"
                                                    ).toLowerCase()}`}
                                                >
                                                    {item.status ||
                                                        "UPCOMING"}
                                                </span>

                                            </div>

                                        </div>
                                    )
                                )
                            )}

                        </div>

                    </div>

                    {/* ====================================
                        ATTENDANCE HISTORY
                    ==================================== */}

                    <div
                        className="dashboard-card history-card"
                        onClick={goToAttendance}
                        title="View Attendance History"
                    >

                        <div className="dashboard-card-heading">

                            <div>

                                <h3>
                                    Attendance History
                                </h3>

                                <p>
                                    Recent attendance logs
                                </p>

                            </div>

                            <span className="card-icon">
                                ↶
                            </span>

                        </div>

                        <div className="attendance-table-wrapper">

                            <table className="attendance-table">

                                <thead>

                                    <tr>

                                        <th>
                                            Date
                                        </th>

                                        <th>
                                            Subject
                                        </th>

                                        <th>
                                            Teacher
                                        </th>

                                        <th>
                                            Status
                                        </th>

                                        <th>
                                            Time
                                        </th>

                                    </tr>

                                </thead>

                                <tbody>

                                    {recentAttendance.length === 0 ? (

                                        <tr>
                                            <td colSpan="5">
                                                No attendance records available.
                                            </td>
                                        </tr>

                                    ) : (

                                        recentAttendance.map(
                                            (item) => (

                                                <tr
                                                    key={
                                                        item.attendanceId
                                                    }
                                                >

                                                    <td>
                                                        {formatDate(
                                                            item.date
                                                        )}
                                                    </td>

                                                    <td>
                                                        {item.subjectName ||
                                                            "--"}
                                                    </td>

                                                    <td>
                                                        {item.teacherName ||
                                                            "--"}
                                                    </td>

                                                    <td>
                                                        <span
                                                            className={`history-status history-${(
                                                                item.status ||
                                                                ""
                                                            ).toLowerCase()}`}
                                                        >
                                                            {item.status ||
                                                                "--"}
                                                        </span>
                                                    </td>

                                                    <td>
                                                        {formatTime(
                                                            item.time
                                                        )}
                                                    </td>

                                                </tr>
                                            )
                                        )
                                    )}

                                </tbody>

                            </table>

                        </div>

                    </div>

                </section>

            </main>

            {/* ============================================
                FLOATING ATTENDANCE BUTTON
            ============================================ */}

            {liveClass && (

                <button
                    type="button"
                    className="floating-attendance-button"
                    title="Mark Attendance"
                    onClick={handleMarkAttendance}
                    disabled={isMarking}
                >
                    {isMarking
                        ? "…"
                        : "◎"}
                </button>
            )}

            {/* ============================================
                BOTTOM NAVIGATION
            ============================================ */}

            <nav className="student-bottom-navigation">

                <button
                    type="button"
                    className="bottom-nav-item active"
                    onClick={goToDashboard}
                >
                    <span>
                        ▦
                    </span>

                    Dashboard
                </button>

                <button
                    type="button"
                    className="bottom-nav-item"
                    onClick={goToAttendance}
                >
                    <span>
                        ▣
                    </span>

                    Attendance
                </button>

                <button
                    type="button"
                    className="bottom-nav-item"
                    onClick={goToQrScanner}
                >
                    <span>▣</span>
                    <small>Scan QR</small>
                </button>

                <button
                    type="button"
                    className="bottom-nav-item"
                    onClick={goToTimetable}
                >
                    <span>
                        📅
                    </span>

                    Timetable
                </button>

                <button
                    type="button"
                    className="bottom-nav-item"
                    onClick={goToProfile}
                >
                    <span>
                        ♙
                    </span>

                    Profile
                </button>

                <button
                    type="button"
                    className="bottom-nav-item"
                    onClick={goToSettings}
                >
                    <span>
                        ⚙
                    </span>

                    Settings
                </button>

                <button
                    type="button"
                    className="bottom-nav-item logout-nav-item"
                    onClick={handleLogout}
                >
                    <span>
                        ↪
                    </span>

                    Logout
                </button>

            </nav>

        </div>
    );
}

export default StudentDashboard;

/* eslint-disable react-hooks/set-state-in-effect */
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./TeacherDashboard.css";

const TeacherDashboard = () => {
    const navigate = useNavigate();

    // =====================================================
    // STATE
    // =====================================================

    const [dashboardData, setDashboardData] =
        useState(null);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    const [
        mobileMenuOpen,
        setMobileMenuOpen,
    ] = useState(false);

    // =====================================================
    // TEACHER OWN ATTENDANCE STATE
    // =====================================================

    const [
        teacherAttendanceLoading,
        setTeacherAttendanceLoading,
    ] = useState(false);

    const [
        teacherAttendanceMessage,
        setTeacherAttendanceMessage,
    ] = useState("");

    const [
        teacherAttendanceStatus,
        setTeacherAttendanceStatus,
    ] = useState("NOT_MARKED");

    const [
        teacherAttendanceDistance,
        setTeacherAttendanceDistance,
    ] = useState(null);

    // =====================================================
    // SCHEDULED CLASS STATE
    // =====================================================

    const [
        startingTimetableId,
        setStartingTimetableId,
    ] = useState(null);

    const [
        endingSessionId,
        setEndingSessionId,
    ] = useState(null);

    const [
        scheduleMessage,
        setScheduleMessage,
    ] = useState("");

    const [
        scheduleMessageType,
        setScheduleMessageType,
    ] = useState("");


    // =====================================================
    // ATTENDANCE LOCATION POPUP STATE
    // =====================================================

    const [
        locationModalOpen,
        setLocationModalOpen,
    ] = useState(false);

    const [
        selectedScheduleItem,
        setSelectedScheduleItem,
    ] = useState(null);

    const [
        availableLocations,
        setAvailableLocations,
    ] = useState([]);

    const [
        selectedLocationId,
        setSelectedLocationId,
    ] = useState("");

    const [
        locationLoading,
        setLocationLoading,
    ] = useState(false);

    const [
        locationError,
        setLocationError,
    ] = useState("");

    // =====================================================
    // API URLS
    // =====================================================

    const API_URL =
        "http://localhost:8080/api/teacher/dashboard";

    const TEACHER_ATTENDANCE_URL =
        "http://localhost:8080/api/teacher/attendance/mark";

    const START_SCHEDULED_CLASS_URL =
        "http://localhost:8080/api/teacher/scheduled-classes/start";

    const SCHEDULED_CLASS_BASE_URL =
        "http://localhost:8080/api/teacher/scheduled-classes";

    const END_CLASS_BASE_URL =
        "http://localhost:8080/api/teacher/class";

    // =====================================================
    // CLEAR LOGIN DATA
    // =====================================================

    const clearLoginData = () => {
        localStorage.removeItem(
            "accessToken"
        );

        localStorage.removeItem(
            "user"
        );

        localStorage.removeItem(
            "tokenExpiresIn"
        );

        localStorage.removeItem(
            "loginTime"
        );

        localStorage.removeItem(
            "rememberMe"
        );
    };

    // =====================================================
    // NAVIGATION
    // =====================================================

    const goToPage = (path) => {
        setMobileMenuOpen(false);

        navigate(path);
    };

    // =====================================================
    // MOBILE MENU
    // =====================================================

    useEffect(() => {
        if (mobileMenuOpen) {
            document.body.classList.add(
                "menu-open"
            );
        } else {
            document.body.classList.remove(
                "menu-open"
            );
        }

        return () => {
            document.body.classList.remove(
                "menu-open"
            );
        };
    }, [mobileMenuOpen]);

    // =====================================================
    // FETCH DASHBOARD
    // =====================================================

    const fetchDashboardData = async (
        showLoader = true
    ) => {
        try {
            if (showLoader) {
                setLoading(true);
            }

            setError("");

            const token =
                localStorage.getItem(
                    "accessToken"
                );

            if (!token) {
                navigate("/login");
                return;
            }

            const response =
                await fetch(
                    API_URL,
                    {
                        method: "GET",

                        headers: {
                            Accept:
                                "application/json",

                            Authorization:
                                `Bearer ${token}`,
                        },
                    }
                );

            if (
                response.status === 401 ||
                response.status === 403
            ) {
                clearLoginData();

                navigate("/login");

                return;
            }

            const contentType =
                response.headers.get(
                    "content-type"
                );

            let data = null;

            if (
                contentType &&
                contentType.includes(
                    "application/json"
                )
            ) {
                data =
                    await response.json();
            }

            if (!response.ok) {
                throw new Error(
                    data?.message ||
                    `Dashboard request failed (${response.status})`
                );
            }

            if (!data) {
                throw new Error(
                    "Dashboard API returned empty response"
                );
            }

            setDashboardData(data);

            const attendanceStatus =
                data.teacherAttendanceStatus ||
                "NOT_MARKED";

            setTeacherAttendanceStatus(
                attendanceStatus
            );

            setTeacherAttendanceDistance(
                data.teacherAttendanceDistance !==
                    undefined &&
                    data.teacherAttendanceDistance !==
                    null
                    ? Number(
                        data.teacherAttendanceDistance
                    )
                    : null
            );
        } catch (err) {
            console.error(
                "Teacher Dashboard Error:",
                err
            );

            setError(
                err.message ||
                "Something went wrong while loading dashboard"
            );
        } finally {
            if (showLoader) {
                setLoading(false);
            }
        }
    };

    // =====================================================
    // PAGE LOAD
    // =====================================================

    useEffect(() => {
        fetchDashboardData();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // =====================================================
    // OPEN ATTENDANCE LOCATION POPUP
    // =====================================================

    const openAttendanceLocationModal =
        async (scheduleItem) => {

            if (!scheduleItem?.id) {
                setScheduleMessageType(
                    "error"
                );

                setScheduleMessage(
                    "Scheduled class not found."
                );

                return;
            }

            try {
                setLocationLoading(
                    true
                );

                setLocationError(
                    ""
                );

                setAvailableLocations(
                    []
                );

                setSelectedLocationId(
                    ""
                );

                setSelectedScheduleItem(
                    scheduleItem
                );

                setLocationModalOpen(
                    true
                );

                const token =
                    localStorage.getItem(
                        "accessToken"
                    );

                if (!token) {
                    clearLoginData();

                    navigate("/login");

                    return;
                }

                const response =
                    await fetch(
                        `${SCHEDULED_CLASS_BASE_URL}/${scheduleItem.id}/locations`,
                        {
                            method: "GET",

                            headers: {
                                Accept:
                                    "application/json",

                                Authorization:
                                    `Bearer ${token}`,
                            },
                        }
                    );

                let data = [];

                const contentType =
                    response.headers.get(
                        "content-type"
                    );

                if (
                    contentType &&
                    contentType.includes(
                        "application/json"
                    )
                ) {
                    data =
                        await response.json();
                }

                if (
                    response.status === 401
                ) {
                    clearLoginData();

                    navigate("/login");

                    return;
                }

                if (!response.ok) {
                    throw new Error(
                        data?.message ||
                        "Unable to load attendance locations."
                    );
                }

                const locations =
                    Array.isArray(data)
                        ? data
                        : [];

                setAvailableLocations(
                    locations
                );

                // If only one location exists,
                // select it automatically.
                if (
                    locations.length === 1
                ) {
                    setSelectedLocationId(
                        String(
                            locations[0].id
                        )
                    );
                }

            } catch (err) {
                console.error(
                    "Attendance Location Error:",
                    err
                );

                setLocationError(
                    err?.message ||
                    "Unable to load attendance locations."
                );

            } finally {
                setLocationLoading(
                    false
                );
            }
        };


    // =====================================================
    // CLOSE LOCATION POPUP
    // =====================================================

    const closeAttendanceLocationModal =
        () => {

            if (
                startingTimetableId !==
                null
            ) {
                return;
            }

            setLocationModalOpen(
                false
            );

            setSelectedScheduleItem(
                null
            );

            setAvailableLocations(
                []
            );

            setSelectedLocationId(
                ""
            );

            setLocationError(
                ""
            );
        };


    // =====================================================
    // START SCHEDULED CLASS ATTENDANCE
    // =====================================================

    const startScheduledAttendance =
        async () => {

            const timetableId =
                selectedScheduleItem?.id;

            if (!timetableId) {
                setLocationError(
                    "Scheduled class not found."
                );

                return;
            }

            if (!selectedLocationId) {
                setLocationError(
                    "Please select an attendance location."
                );

                return;
            }

            try {
                setStartingTimetableId(
                    timetableId
                );

                setLocationError(
                    ""
                );

                setScheduleMessage(
                    ""
                );

                setScheduleMessageType(
                    ""
                );

                const token =
                    localStorage.getItem(
                        "accessToken"
                    );

                if (!token) {
                    clearLoginData();

                    navigate("/login");

                    return;
                }

                const response =
                    await fetch(
                        START_SCHEDULED_CLASS_URL,
                        {
                            method: "POST",

                            headers: {
                                Accept:
                                    "application/json",

                                "Content-Type":
                                    "application/json",

                                Authorization:
                                    `Bearer ${token}`,
                            },

                            body:
                                JSON.stringify(
                                    {
                                        timetableId,

                                        attendanceLocationId:
                                            Number(
                                                selectedLocationId
                                            ),
                                    }
                                ),
                        }
                    );

                let data = {};

                const contentType =
                    response.headers.get(
                        "content-type"
                    );

                if (
                    contentType &&
                    contentType.includes(
                        "application/json"
                    )
                ) {
                    data =
                        await response.json();
                }

                if (
                    response.status === 401
                ) {
                    clearLoginData();

                    navigate("/login");

                    return;
                }

                if (!response.ok) {
                    throw new Error(
                        data?.message ||
                        "Unable to start attendance."
                    );
                }

                setScheduleMessageType(
                    "success"
                );

                setScheduleMessage(
                    data?.message ||
                    "Attendance started successfully."
                );

                setLocationModalOpen(
                    false
                );

                setSelectedScheduleItem(
                    null
                );

                setAvailableLocations(
                    []
                );

                setSelectedLocationId(
                    ""
                );

                await fetchDashboardData(
                    false
                );

            } catch (err) {
                console.error(
                    "Start Attendance Error:",
                    err
                );

                setLocationError(
                    err?.message ||
                    "Unable to start attendance."
                );

            } finally {
                setStartingTimetableId(
                    null
                );
            }
        };


    // =====================================================
    // END SCHEDULED CLASS ATTENDANCE
    // =====================================================

    const endScheduledAttendance =
        async (sessionId) => {
            if (!sessionId) {
                setScheduleMessageType(
                    "error"
                );

                setScheduleMessage(
                    "Live attendance session not found."
                );

                return;
            }

            const confirmed =
                window.confirm(
                    "End attendance now?\n\nStudents who have not marked attendance will be marked ABSENT."
                );

            if (!confirmed) {
                return;
            }

            try {
                setEndingSessionId(
                    sessionId
                );

                setScheduleMessage(
                    ""
                );

                setScheduleMessageType(
                    ""
                );

                const token =
                    localStorage.getItem(
                        "accessToken"
                    );

                if (!token) {
                    clearLoginData();

                    navigate("/login");

                    return;
                }

                const response =
                    await fetch(
                        `${END_CLASS_BASE_URL}/${sessionId}/end`,
                        {
                            method: "POST",

                            headers: {
                                Accept:
                                    "application/json",

                                Authorization:
                                    `Bearer ${token}`,
                            },
                        }
                    );

                const text =
                    await response.text();

                let data = null;

                if (text) {
                    try {
                        data =
                            JSON.parse(
                                text
                            );
                    } catch {
                        data = text;
                    }
                }

                if (
                    response.status === 401
                ) {
                    clearLoginData();

                    navigate("/login");

                    return;
                }

                if (!response.ok) {
                    const message =
                        data?.message ||
                        data?.error ||
                        (
                            typeof data ===
                                "string"
                                ? data
                                : "Unable to end attendance."
                        );

                    throw new Error(
                        message
                    );
                }

                setScheduleMessageType(
                    "success"
                );

                setScheduleMessage(
                    typeof data ===
                        "string"
                        ? data
                        : data?.message ||
                        "Attendance ended successfully. Remaining students have been marked absent."
                );

                await fetchDashboardData(
                    false
                );

            } catch (err) {
                console.error(
                    "End Attendance Error:",
                    err
                );

                setScheduleMessageType(
                    "error"
                );

                setScheduleMessage(
                    err?.message ||
                    "Unable to end attendance."
                );

            } finally {
                setEndingSessionId(
                    null
                );
            }
        };

    // =====================================================
    // VIEW ATTENDANCE
    // =====================================================

    const viewClassAttendance = (
        sessionId
    ) => {
        if (!sessionId) {
            setScheduleMessageType(
                "error"
            );

            setScheduleMessage(
                "Start attendance first to view student attendance."
            );

            return;
        }

        goToPage(
            `/teacher/attendance/${sessionId}`
        );
    };

    // =====================================================
    // MARK TEACHER OWN ATTENDANCE
    // =====================================================

    const markTeacherAttendance =
        async () => {
            try {
                if (
                    teacherAttendanceStatus ===
                    "PRESENT" ||
                    teacherAttendanceStatus ===
                    "ABSENT"
                ) {
                    setTeacherAttendanceMessage(
                        "Your attendance has already been recorded for today."
                    );

                    return;
                }

                setTeacherAttendanceLoading(
                    true
                );

                setTeacherAttendanceMessage(
                    ""
                );

                setTeacherAttendanceDistance(
                    null
                );

                const token =
                    localStorage.getItem(
                        "accessToken"
                    );

                if (!token) {
                    navigate("/login");
                    return;
                }

                if (
                    !navigator.geolocation
                ) {
                    throw new Error(
                        "Your browser does not support location services."
                    );
                }

                const position =
                    await new Promise(
                        (
                            resolve,
                            reject
                        ) => {
                            navigator.geolocation.getCurrentPosition(
                                resolve,
                                reject,
                                {
                                    enableHighAccuracy:
                                        true,

                                    timeout:
                                        15000,

                                    maximumAge:
                                        0,
                                }
                            );
                        }
                    );

                const latitude =
                    position.coords.latitude;

                const longitude =
                    position.coords.longitude;

                const accuracy =
                    position.coords.accuracy;

                const response =
                    await fetch(
                        TEACHER_ATTENDANCE_URL,
                        {
                            method: "POST",

                            headers: {
                                Accept:
                                    "application/json",

                                "Content-Type":
                                    "application/json",

                                Authorization:
                                    `Bearer ${token}`,
                            },

                            body:
                                JSON.stringify(
                                    {
                                        latitude,
                                        longitude,
                                        accuracy,
                                    }
                                ),
                        }
                    );

                let data = {};

                const contentType =
                    response.headers.get(
                        "content-type"
                    );

                if (
                    contentType &&
                    contentType.includes(
                        "application/json"
                    )
                ) {
                    data =
                        await response.json();
                }

                if (
                    response.status === 401 ||
                    response.status === 403
                ) {
                    clearLoginData();

                    navigate("/login");

                    return;
                }

                if (!response.ok) {
                    throw new Error(
                        data?.message ||
                        "Unable to mark teacher attendance."
                    );
                }

                const status =
                    data?.status ||
                    "NOT_MARKED";

                setTeacherAttendanceStatus(
                    status
                );

                if (
                    data?.distanceMeters !==
                    undefined &&
                    data?.distanceMeters !==
                    null
                ) {
                    setTeacherAttendanceDistance(
                        Number(
                            data.distanceMeters
                        )
                    );
                }

                if (data?.message) {
                    setTeacherAttendanceMessage(
                        data.message
                    );
                } else if (
                    status === "PRESENT"
                ) {
                    setTeacherAttendanceMessage(
                        "Your location has been verified. Attendance marked Present."
                    );
                } else if (
                    status === "ABSENT"
                ) {
                    setTeacherAttendanceMessage(
                        "You are outside the allowed department location. Attendance marked Absent."
                    );
                } else {
                    setTeacherAttendanceMessage(
                        "Attendance submitted successfully."
                    );
                }

                await fetchDashboardData(
                    false
                );
            } catch (err) {
                console.error(
                    "Teacher Attendance Error:",
                    err
                );

                if (err?.code === 1) {
                    setTeacherAttendanceMessage(
                        "Location permission denied. Please allow location permission and try again."
                    );
                } else if (
                    err?.code === 2
                ) {
                    setTeacherAttendanceMessage(
                        "Your current location is unavailable. Please enable GPS/location services."
                    );
                } else if (
                    err?.code === 3
                ) {
                    setTeacherAttendanceMessage(
                        "Location request timed out. Please try again."
                    );
                } else {
                    setTeacherAttendanceMessage(
                        err.message ||
                        "Unable to mark attendance."
                    );
                }
            } finally {
                setTeacherAttendanceLoading(
                    false
                );
            }
        };

    // =====================================================
    // LOGOUT
    // =====================================================

    const handleLogout = async () => {
        try {
            setMobileMenuOpen(false);

            const token =
                localStorage.getItem(
                    "accessToken"
                );

            const storedUser =
                localStorage.getItem(
                    "user"
                );

            let user = {};

            if (storedUser) {
                try {
                    user =
                        JSON.parse(
                            storedUser
                        );
                } catch (parseError) {
                    console.error(
                        "User Parse Error:",
                        parseError
                    );
                }
            }

            if (user?.email) {
                const logoutURL =
                    `http://localhost:8080/api/auth/logout?email=${encodeURIComponent(
                        user.email
                    )}`;

                await fetch(
                    logoutURL,
                    {
                        method: "POST",

                        headers: {
                            Accept:
                                "application/json",

                            Authorization:
                                token
                                    ? `Bearer ${token}`
                                    : "",
                        },
                    }
                );
            }
        } catch (logoutError) {
            console.error(
                "Logout Error:",
                logoutError
            );
        } finally {
            clearLoginData();

            navigate("/login");
        }
    };

    // =====================================================
    // LOADING
    // =====================================================

    if (loading) {
        return (
            <div className="dashboard-loading">
                <h2>
                    Loading Teacher Dashboard...
                </h2>

                <p>
                    Please wait while we load
                    your dashboard.
                </p>
            </div>
        );
    }

    // =====================================================
    // ERROR
    // =====================================================

    if (error) {
        return (
            <div className="dashboard-error">
                <h2>
                    Unable to load dashboard
                </h2>

                <p>{error}</p>

                <div>
                    <button
                        type="button"
                        onClick={() =>
                            fetchDashboardData()
                        }
                    >
                        Try Again
                    </button>

                    <button
                        type="button"
                        onClick={
                            handleLogout
                        }
                    >
                        Back to Login
                    </button>
                </div>
            </div>
        );
    }

    if (!dashboardData) {
        return null;
    }

    // =====================================================
    // BACKEND DATA
    // =====================================================

    const {
        teacher = {},

        totalClasses = 0,

        classesToday = 0,

        todaySchedule = [],

        attendanceOverview = {},

        recentClasses = [],

        myClasses = [],

        topAttendance = [],
    } = dashboardData;

    // =====================================================
    // MONTHLY TEACHER ATTENDANCE
    // =====================================================

    const presentPercentage =
        Number(
            attendanceOverview
                ?.presentPercentage
        ) || 0;

    const absentPercentage =
        Number(
            attendanceOverview
                ?.absentPercentage
        ) || 0;

    const presentCount =
        Number(
            attendanceOverview
                ?.presentCount
        ) || 0;

    const absentCount =
        Number(
            attendanceOverview
                ?.absentCount
        ) || 0;

    const totalMarkedDays =
        Number(
            attendanceOverview
                ?.totalMarkedDays
        ) || 0;

    // =====================================================
    // DATE
    // =====================================================

    const currentDate =
        new Date().toLocaleDateString(
            "en-IN",
            {
                day: "2-digit",
                month: "short",
                year: "numeric",
            }
        );

    const currentDay =
        new Date().toLocaleDateString(
            "en-US",
            {
                weekday: "long",
            }
        );

    // =====================================================
    // TEACHER INITIAL
    // =====================================================

    const teacherInitial =
        teacher?.name
            ?.charAt(0)
            ?.toUpperCase() || "T";

    const attendanceAlreadyMarked =
        teacherAttendanceStatus ===
        "PRESENT" ||
        teacherAttendanceStatus ===
        "ABSENT";

    // =====================================================
    // UI
    // =====================================================

    return (
        <div className="teacher-dashboard">

            {/* =================================================
                MOBILE TOP BAR
            ================================================= */}

            <div className="mobile-topbar">

                <div className="mobile-brand">

                    <div className="mobile-brand-icon">
                        🌐
                    </div>

                    <strong>
                        GEO-ATTEND
                    </strong>

                </div>

                <button
                    type="button"
                    className="mobile-menu-btn"
                    onClick={() =>
                        setMobileMenuOpen(
                            (previous) =>
                                !previous
                        )
                    }
                    aria-label={
                        mobileMenuOpen
                            ? "Close navigation menu"
                            : "Open navigation menu"
                    }
                    aria-expanded={
                        mobileMenuOpen
                    }
                >
                    <span></span>
                    <span></span>
                    <span></span>
                </button>

            </div>

            {/* =================================================
                SIDEBAR OVERLAY
            ================================================= */}

            <div
                className={
                    mobileMenuOpen
                        ? "sidebar-overlay show"
                        : "sidebar-overlay"
                }
                onClick={() =>
                    setMobileMenuOpen(
                        false
                    )
                }
            />

            {/* =================================================
                SIDEBAR
            ================================================= */}

            <aside
                className={
                    mobileMenuOpen
                        ? "sidebar mobile-open"
                        : "sidebar"
                }
            >

                <div className="logo">

                    <div className="logo-icon">
                        🌐
                    </div>

                    <div>
                        <h2>
                            GEO-ATTEND
                        </h2>

                        <p>
                            Smart Attendance
                            System
                        </p>
                    </div>

                </div>

                <nav className="menu">

                    <button
                        type="button"
                        className="menu-item active"
                        onClick={() =>
                            goToPage(
                                "/teacher-dashboard"
                            )
                        }
                    >
                        ▦ Dashboard
                    </button>

                    <button
                        type="button"
                        className="menu-item"
                        onClick={() =>
                            goToPage(
                                "/teacher/classes"
                            )
                        }
                    >
                        ▣ My Classes
                    </button>

                    <button
                        type="button"
                        className="menu-item"
                        onClick={() =>
                            goToPage(
                                "/teacher/attendance"
                            )
                        }
                    >
                        ☑ Attendance
                    </button>

                    <button
                        type="button"
                        className="menu-item"
                        onClick={() =>
                            goToPage(
                                "/teacher/create-class"
                            )
                        }
                    >
                        ✎ Create Class
                    </button>

                    <button
                        type="button"
                        className="menu-item"
                        onClick={() =>
                            goToPage(
                                "/teacher/reports"
                            )
                        }
                    >
                        ▤ Reports
                    </button>

                    <button
                        type="button"
                        className="menu-item"
                        onClick={() =>
                            goToPage(
                                "/teacher/calendar"
                            )
                        }
                    >
                        📅 Calendar
                    </button>

                    <button
                        type="button"
                        className="menu-item"
                        onClick={() =>
                            goToPage(
                                "/teacher/settings"
                            )
                        }
                    >
                        ⚙ Settings
                    </button>

                    <button
                        type="button"
                        className="menu-item"
                        onClick={() =>
                            goToPage(
                                "/teacher/profile"
                            )
                        }
                    >
                        ♙ Profile
                    </button>

                    <button
                        type="button"
                        className="menu-item logout"
                        onClick={
                            handleLogout
                        }
                    >
                        ⇥ Logout
                    </button>

                </nav>

                <div className="teacher-mini-profile">

                    <div className="profile-avatar">
                        {
                            teacherInitial
                        }
                    </div>

                    <div>
                        <h4>
                            {teacher?.name ||
                                "Teacher"}
                        </h4>

                        <p>
                            {teacher?.department ||
                                "Department"}
                        </p>

                        <span>
                            Teacher
                        </span>
                    </div>

                </div>

            </aside>

            {/* =================================================
                MAIN
            ================================================= */}

            <main className="dashboard-main">

                <header className="top-header">

                    <div className="mobile-top-actions">
                        <button
                            type="button"
                            className="top-notification-mobile"
                            onClick={() => goToPage("/teacher/notifications")}
                            title="Notifications"
                        >
                            🔔
                        </button>
                        <button
                            type="button"
                            className="top-profile-mobile"
                            onClick={() => goToPage("/teacher/profile")}
                            title="Teacher Profile"
                        >
                            {teacherInitial}
                        </button>
                    </div>

                    <div>
                        <h1>
                            Welcome back,<br className="mobile-welcome-break" />{" "}
                            <span className="welcome-name">{teacher?.name || "Teacher"}</span>{" "}
                            👋
                        </h1>

                        <p>
                            Here's what's
                            happening with your
                            classes today.
                        </p>
                    </div>

                    <div className="header-right">

                        <div className="date-box">
                            <strong>
                                {
                                    currentDate
                                }
                            </strong>

                            <span>
                                {
                                    currentDay
                                }
                            </span>
                        </div>

                        <button
                            type="button"
                            className="icon-btn"
                            onClick={() =>
                                goToPage(
                                    "/teacher/notifications"
                                )
                            }
                            title="Notifications"
                        >
                            🔔
                        </button>

                        <div
                            className="top-avatar"
                            onClick={() =>
                                goToPage(
                                    "/teacher/profile"
                                )
                            }
                            title="Teacher Profile"
                            role="button"
                            tabIndex={0}
                            onKeyDown={(event) => {
                                if (
                                    event.key ===
                                    "Enter" ||
                                    event.key ===
                                    " "
                                ) {
                                    goToPage(
                                        "/teacher/profile"
                                    );
                                }
                            }}
                        >
                            {
                                teacherInitial
                            }
                        </div>

                    </div>

                </header>

                {/* =================================================
                    TOP STATISTICS
                ================================================= */}

                <section className="stats-grid teacher-stats-grid">

                    <div className="stat-card">

                        <div className="stat-icon blue">
                            📚
                        </div>

                        <div>
                            <p>
                                Total Classes
                            </p>

                            <h2>
                                {
                                    totalClasses
                                }
                            </h2>

                            <span>
                                Full Academic
                                Year
                            </span>
                        </div>

                    </div>

                    <div className="stat-card">

                        <div className="stat-icon green">
                            📅
                        </div>

                        <div>
                            <p>
                                Classes Today
                            </p>

                            <h2>
                                {
                                    classesToday
                                }
                            </h2>

                            <span>
                                Today's
                                Schedule
                            </span>
                        </div>

                    </div>

                </section>

                {/* =================================================
                    DASHBOARD CONTENT
                ================================================= */}

                <section className="dashboard-content">

                    {/* =================================================
                        LEFT COLUMN
                    ================================================= */}

                    <div className="left-column">

                        {/* =================================================
                            TODAY'S SCHEDULE
                        ================================================= */}

                        <div className="card schedule-card">

                            <div className="card-header">

                                <h3>
                                    📅 Today's
                                    Schedule
                                </h3>

                                <button
                                    type="button"
                                    onClick={() =>
                                        goToPage(
                                            "/teacher/calendar"
                                        )
                                    }
                                >
                                    View Calendar
                                </button>

                            </div>

                            {scheduleMessage && (

                                <div
                                    className={
                                        scheduleMessageType ===
                                            "success"
                                            ? "schedule-action-message success"
                                            : "schedule-action-message error"
                                    }
                                >
                                    {
                                        scheduleMessage
                                    }
                                </div>

                            )}

                            <div className="schedule-list">

                                {todaySchedule.length ===
                                    0 ? (

                                    <p className="empty-message">
                                        No classes
                                        scheduled today.
                                    </p>

                                ) : (

                                    todaySchedule.map(
                                        (
                                            item,
                                            index
                                        ) => (

                                            <div
                                                className="schedule-item teacher-schedule-item"
                                                key={
                                                    item.id ??
                                                    index
                                                }
                                            >

                                                <div className="schedule-time">
                                                    {item.time ||
                                                        "--:--"}
                                                </div>

                                                <div className="schedule-info">

                                                    <h4>
                                                        {item.subject ||
                                                            "Subject"}
                                                    </h4>

                                                    <p>
                                                        <strong>
                                                            Branch:
                                                        </strong>{" "}
                                                        {item.branch ||
                                                            "-"}
                                                    </p>

                                                    <p>
                                                        <strong>
                                                            Semester:
                                                        </strong>{" "}
                                                        {item.semester ??
                                                            "-"}
                                                    </p>

                                                </div>

                                                <div className="schedule-actions">

                                                    {(
                                                        item.status === "SCHEDULED" ||
                                                        item.status === "UPCOMING"
                                                    ) && (

                                                            <span className="status upcoming">
                                                                UPCOMING
                                                            </span>

                                                        )}

                                                    {item.status ===
                                                        "LIVE" && (

                                                            <span className="status live">
                                                                🔴 LIVE
                                                            </span>

                                                        )}

                                                    {item.status ===
                                                        "COMPLETED" && (

                                                            <span className="status completed">
                                                                ✓ COMPLETED
                                                            </span>

                                                        )}

                                                    {/* START ATTENDANCE */}

                                                    {(
                                                        item.status === "SCHEDULED" ||
                                                        item.status === "UPCOMING"
                                                    ) && (

                                                            <button
                                                                type="button"
                                                                className="start-schedule-attendance-btn"
                                                                disabled={
                                                                    startingTimetableId ===
                                                                    item.id
                                                                }
                                                                onClick={() =>
                                                                    openAttendanceLocationModal(
                                                                        item
                                                                    )
                                                                }
                                                            >
                                                                ▶ Start Attendance
                                                            </button>

                                                        )}

                                                    {/* LIVE RUNNING */}

                                                    {item.status ===
                                                        "LIVE" && (

                                                            <button
                                                                type="button"
                                                                className="attendance-running-btn"
                                                                disabled
                                                            >
                                                                🔴 Attendance Running
                                                            </button>

                                                        )}

                                                    {/* END ATTENDANCE */}

                                                    {item.status ===
                                                        "LIVE" && (

                                                            <button
                                                                type="button"
                                                                className="end-schedule-attendance-btn"
                                                                disabled={
                                                                    endingSessionId ===
                                                                    item.sessionId
                                                                }
                                                                onClick={() =>
                                                                    endScheduledAttendance(
                                                                        item.sessionId
                                                                    )
                                                                }
                                                            >
                                                                {endingSessionId ===
                                                                    item.sessionId
                                                                    ? "Ending..."
                                                                    : "■ End Attendance"}
                                                            </button>

                                                        )}

                                                    {/* VIEW ATTENDANCE */}

                                                    {item.sessionId && (

                                                        <button
                                                            type="button"
                                                            className="view-schedule-attendance-btn"
                                                            onClick={() =>
                                                                viewClassAttendance(
                                                                    item.sessionId
                                                                )
                                                            }
                                                        >
                                                            View Attendance
                                                        </button>

                                                    )}

                                                </div>

                                            </div>

                                        )
                                    )

                                )}

                            </div>

                        </div>

                        {/* =================================================
                            MY COMPLETED CLASSES
                        ================================================= */}

                        <div className="card classes-card">

                            <div className="card-header">

                                <h3>
                                    📖 My Classes
                                </h3>

                                <button
                                    type="button"
                                    onClick={() =>
                                        goToPage(
                                            "/teacher/classes"
                                        )
                                    }
                                >
                                    View All Classes
                                </button>

                            </div>

                            {myClasses.length ===
                                0 ? (

                                <p className="empty-message">
                                    No completed
                                    classes available.
                                </p>

                            ) : (

                                <div className="table-wrapper">

                                    <table>

                                        <thead>
                                            <tr>
                                                <th>
                                                    Subject
                                                </th>

                                                <th>
                                                    Branch /
                                                    Semester
                                                </th>

                                                <th>
                                                    Students
                                                </th>

                                                <th>
                                                    Time
                                                </th>

                                                <th>
                                                    Attendance
                                                </th>
                                            </tr>
                                        </thead>

                                        <tbody>

                                            {myClasses.map(
                                                (
                                                    item,
                                                    index
                                                ) => (

                                                    <tr
                                                        key={
                                                            item.id ??
                                                            index
                                                        }
                                                    >

                                                        <td>
                                                            {item.className ||
                                                                "-"}
                                                        </td>

                                                        <td>
                                                            {item.course ||
                                                                "-"}
                                                        </td>

                                                        <td>
                                                            {item.studentCount ??
                                                                0}
                                                        </td>

                                                        <td>
                                                            {item.schedule ||
                                                                "-"}
                                                        </td>

                                                        <td className="actions">

                                                            <button
                                                                type="button"
                                                                className="view-schedule-attendance-btn"
                                                                onClick={() =>
                                                                    viewClassAttendance(
                                                                        item.id
                                                                    )
                                                                }
                                                            >
                                                                View Attendance
                                                            </button>

                                                        </td>

                                                    </tr>

                                                )
                                            )}

                                        </tbody>

                                    </table>

                                </div>

                            )}

                        </div>

                    </div>

                    {/* =================================================
                        MIDDLE COLUMN
                    ================================================= */}

                    <div className="middle-column">

                        <div className="card attendance-card">

                            <div className="teacher-attendance-heading">

                                <div>
                                    <h3>
                                        My Attendance
                                        (This Month)
                                    </h3>

                                    <p>
                                        Your attendance
                                        is verified using
                                        your department's
                                        registered
                                        location.
                                    </p>
                                </div>

                                {teacherAttendanceStatus ===
                                    "PRESENT" && (

                                        <span className="teacher-attendance-status present">
                                            PRESENT
                                        </span>

                                    )}

                                {teacherAttendanceStatus ===
                                    "ABSENT" && (

                                        <span className="teacher-attendance-status absent">
                                            ABSENT
                                        </span>

                                    )}

                                {teacherAttendanceStatus ===
                                    "NOT_MARKED" && (

                                        <span className="teacher-attendance-status not-marked">
                                            NOT MARKED TODAY
                                        </span>

                                    )}

                            </div>

                            <div className="attendance-top">

                                <div
                                    className="donut"
                                    style={{
                                        background:
                                            totalMarkedDays ===
                                                0
                                                ? "#e5e7eb"
                                                : `conic-gradient(
                                                    #38d068 0% ${presentPercentage}%,
                                                    #ff4961 ${presentPercentage}% 100%
                                                  )`,
                                    }}
                                >

                                    <div className="donut-inner">

                                        <strong>
                                            {
                                                presentPercentage
                                            }
                                            %
                                        </strong>

                                        <span>
                                            Present
                                        </span>

                                    </div>

                                </div>

                                <div className="legend">

                                    <p>
                                        <span className="dot green-dot"></span>

                                        <span>
                                            Present (
                                            {
                                                presentCount
                                            }
                                            )
                                        </span>

                                        <strong>
                                            {
                                                presentPercentage
                                            }
                                            %
                                        </strong>
                                    </p>

                                    <p>
                                        <span className="dot red-dot"></span>

                                        <span>
                                            Absent (
                                            {
                                                absentCount
                                            }
                                            )
                                        </span>

                                        <strong>
                                            {
                                                absentPercentage
                                            }
                                            %
                                        </strong>
                                    </p>

                                    <p className="teacher-marked-days">

                                        <span>
                                            Marked Days
                                        </span>

                                        <strong>
                                            {
                                                totalMarkedDays
                                            }
                                        </strong>

                                    </p>

                                </div>

                            </div>

                            <div className="teacher-location-note">

                                <strong>
                                    📍 Location
                                    Verification
                                </strong>

                                <span>
                                    Your current
                                    location will be
                                    compared with the
                                    location configured
                                    by Admin for{" "}

                                    <b>
                                        {teacher?.department ||
                                            "your department"}
                                    </b>
                                    .
                                </span>

                            </div>

                            <div className="teacher-attendance-buttons">

                                <button
                                    type="button"
                                    className="teacher-present-btn"
                                    onClick={
                                        markTeacherAttendance
                                    }
                                    disabled={
                                        teacherAttendanceLoading ||
                                        attendanceAlreadyMarked
                                    }
                                >

                                    {teacherAttendanceLoading
                                        ? "📍 Checking Location..."
                                        : teacherAttendanceStatus ===
                                            "PRESENT"
                                            ? "✓ Present Marked"
                                            : teacherAttendanceStatus ===
                                                "ABSENT"
                                                ? "✕ Absent Marked"
                                                : "✓ Present"}

                                </button>

                                <button
                                    type="button"
                                    className="teacher-absent-btn"
                                    disabled
                                    title="Absent is automatically decided by location verification"
                                >
                                    ✕ Absent
                                </button>

                            </div>

                            {teacherAttendanceDistance !==
                                null && (

                                    <div className="teacher-distance-info">

                                        📍 Distance from
                                        registered location:{" "}

                                        <strong>
                                            {Number(
                                                teacherAttendanceDistance
                                            ).toFixed(
                                                1
                                            )}{" "}
                                            meters
                                        </strong>

                                    </div>

                                )}

                            {teacherAttendanceMessage && (

                                <div
                                    className={
                                        teacherAttendanceStatus ===
                                            "PRESENT"
                                            ? "teacher-attendance-message success"
                                            : teacherAttendanceStatus ===
                                                "ABSENT"
                                                ? "teacher-attendance-message error"
                                                : "teacher-attendance-message"
                                    }
                                >
                                    {
                                        teacherAttendanceMessage
                                    }
                                </div>

                            )}

                        </div>

                    </div>

                    {/* =================================================
                        RIGHT COLUMN
                    ================================================= */}

                    <div className="right-column">

                        <div className="card recent-card">

                            <div className="card-header">

                                <h3>
                                    ◷ Recent Classes
                                </h3>

                                <button
                                    type="button"
                                    onClick={() =>
                                        goToPage(
                                            "/teacher/classes"
                                        )
                                    }
                                >
                                    View All
                                </button>

                            </div>

                            {recentClasses.length ===
                                0 ? (

                                <p className="empty-message">
                                    No recent
                                    completed classes.
                                </p>

                            ) : (

                                recentClasses.map(
                                    (
                                        item,
                                        index
                                    ) => (

                                        <div
                                            className="recent-item"
                                            key={
                                                item.id ??
                                                index
                                            }
                                        >

                                            <div>
                                                <h4>
                                                    {item.subject ||
                                                        "Subject"}
                                                </h4>

                                                <p>
                                                    {item.course ||
                                                        "Course"}
                                                </p>
                                            </div>

                                            <div>
                                                <span>
                                                    {item.time ||
                                                        "--:--"}
                                                </span>

                                                <small
                                                    className={
                                                        item.status ===
                                                            "COMPLETED"
                                                            ? "completed"
                                                            : "upcoming-small"
                                                    }
                                                >
                                                    {item.status ||
                                                        "UPCOMING"}
                                                </small>
                                            </div>

                                        </div>

                                    )
                                )

                            )}

                        </div>

                        <div className="card top-attendance">

                            <h3>
                                🏆 Top Attendance
                                (This Month)
                            </h3>

                            {topAttendance.length ===
                                0 ? (

                                <p className="empty-message">
                                    No attendance
                                    data available.
                                </p>

                            ) : (

                                topAttendance.map(
                                    (
                                        item,
                                        index
                                    ) => (

                                        <div
                                            className="rank-item"
                                            key={
                                                item.id ??
                                                index
                                            }
                                        >

                                            <span className="rank">
                                                {
                                                    index +
                                                    1
                                                }
                                            </span>

                                            <div>
                                                <strong>
                                                    {item.subject ||
                                                        "Subject"}
                                                </strong>

                                                <p>
                                                    {item.course ||
                                                        "Course"}
                                                </p>
                                            </div>

                                            <b>
                                                {item.percentage ??
                                                    0}
                                                %
                                            </b>

                                        </div>

                                    )
                                )

                            )}

                        </div>

                    </div>

                </section>

            </main>

            {/* =================================================
                CHOOSE ATTENDANCE LOCATION MODAL
            ================================================= */}

            {locationModalOpen && (

                <div
                    className="attendance-location-modal-overlay"
                    onClick={
                        closeAttendanceLocationModal
                    }
                >

                    <div
                        className="attendance-location-modal"
                        onClick={(event) =>
                            event.stopPropagation()
                        }
                    >

                        <div className="attendance-location-modal-header">

                            <div>
                                <h2>
                                    📍 Choose Attendance Location
                                </h2>

                                <p>
                                    Select where this class is being conducted.
                                </p>
                            </div>

                            <button
                                type="button"
                                className="attendance-location-modal-close"
                                onClick={
                                    closeAttendanceLocationModal
                                }
                                disabled={
                                    startingTimetableId !==
                                    null
                                }
                                aria-label="Close attendance location dialog"
                            >
                                ×
                            </button>

                        </div>

                        {selectedScheduleItem && (

                            <div className="attendance-location-class-info">

                                <strong>
                                    {selectedScheduleItem.subject ||
                                        "Subject"}
                                </strong>

                                <span>
                                    {selectedScheduleItem.branch ||
                                        "-"}
                                </span>

                                <span>
                                    Semester{" "}
                                    {selectedScheduleItem.semester ??
                                        "-"}
                                </span>

                                <span>
                                    {selectedScheduleItem.time ||
                                        "--:--"}
                                </span>

                            </div>

                        )}

                        {locationLoading ? (

                            <div className="attendance-location-loading">
                                Loading available locations...
                            </div>

                        ) : availableLocations.length ===
                            0 ? (

                            <div className="attendance-location-empty">

                                <strong>
                                    No attendance location available
                                </strong>

                                <p>
                                    Ask Admin to configure an attendance
                                    location for this department and branch.
                                </p>

                            </div>

                        ) : (

                            <div className="attendance-location-options">

                                {availableLocations.map(
                                    (location) => {

                                        const selected =
                                            String(
                                                selectedLocationId
                                            ) ===
                                            String(
                                                location.id
                                            );

                                        return (

                                            <button
                                                type="button"
                                                key={
                                                    location.id
                                                }
                                                className={
                                                    selected
                                                        ? "attendance-location-option selected"
                                                        : "attendance-location-option"
                                                }
                                                onClick={() => {
                                                    setSelectedLocationId(
                                                        String(
                                                            location.id
                                                        )
                                                    );

                                                    setLocationError(
                                                        ""
                                                    );
                                                }}
                                            >

                                                <div className="attendance-location-radio">

                                                    <span>
                                                        {selected
                                                            ? "●"
                                                            : "○"}
                                                    </span>

                                                </div>

                                                <div className="attendance-location-option-info">

                                                    <strong>
                                                        📍{" "}
                                                        {location.placeName ||
                                                            "Attendance Location"}
                                                    </strong>

                                                    <span>
                                                        {location.department ||
                                                            "-"}
                                                    </span>

                                                    <span>
                                                        {location.branch ||
                                                            "-"}
                                                    </span>

                                                    <small>
                                                        Allowed Radius:{" "}
                                                        {location.radiusMeters ??
                                                            0}{" "}
                                                        meters
                                                    </small>

                                                </div>

                                            </button>

                                        );
                                    }
                                )}

                            </div>

                        )}

                        {locationError && (

                            <div className="attendance-location-error">
                                {locationError}
                            </div>

                        )}

                        <div className="attendance-location-modal-actions">

                            <button
                                type="button"
                                className="attendance-location-cancel-btn"
                                onClick={
                                    closeAttendanceLocationModal
                                }
                                disabled={
                                    startingTimetableId !==
                                    null
                                }
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                className="attendance-location-start-btn"
                                onClick={
                                    startScheduledAttendance
                                }
                                disabled={
                                    locationLoading ||
                                    availableLocations.length ===
                                    0 ||
                                    !selectedLocationId ||
                                    startingTimetableId !==
                                    null
                                }
                            >

                                {startingTimetableId !==
                                    null
                                    ? "Starting Attendance..."
                                    : "▶ Start Attendance"}

                            </button>

                        </div>

                    </div>

                </div>

            )}

        </div>
    );
};

export default TeacherDashboard;

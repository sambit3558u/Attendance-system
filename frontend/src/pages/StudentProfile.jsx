import StudentMobileMenu from "./StudentMobileMenu";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./StudentProfile.css";

function StudentProfile() {
    const navigate = useNavigate();

    // =====================================================
    // Logged-in User
    // =====================================================

    const storedUser =
        JSON.parse(localStorage.getItem("user")) || null;

    // =====================================================
    // State
    // =====================================================

    const [profileData, setProfileData] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");

    const [isEditing, setIsEditing] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");

    const [formData, setFormData] = useState({
        name: "",
        phone: "",
        rollNumber: "",
        semester: "",
    });

    // =====================================================
    // Load Student Profile
    // =====================================================

    useEffect(() => {
        const loadProfile = async () => {
            try {
                if (!storedUser?.email) {
                    throw new Error(
                        "Logged-in student information not found."
                    );
                }

                const response = await fetch(
                    `http://localhost:8080/api/student/profile?email=${encodeURIComponent(storedUser.email)}`,
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
                        "Failed to load student profile."
                    );
                }

                setProfileData(data);

                setFormData({
                    name: data.name || "",
                    phone: data.phone || "",
                    rollNumber: data.rollNumber || "",
                    semester: data.semester || "",
                });

                setErrorMessage("");

            } catch (error) {
                console.error(
                    "Student Profile Error:",
                    error
                );

                setErrorMessage(
                    error.message ||
                    "Unable to load profile."
                );

            } finally {
                setIsLoading(false);
            }
        };

        loadProfile();

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
    // Handle Input Change
    // =====================================================

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((previous) => ({
            ...previous,
            [name]: value,
        }));
    };

    // =====================================================
    // Open Edit Section
    // =====================================================

    const handleEditProfile = () => {
        if (!profileData) {
            return;
        }

        setFormData({
            name: profileData.name || "",
            phone: profileData.phone || "",
            rollNumber: profileData.rollNumber || "",
            semester: profileData.semester || "",
        });

        setSuccessMessage("");
        setErrorMessage("");
        setIsEditing(true);
    };

    // =====================================================
    // Cancel Edit
    // =====================================================

    const handleCancelEdit = () => {
        setIsEditing(false);
        setSuccessMessage("");
        setErrorMessage("");
    };

    // =====================================================
    // Update Profile
    // =====================================================

    const handleUpdateProfile = async (event) => {
        event.preventDefault();

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
                `http://localhost:8080/api/student/profile?email=${encodeURIComponent(storedUser.email)}`,
                {
                    method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
                },
                    body: JSON.stringify({
                        name: formData.name.trim(),
                        phone: formData.phone.trim(),
                        rollNumber: formData.rollNumber.trim(),
                        semester: Number(formData.semester),
                    }),
                }
            );

            const data = await response.json();

            if (!response.ok) {
                throw new Error(
                    data.message ||
                    "Failed to update profile."
                );
            }

            // Updated profile show karo
            setProfileData(data);

            // Form ko bhi latest data se update karo
            setFormData({
                name: data.name || "",
                phone: data.phone || "",
                rollNumber: data.rollNumber || "",
                semester: data.semester || "",
            });

            /*
             * localStorage user me name update kar dete hain.
             *
             * Email same hai.
             * Branch backend se manage ho rahi hai.
             */
            const updatedStoredUser = {
                ...storedUser,
                name: data.name,
            };

            localStorage.setItem(
                "user",
                JSON.stringify(updatedStoredUser)
            );

            setSuccessMessage(
                "Profile updated successfully."
            );

            setIsEditing(false);

        } catch (error) {
            console.error(
                "Profile Update Error:",
                error
            );

            setErrorMessage(
                error.message ||
                "Unable to update profile."
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
            <div className="student-profile-page">

                <div className="student-profile-state">

                    <h2>
                        Loading Profile...
                    </h2>

                </div>

            </div>
        );
    }

    // =====================================================
    // Load Error
    // =====================================================

    if (!profileData && errorMessage) {
        return (
            <div className="student-profile-page">

                <div className="student-profile-state">

                    <h2>
                        Unable to load profile
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
        <div className="student-profile-page">

            {/* ============================================
                HEADER
            ============================================ */}

            <header className="student-profile-header">
                <StudentMobileMenu />

                <div>

                    <span className="student-profile-tag">
                        PROFILE
                    </span>

                    <h1>
                        My Profile
                    </h1>

                    <p>
                        View and manage your student details.
                    </p>

                </div>

                <button
                    type="button"
                    className="student-profile-back-button"
                    onClick={goToDashboard}
                >
                    ← Dashboard
                </button>

            </header>

            {/* ============================================
                SUCCESS MESSAGE
            ============================================ */}

            {successMessage && (

                <div className="student-profile-success">
                    {successMessage}
                </div>

            )}

            {/* ============================================
                ERROR MESSAGE
            ============================================ */}

            {errorMessage && profileData && (

                <div className="student-profile-error">
                    {errorMessage}
                </div>

            )}

            {/* ============================================
                PROFILE VIEW
            ============================================ */}

            {!isEditing && (

                <section className="student-profile-card">

                    {/* PROFILE TOP */}

                    <div className="student-profile-card-top">

                        <div className="student-profile-avatar">

                            {profileData?.name
                                ?.charAt(0)
                                ?.toUpperCase() || "S"}

                        </div>

                        <div className="student-profile-main-info">

                            <h2>
                                {profileData?.name}
                            </h2>

                            <p>
                                {profileData?.email}
                            </p>

                            <span>
                                {profileData?.role}
                            </span>

                        </div>

                        <button
                            type="button"
                            className="student-profile-edit-button"
                            onClick={handleEditProfile}
                        >
                            ✎ Edit Profile
                        </button>

                    </div>

                    {/* PROFILE DETAILS */}

                    <div className="student-profile-details-grid">

                        <div className="student-profile-detail">

                            <span>
                                User ID
                            </span>

                            <strong>
                                {profileData?.userId}
                            </strong>

                        </div>

                        <div className="student-profile-detail">

                            <span>
                                Full Name
                            </span>

                            <strong>
                                {profileData?.name}
                            </strong>

                        </div>

                        <div className="student-profile-detail">

                            <span>
                                Email
                            </span>

                            <strong>
                                {profileData?.email}
                            </strong>

                            <small>
                                Read Only
                            </small>

                        </div>

                        <div className="student-profile-detail">

                            <span>
                                Phone
                            </span>

                            <strong>
                                {profileData?.phone}
                            </strong>

                        </div>

                        <div className="student-profile-detail">

                            <span>
                                Roll Number
                            </span>

                            <strong>
                                {profileData?.rollNumber}
                            </strong>

                        </div>

                        <div className="student-profile-detail">

                            <span>
                                Branch
                            </span>

                            <strong>
                                {profileData?.branch}
                            </strong>

                            <small>
                                Branch cannot be changed
                            </small>

                        </div>

                        <div className="student-profile-detail">

                            <span>
                                Semester
                            </span>

                            <strong>
                                Semester {profileData?.semester}
                            </strong>

                        </div>

                        <div className="student-profile-detail">

                            <span>
                                Role
                            </span>

                            <strong>
                                {profileData?.role}
                            </strong>

                            <small>
                                Read Only
                            </small>

                        </div>

                    </div>

                </section>

            )}

            {/* ============================================
                EDIT PROFILE SECTION
            ============================================ */}

            {isEditing && (

                <section className="student-profile-edit-card">

                    <div className="student-profile-edit-heading">

                        <div>

                            <span>
                                UPDATE PROFILE
                            </span>

                            <h2>
                                Edit Profile
                            </h2>

                            <p>
                                Update your allowed student details.
                            </p>

                        </div>

                        <button
                            type="button"
                            className="student-profile-close-edit"
                            onClick={handleCancelEdit}
                        >
                            ✕
                        </button>

                    </div>

                    <form
                        className="student-profile-edit-form"
                        onSubmit={handleUpdateProfile}
                    >

                        {/* NAME */}

                        <div className="student-profile-form-group">

                            <label>
                                Full Name
                            </label>

                            <input
                                type="text"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                placeholder="Enter full name"
                                required
                            />

                        </div>

                        {/* EMAIL READ ONLY */}

                        <div className="student-profile-form-group">

                            <label>
                                Email
                            </label>

                            <input
                                type="email"
                                value={
                                    profileData?.email || ""
                                }
                                readOnly
                                disabled
                            />

                            <small>
                                Email cannot be changed.
                            </small>

                        </div>

                        {/* PHONE */}

                        <div className="student-profile-form-group">

                            <label>
                                Phone Number
                            </label>

                            <input
                                type="tel"
                                name="phone"
                                value={formData.phone}
                                onChange={handleChange}
                                placeholder="Enter phone number"
                                maxLength="10"
                                required
                            />

                        </div>

                        {/* ROLL NUMBER */}

                        <div className="student-profile-form-group">

                            <label>
                                Roll Number
                            </label>

                            <input
                                type="text"
                                name="rollNumber"
                                value={formData.rollNumber}
                                onChange={handleChange}
                                placeholder="Enter roll number"
                                required
                            />

                        </div>

                        {/* BRANCH READ ONLY */}

                        <div className="student-profile-form-group">

                            <label>
                                Branch
                            </label>

                            <input
                                type="text"
                                value={
                                    profileData?.branch || ""
                                }
                                readOnly
                                disabled
                            />

                            <small>
                                Branch cannot be changed.
                            </small>

                        </div>

                        {/* SEMESTER SELECT */}

                        <div className="student-profile-form-group">

                            <label>
                                Semester
                            </label>

                            <select
                                name="semester"
                                value={formData.semester}
                                onChange={handleChange}
                                required
                            >

                                <option value="">
                                    Select Semester
                                </option>

                                <option value="1">
                                    Semester 1
                                </option>

                                <option value="2">
                                    Semester 2
                                </option>

                                <option value="3">
                                    Semester 3
                                </option>

                                <option value="4">
                                    Semester 4
                                </option>

                                <option value="5">
                                    Semester 5
                                </option>

                                <option value="6">
                                    Semester 6
                                </option>

                                <option value="7">
                                    Semester 7
                                </option>

                                <option value="8">
                                    Semester 8
                                </option>

                            </select>

                            <small>
                                Timetable will automatically use
                                your updated semester.
                            </small>

                        </div>

                        {/* ROLE READ ONLY */}

                        <div className="student-profile-form-group">

                            <label>
                                Role
                            </label>

                            <input
                                type="text"
                                value={
                                    profileData?.role || ""
                                }
                                readOnly
                                disabled
                            />

                        </div>

                        {/* ACTIONS */}

                        <div className="student-profile-form-actions">

                            <button
                                type="button"
                                className="student-profile-cancel-button"
                                onClick={handleCancelEdit}
                                disabled={isSaving}
                            >
                                Cancel
                            </button>

                            <button
                                type="submit"
                                className="student-profile-save-button"
                                disabled={isSaving}
                            >
                                {isSaving
                                    ? "Updating..."
                                    : "Update Profile"}
                            </button>

                        </div>

                    </form>

                </section>

            )}

            {/* ============================================
                BOTTOM NAVIGATION
            ============================================ */}

            <nav className="student-profile-bottom-nav">

                <button
                    type="button"
                    className="student-profile-nav-item"
                    onClick={goToDashboard}
                >
                    <span>▦</span>
                    Dashboard
                </button>

                <button
                    type="button"
                    className="student-profile-nav-item"
                    onClick={goToAttendance}
                >
                    <span>▣</span>
                    Attendance
                </button>

                <button
                    type="button"
                    className="student-profile-nav-item"
                    onClick={goToTimetable}
                >
                    <span>📅</span>
                    Timetable
                </button>

                <button
                    type="button"
                    className="student-profile-nav-item active"
                    onClick={goToProfile}
                >
                    <span>♙</span>
                    Profile
                </button>

                <button
                    type="button"
                    className="student-profile-nav-item"
                    onClick={goToSettings}
                >
                    <span>⚙</span>
                    Settings
                </button>

                <button
                    type="button"
                    className="student-profile-nav-item student-profile-logout"
                    onClick={handleLogout}
                >
                    <span>↪</span>
                    Logout
                </button>

            </nav>

        </div>
    );
}

export default StudentProfile;

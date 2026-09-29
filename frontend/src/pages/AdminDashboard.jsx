/* eslint-disable react-hooks/set-state-in-effect, react-hooks/exhaustive-deps */

import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./AdminDashboard.css";
import WeeklyTimetableManager from "./WeeklyTimetableManager";

const API = "http://localhost:8080/api/admin";

/* =========================================================
   EMPTY USER
========================================================= */

const emptyUser = {
    name: "",
    email: "",
    phone: "",
    password: "",
    role: "STUDENT",
    department: "",
    rollNumber: "",
    branch: "",
    semester: "",
};

/* =========================================================
   ADMIN DASHBOARD
========================================================= */

function AdminDashboard() {
    const nav = useNavigate();

    const [data, setData] = useState(null);
    const [tab, setTab] = useState("dashboard");
    const [form, setForm] = useState(emptyUser);
    const [error, setError] = useState("");
    const [departments, setDepartments] = useState([]);
    const [branches, setBranches] = useState([]);

    const branchesForDepartment = (departmentName) =>
        branches.filter(
            (branch) =>
                (branch.department?.name || branch.departmentName || "") === departmentName
        );

    const departmentForBranch = (branchName) => {
        const branch = branches.find((item) => item.name === branchName);
        return branch?.department?.name || branch?.departmentName || "";
    };

    const token = localStorage.getItem("accessToken");

    /* =====================================================
       COMMON API CALL
    ===================================================== */

    async function call(path = "/dashboard", options = {}) {
        const response = await fetch(API + path, {
            ...options,

            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
                ...options.headers,
            },
        });

        if (
            response.status === 401 ||
            response.status === 403
        ) {
            localStorage.clear();

            nav("/login");

            throw new Error(
                "Admin login required"
            );
        }

        if (!response.ok) {
            const result = await response
                .json()
                .catch(() => ({}));

            throw new Error(
                result.message ||
                "Operation failed"
            );
        }

        return response.status === 204
            ? null
            : response.json();
    }

    async function loadAcademicStructure() {
        try {
            const [departmentData, branchData] = await Promise.all([
                call("/academic/departments"),
                call("/academic/branches"),
            ]);
            setDepartments(Array.isArray(departmentData) ? departmentData : []);
            setBranches(Array.isArray(branchData) ? branchData : []);
        } catch (e) {
            setError(e.message);
        }
    }

    /* =====================================================
       LOAD DASHBOARD
    ===================================================== */

    async function load() {
        try {
            const result = await call();

            setData(result);
            setError("");
        } catch (e) {
            setError(e.message);
        }
    }

    useEffect(() => {
        load();
        loadAcademicStructure();
    }, []);

    /* =====================================================
       SAVE USER
    ===================================================== */

    async function saveUser(e) {
        e.preventDefault();

        try {
            await call(
                form.id
                    ? `/users/${form.id}`
                    : "/users",
                {
                    method: form.id
                        ? "PUT"
                        : "POST",

                    body:
                        JSON.stringify(form),
                }
            );

            setForm(emptyUser);

            await load();
        } catch (e) {
            setError(e.message);
        }
    }

    /* =====================================================
       DELETE COMMON RECORD
    ===================================================== */

    async function remove(path) {
        if (
            !window.confirm(
                "Are you sure?"
            )
        ) {
            return;
        }

        try {
            await call(path, {
                method: "DELETE",
            });

            await load();
        } catch (e) {
            setError(e.message);
        }
    }

    /* =====================================================
       RESET STUDENT DEVICE
    ===================================================== */

    async function reset(id) {
        try {
            await call(
                `/users/${id}/reset-device`,
                {
                    method: "PATCH",
                }
            );

            await load();
        } catch (e) {
            setError(e.message);
        }
    }

    /* =====================================================
       LOGOUT
    ===================================================== */

    function logout() {
        localStorage.clear();
        nav("/login");
    }

    /* =====================================================
       LOADING
    ===================================================== */

    if (!data) {
        return (
            <div className="admin-loading">
                {error ||
                    "Loading admin control panel..."}
            </div>
        );
    }

    const users = data.users || [];

    const teachers =
        users.filter(
            (user) =>
                user.role === "TEACHER"
        );

    return (
        <div className="admin-shell">

            {/* =================================================
                SIDEBAR
            ================================================= */}

            <aside>

                <h2>
                    <span>SA</span>
                    Admin
                </h2>

                <p>
                    Complete Control Panel
                </p>

                {[
                    "dashboard",
                    "users",
                    "academic",
                    "subjects",
                    "timetables",
                    "weekly-planner",
                    "locations",
                    "teacher-locations",
                    "attendance",
                    "sessions",
                ].map((item) => (
                    <button
                        type="button"
                        key={item}
                        className={
                            tab === item
                                ? "active"
                                : ""
                        }
                        onClick={() =>
                            setTab(item)
                        }
                    >
                        {item === "dashboard"
                            ? "Dashboard"
                            : item === "academic"
                            ? "Academic Structure"
                            : item === "weekly-planner"
                            ? "Weekly Planner"

                            : item === "locations"
                                ? "Student Locations"

                                : item === "teacher-locations"
                                    ? "Teacher Locations"

                                    : item
                                        .charAt(0)
                                        .toUpperCase() +
                                    item.slice(1)}
                    </button>
                ))}

                <button
                    type="button"
                    className="logout"
                    onClick={logout}
                >
                    Logout
                </button>

            </aside>

            {/* =================================================
                MAIN
            ================================================= */}

            <main>

                {/* HEADER */}

                <header>

                    <div>

                        <h1>
                            Administration
                        </h1>

                        <p>
                            Manage the complete
                            attendance system
                        </p>

                    </div>

                    <b>
                        System Administrator
                    </b>

                </header>

                {/* ERROR */}

                {error && (
                    <div className="admin-error">
                        {error}
                    </div>
                )}

                {tab === "dashboard" && (
                    <DashboardHome
                        data={data}
                        departments={departments}
                        branches={branches}
                        onNavigate={setTab}
                    />
                )}

                {tab === "academic" && (
                    <AcademicStructurePanel
                        call={call}
                        departments={departments}
                        branches={branches}
                        reload={loadAcademicStructure}
                    />
                )}

                {/* =================================================
                    USERS
                ================================================= */}

                {tab === "users" && (

                    <section>

                        <h2>
                            User Management
                        </h2>

                        <form
                            className="admin-form"
                            onSubmit={saveUser}
                        >

                            <input
                                type="text"
                                placeholder="Name"
                                value={
                                    form.name || ""
                                }
                                onChange={(e) =>
                                    setForm({
                                        ...form,

                                        name:
                                            e.target
                                                .value,
                                    })
                                }
                                required
                            />

                            <input
                                type="email"
                                placeholder="Email"
                                value={
                                    form.email || ""
                                }
                                onChange={(e) =>
                                    setForm({
                                        ...form,

                                        email:
                                            e.target
                                                .value,
                                    })
                                }
                                required
                            />

                            <input
                                type="text"
                                placeholder="Phone"
                                value={
                                    form.phone || ""
                                }
                                onChange={(e) =>
                                    setForm({
                                        ...form,

                                        phone:
                                            e.target
                                                .value,
                                    })
                                }
                            />

                            <input
                                type="password"
                                placeholder={
                                    form.id
                                        ? "Password (leave blank to keep)"
                                        : "Password"
                                }
                                value={
                                    form.password ||
                                    ""
                                }
                                onChange={(e) =>
                                    setForm({
                                        ...form,

                                        password:
                                            e.target
                                                .value,
                                    })
                                }
                                required={!form.id}
                            />

                            {/* STUDENT ROLL */}

                            {form.role ===
                                "STUDENT" && (

                                    <input
                                        type="text"
                                        placeholder="Roll Number"
                                        value={
                                            form.rollNumber ||
                                            ""
                                        }
                                        onChange={(e) =>
                                            setForm({
                                                ...form,

                                                rollNumber:
                                                    e.target
                                                        .value,
                                            })
                                        }
                                    />

                                )}

                            {/* STUDENT SEMESTER */}

                            {form.role ===
                                "STUDENT" && (

                                    <input
                                        type="number"
                                        min="1"
                                        placeholder="Semester"
                                        value={
                                            form.semester ||
                                            ""
                                        }
                                        onChange={(e) =>
                                            setForm({
                                                ...form,

                                                semester:
                                                    e.target
                                                        .value,
                                            })
                                        }
                                    />

                                )}

                            {/* DEPARTMENT */}

                            <select
                                value={
                                    form.department ||
                                    ""
                                }
                                onChange={(e) =>
                                    setForm({
                                        ...form,

                                        department:
                                            e.target
                                                .value,

                                        branch: "",
                                    })
                                }
                            >

                                <option value="">
                                    Select Department
                                </option>

                                {departments.map((department) => (
                                    <option key={department.id} value={department.name}>
                                        {department.name}
                                    </option>
                                ))}

                            </select>

                            {/* BRANCH ONLY STUDENT */}

                            {form.role ===
                                "STUDENT" && (

                                    <select
                                        value={
                                            form.branch ||
                                            ""
                                        }
                                        onChange={(e) =>
                                            setForm({
                                                ...form,

                                                branch:
                                                    e.target
                                                        .value,
                                            })
                                        }
                                    >

                                        <option value="">
                                            Select Branch
                                        </option>

                                        {branchesForDepartment(form.department).map((branch) => (
                                            <option key={branch.id} value={branch.name}>
                                                {branch.name}
                                            </option>
                                        ))}

                                    </select>

                                )}

                            {/* ROLE */}

                            <select
                                value={form.role}
                                onChange={(e) => {

                                    const role =
                                        e.target.value;

                                    setForm({
                                        ...form,

                                        role,

                                        ...(role !==
                                            "STUDENT"
                                            ? {
                                                branch:
                                                    "",

                                                rollNumber:
                                                    "",

                                                semester:
                                                    "",
                                            }
                                            : {}),
                                    });
                                }}
                            >

                                <option value="STUDENT">
                                    STUDENT
                                </option>

                                <option value="TEACHER">
                                    TEACHER
                                </option>

                                <option value="ADMIN">
                                    ADMIN
                                </option>

                            </select>

                            <button type="submit">

                                {form.id
                                    ? "Update User"
                                    : "Add User"}

                            </button>

                            {form.id && (

                                <button
                                    type="button"
                                    className="secondary"
                                    onClick={() =>
                                        setForm(
                                            emptyUser
                                        )
                                    }
                                >
                                    Cancel
                                </button>

                            )}

                        </form>

                        <Table
                            heads={[
                                "Name",
                                "Email",
                                "Phone",
                                "Role",
                                "Class",
                                "Device",
                                "Actions",
                            ]}
                            rows={users.map(
                                (user) => [

                                    user.name ||
                                    "-",

                                    user.email ||
                                    "-",

                                    user.phone ||
                                    "-",

                                    user.role ||
                                    "-",

                                    user.role ===
                                        "STUDENT"
                                        ? `${user.branch ||
                                        "-"
                                        } / Sem ${user.semester ||
                                        "-"
                                        }`
                                        : user.department ||
                                        "-",

                                    user.deviceLinked
                                        ? "Linked"
                                        : "—",

                                    <div
                                        className="actions"
                                        key={`user-actions-${user.id}`}
                                    >

                                        <button
                                            type="button"
                                            onClick={() =>
                                                setForm({
                                                    ...emptyUser,
                                                    ...user,

                                                    password:
                                                        "",
                                                })
                                            }
                                        >
                                            Edit
                                        </button>

                                        {user.role ===
                                            "STUDENT" && (

                                                <button
                                                    type="button"
                                                    onClick={() =>
                                                        reset(
                                                            user.id
                                                        )
                                                    }
                                                >
                                                    Reset Device
                                                </button>

                                            )}

                                        <button
                                            type="button"
                                            className="danger"
                                            onClick={() =>
                                                remove(
                                                    `/users/${user.id}`
                                                )
                                            }
                                        >
                                            Delete
                                        </button>

                                    </div>,
                                ]
                            )}
                        />

                    </section>

                )}

                {/* =================================================
                    SUBJECTS
                ================================================= */}

                {tab === "subjects" && (

                    <SubjectPanel
                        departments={departments}
                        branches={branches}
                        departmentForBranch={departmentForBranch}
                        data={data}
                        call={call}
                        load={load}
                        remove={remove}
                    />

                )}

                {/* =================================================
                    TIMETABLE
                ================================================= */}

                {tab ===
                    "timetables" && (

                        <TimetablePanel
                            departments={departments}
                            branches={branches}
                            departmentForBranch={departmentForBranch}
                            data={data}
                            teachers={
                                teachers
                            }
                            call={call}
                            load={load}
                            remove={remove}
                        />

                    )}

                {/* =================================================
                    WEEKLY PLANNER
                ================================================= */}

                {tab ===
                    "weekly-planner" && (

                        <WeeklyTimetableManager
                            data={data}
                            call={call}
                            load={load}
                        />

                    )}

                {/* =================================================
                    STUDENT LOCATIONS
                ================================================= */}

                {tab ===
                    "locations" && (

                        <LocationPanel
                            departments={departments}
                            branches={branches}
                            call={call}
                        />

                    )}

                {/* =================================================
                    TEACHER LOCATIONS
                ================================================= */}

                {tab ===
                    "teacher-locations" && (

                        <TeacherLocationPanel
                            departments={departments}
                            call={call}
                        />

                    )}

                {/* =================================================
                    ATTENDANCE
                ================================================= */}

                {tab ===
                    "attendance" && (

                        <section>

                            <h2>
                                Attendance Records
                            </h2>

                            <Table
                                heads={[
                                    "Student",
                                    "Roll",
                                    "Subject",
                                    "Status",
                                    "GPS",
                                    "Time",
                                    "Action",
                                ]}
                                rows={(
                                    data.attendance ||
                                    []
                                ).map(
                                    (
                                        attendance
                                    ) => [

                                            attendance.studentName ||
                                            "-",

                                            attendance.rollNumber ||
                                            "-",

                                            attendance.subject ||
                                            "-",

                                            attendance.status ||
                                            "-",

                                            attendance.geoVerified
                                                ? "Verified"
                                                : "No",

                                            attendance.attendanceTime
                                                ? new Date(
                                                    attendance.attendanceTime
                                                ).toLocaleString()
                                                : "-",

                                            <button
                                                type="button"
                                                className="danger"
                                                key={`attendance-${attendance.id}`}
                                                onClick={() =>
                                                    remove(
                                                        `/attendance/${attendance.id}`
                                                    )
                                                }
                                            >
                                                Delete
                                            </button>,
                                        ]
                                )}
                            />

                        </section>

                    )}

                {/* =================================================
                    SESSIONS
                ================================================= */}

                {tab ===
                    "sessions" && (

                        <section>

                            <h2>
                                Class Sessions
                            </h2>

                            <Table
                                heads={[
                                    "Teacher",
                                    "Subject",
                                    "Class",
                                    "Status",
                                    "Time",
                                    "Actions",
                                ]}
                                rows={(
                                    data.sessions ||
                                    []
                                ).map(
                                    (session) => [

                                        session.teacherName ||
                                        "-",

                                        session.subjectName ||
                                        "-",

                                        `${session.branch ||
                                        "-"
                                        } / Sem ${session.semester ||
                                        "-"
                                        }`,

                                        session.status ||
                                        "-",

                                        `${session.startTime ||
                                        "-"
                                        } – ${session.endTime ||
                                        "-"
                                        }`,

                                        <div
                                            className="actions"
                                            key={`session-${session.id}`}
                                        >

                                            {session.status ===
                                                "LIVE" && (

                                                    <button
                                                        type="button"
                                                        onClick={async () => {
                                                            try {
                                                                await call(
                                                                    `/sessions/${session.id}/complete`,
                                                                    {
                                                                        method:
                                                                            "PATCH",
                                                                    }
                                                                );

                                                                await load();
                                                            } catch (
                                                            e
                                                            ) {
                                                                setError(
                                                                    e.message
                                                                );
                                                            }
                                                        }}
                                                    >
                                                        Complete
                                                    </button>

                                                )}

                                            <button
                                                type="button"
                                                className="danger"
                                                onClick={() =>
                                                    remove(
                                                        `/sessions/${session.id}`
                                                    )
                                                }
                                            >
                                                Delete
                                            </button>

                                        </div>,
                                    ]
                                )}
                            />

                        </section>

                    )}

            </main>

        </div>
    );
}

/* =========================================================
   DASHBOARD HOME
========================================================= */

function DashboardHome({ data, departments, branches, onNavigate }) {
    const users = Array.isArray(data.users) ? data.users : [];
    const overview = [
        ["Total Users", users.length],
        ["Total Students", users.filter((user) => user.role === "STUDENT").length],
        ["Total Teachers", users.filter((user) => user.role === "TEACHER").length],
        ["Total Departments", departments.length],
        ["Total Branches", branches.length],
        ["Total Subjects", Array.isArray(data.subjects) ? data.subjects.length : 0],
        ["Total Timetables", Array.isArray(data.timetables) ? data.timetables.length : 0],
        ["Attendance Records", Array.isArray(data.attendance) ? data.attendance.length : 0],
    ];

    const quickActions = [
        ["Manage Users", "users"],
        ["Academic Structure", "academic"],
        ["Subjects", "subjects"],
        ["Timetables", "timetables"],
        ["Weekly Planner", "weekly-planner"],
        ["Student Locations", "locations"],
        ["Teacher Locations", "teacher-locations"],
        ["Attendance", "attendance"],
        ["Class Sessions", "sessions"],
    ];

    const departmentName = (branch) =>
        branch.department?.name || branch.departmentName || "";

    return (
        <div className="dashboard-home">
            <section className="dashboard-welcome">
                <div>
                    <p className="dashboard-eyebrow">SYSTEM OVERVIEW</p>
                    <h2>Attendance system at a glance</h2>
                    <p>Monitor your people, academic structure, schedules, and attendance from one place.</p>
                </div>
            </section>

            <div className="dashboard-overview-grid">
                {overview.map(([label, value]) => (
                    <article className="dashboard-stat-card" key={label}>
                        <small>{label}</small>
                        <strong>{value}</strong>
                    </article>
                ))}
            </div>

            <section className="dashboard-section">
                <div className="dashboard-section-heading">
                    <div>
                        <p className="dashboard-eyebrow">SHORTCUTS</p>
                        <h2>Quick Actions</h2>
                    </div>
                </div>
                <div className="quick-actions-grid">
                    {quickActions.map(([label, target]) => (
                        <button type="button" key={target} onClick={() => onNavigate(target)}>
                            <span>{label}</span>
                            <span aria-hidden="true">→</span>
                        </button>
                    ))}
                </div>
            </section>

            <section className="dashboard-section academic-summary">
                <div className="dashboard-section-heading">
                    <div>
                        <p className="dashboard-eyebrow">ACADEMICS</p>
                        <h2>Academic Structure Summary</h2>
                    </div>
                    <button type="button" className="dashboard-link" onClick={() => onNavigate("academic")}>Manage structure</button>
                </div>
                {departments.length ? (
                    <div className="department-summary-grid">
                        {departments.map((department) => {
                            const departmentBranches = branches.filter(
                                (branch) => departmentName(branch) === department.name
                            );
                            return (
                                <article key={department.id} className="department-summary-card">
                                    <h3>{department.name}</h3>
                                    {departmentBranches.length ? (
                                        <ul>
                                            {departmentBranches.map((branch) => <li key={branch.id}>{branch.name}</li>)}
                                        </ul>
                                    ) : <p>No branches added yet.</p>}
                                </article>
                            );
                        })}
                    </div>
                ) : <p className="dashboard-empty">No departments have been added yet.</p>}
            </section>
        </div>
    );
}

/* =========================================================
   ACADEMIC STRUCTURE PANEL
========================================================= */

function AcademicStructurePanel({ call, departments, branches, reload }) {
    const [departmentName, setDepartmentName] = useState("");
    const [editingDepartment, setEditingDepartment] = useState(null);
    const [branchName, setBranchName] = useState("");
    const [branchDepartmentId, setBranchDepartmentId] = useState("");
    const [editingBranch, setEditingBranch] = useState(null);
    const [panelError, setPanelError] = useState("");
    const [message, setMessage] = useState("");

    async function saveDepartment(e) {
        e.preventDefault();
        try {
            const editing = Boolean(editingDepartment);
            await call(
                editing ? `/academic/departments/${editingDepartment.id}` : "/academic/departments",
                {
                    method: editing ? "PUT" : "POST",
                    body: JSON.stringify({ name: departmentName.trim() }),
                }
            );
            setDepartmentName("");
            setEditingDepartment(null);
            setPanelError("");
            setMessage(editing ? "Department updated successfully." : "Department added successfully.");
            await reload();
        } catch (e) {
            setPanelError(e.message);
            setMessage("");
        }
    }

    async function deleteDepartment(id) {
        if (!window.confirm("Delete this department?")) return;
        try {
            await call(`/academic/departments/${id}`, { method: "DELETE" });
            setMessage("Department deleted successfully.");
            setPanelError("");
            await reload();
        } catch (e) {
            setPanelError(e.message);
            setMessage("");
        }
    }

    async function saveBranch(e) {
        e.preventDefault();
        try {
            const editing = Boolean(editingBranch);
            await call(
                editing ? `/academic/branches/${editingBranch.id}` : "/academic/branches",
                {
                    method: editing ? "PUT" : "POST",
                    body: JSON.stringify({
                        name: branchName.trim(),
                        departmentId: Number(branchDepartmentId),
                    }),
                }
            );
            setBranchName("");
            setBranchDepartmentId("");
            setEditingBranch(null);
            setPanelError("");
            setMessage(editing ? "Branch updated successfully." : "Branch added successfully.");
            await reload();
        } catch (e) {
            setPanelError(e.message);
            setMessage("");
        }
    }

    async function deleteBranch(id) {
        if (!window.confirm("Delete this branch?")) return;
        try {
            await call(`/academic/branches/${id}`, { method: "DELETE" });
            setMessage("Branch deleted successfully.");
            setPanelError("");
            await reload();
        } catch (e) {
            setPanelError(e.message);
            setMessage("");
        }
    }

    return (
        <section>
            <h2>Academic Structure</h2>
            <p>Add departments first, then add branches under the correct department.</p>

            {panelError && <div className="admin-error">{panelError}</div>}
            {message && <div className="teacher-location-success">{message}</div>}

            <h3>Departments</h3>
            <form className="admin-form" onSubmit={saveDepartment}>
                <input
                    type="text"
                    placeholder="Department Name"
                    value={departmentName}
                    onChange={(e) => setDepartmentName(e.target.value)}
                    required
                />
                <button type="submit">
                    {editingDepartment ? "Update Department" : "Add Department"}
                </button>
                {editingDepartment && (
                    <button type="button" className="secondary" onClick={() => {
                        setEditingDepartment(null);
                        setDepartmentName("");
                    }}>
                        Cancel
                    </button>
                )}
            </form>

            <Table
                heads={["Department", "Actions"]}
                rows={departments.map((department) => [
                    department.name,
                    <div className="actions" key={`department-${department.id}`}>
                        <button type="button" onClick={() => {
                            setEditingDepartment(department);
                            setDepartmentName(department.name);
                        }}>Edit</button>
                        <button type="button" className="danger"
                            onClick={() => deleteDepartment(department.id)}>Delete</button>
                    </div>,
                ])}
            />

            <h3>Branches</h3>
            <form className="admin-form" onSubmit={saveBranch}>
                <select
                    value={branchDepartmentId}
                    onChange={(e) => setBranchDepartmentId(e.target.value)}
                    required
                >
                    <option value="">Select Department</option>
                    {departments.map((department) => (
                        <option key={department.id} value={department.id}>
                            {department.name}
                        </option>
                    ))}
                </select>
                <input
                    type="text"
                    placeholder="Branch Name"
                    value={branchName}
                    onChange={(e) => setBranchName(e.target.value)}
                    required
                />
                <button type="submit">
                    {editingBranch ? "Update Branch" : "Add Branch"}
                </button>
                {editingBranch && (
                    <button type="button" className="secondary" onClick={() => {
                        setEditingBranch(null);
                        setBranchName("");
                        setBranchDepartmentId("");
                    }}>
                        Cancel
                    </button>
                )}
            </form>

            <Table
                heads={["Department", "Branch", "Actions"]}
                rows={branches.map((branch) => [
                    branch.department?.name || branch.departmentName || "—",
                    branch.name,
                    <div className="actions" key={`branch-${branch.id}`}>
                        <button type="button" onClick={() => {
                            setEditingBranch(branch);
                            setBranchName(branch.name);
                            setBranchDepartmentId(String(
                                branch.department?.id || branch.departmentId || ""
                            ));
                        }}>Edit</button>
                        <button type="button" className="danger"
                            onClick={() => deleteBranch(branch.id)}>Delete</button>
                    </div>,
                ])}
            />
        </section>
    );
}

/* =========================================================
   REUSABLE TABLE
========================================================= */

function Table({
    heads,
    rows = [],
}) {
    return (
        <div className="table-wrap">

            <table>

                <thead>

                    <tr>

                        {heads.map(
                            (head) => (

                                <th key={head}>
                                    {head}
                                </th>

                            )
                        )}

                    </tr>

                </thead>

                <tbody>

                    {rows.length ? (

                        rows.map(
                            (
                                row,
                                rowIndex
                            ) => (

                                <tr
                                    key={
                                        rowIndex
                                    }
                                >

                                    {row.map(
                                        (
                                            cell,
                                            cellIndex
                                        ) => (

                                            <td
                                                key={
                                                    cellIndex
                                                }
                                            >
                                                {
                                                    cell
                                                }
                                            </td>

                                        )
                                    )}

                                </tr>

                            )
                        )

                    ) : (

                        <tr>

                            <td
                                colSpan={
                                    heads.length
                                }
                                className="no-records"
                            >
                                No records found
                            </td>

                        </tr>

                    )}

                </tbody>

            </table>

        </div>
    );
}

/* =========================================================
   SUBJECT PANEL
========================================================= */

function SubjectPanel({
    departments,
    branches,
    departmentForBranch,
    data,
    call,
    load,
    remove,
}) {

    const emptySubject = {
        subjectName: "",
        subjectCode: "",
        department: "",
        branch: "",
        semester: "",
    };

    const [form, setForm] =
        useState(emptySubject);

    const [panelError, setPanelError] =
        useState("");

    async function save(e) {
        e.preventDefault();

        try {

            await call(
                form.id
                    ? `/subjects/${form.id}`
                    : "/subjects",
                {
                    method: form.id
                        ? "PUT"
                        : "POST",

                    body: JSON.stringify({
                        ...(form.id ? { id: form.id } : {}),
                        subjectName: form.subjectName,
                        subjectCode: form.subjectCode,
                        branch: form.branch,
                        semester: Number(form.semester),
                    }),
                }
            );

            setForm(emptySubject);
            setPanelError("");

            await load();

        } catch (e) {

            setPanelError(
                e.message
            );
        }
    }

    return (
        <section>

            <h2>
                Subject Management
            </h2>

            {panelError && (
                <div className="admin-error">
                    {panelError}
                </div>
            )}

            <form
                className="admin-form"
                onSubmit={save}
            >

                <input
                    type="text"
                    placeholder="Subject Name"
                    value={
                        form.subjectName
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            subjectName:
                                e.target.value,
                        })
                    }
                    required
                />

                <input
                    type="text"
                    placeholder="Subject Code"
                    value={
                        form.subjectCode
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            subjectCode:
                                e.target.value,
                        })
                    }
                    required
                />

                <select
                    value={form.department || ""}
                    onChange={(e) =>
                        setForm({
                            ...form,
                            department: e.target.value,
                            branch: "",
                        })
                    }
                    required
                >
                    <option value="">Select Department</option>
                    {departments.map((department) => (
                        <option key={department.id} value={department.name}>
                            {department.name}
                        </option>
                    ))}
                </select>

                <select
                    value={
                        form.branch
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            branch:
                                e.target.value,
                        })
                    }
                    required
                >

                    <option value="">
                        Select Branch
                    </option>

                    {branches
                        .filter(
                            (branch) =>
                                (branch.department?.name ||
                                    branch.departmentName ||
                                    "") === form.department
                        )
                        .map((branch) => (
                                            <option key={branch.id} value={branch.name}>
                                                {branch.name}
                                            </option>
                                        ))}

                </select>

                <input
                    type="number"
                    min="1"
                    placeholder="Semester"
                    value={
                        form.semester
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            semester:
                                e.target.value,
                        })
                    }
                    required
                />

                <button type="submit">

                    {form.id
                        ? "Update Subject"
                        : "Add Subject"}

                </button>

                {form.id && (

                    <button
                        type="button"
                        className="secondary"
                        onClick={() =>
                            setForm(
                                emptySubject
                            )
                        }
                    >
                        Cancel
                    </button>

                )}

            </form>

            <Table
                heads={[
                    "Code",
                    "Subject",
                    "Branch",
                    "Semester",
                    "Actions",
                ]}
                rows={(
                    data.subjects || []
                ).map(
                    (subject) => [

                        subject.subjectCode,

                        subject.subjectName,

                        subject.branch,

                        subject.semester,

                        <div
                            className="actions"
                            key={`subject-${subject.id}`}
                        >

                            <button
                                type="button"
                                onClick={() =>
                                    setForm({
                                        ...subject,
                                        department: departmentForBranch(subject.branch),
                                    })
                                }
                            >
                                Edit
                            </button>

                            <button
                                type="button"
                                className="danger"
                                onClick={() =>
                                    remove(
                                        `/subjects/${subject.id}`
                                    )
                                }
                            >
                                Delete
                            </button>

                        </div>,
                    ]
                )}
            />

        </section>
    );
}

/* =========================================================
   TIMETABLE PANEL
========================================================= */

function TimetablePanel({
    departments,
    branches,
    departmentForBranch,
    data,
    teachers,
    call,
    load,
    remove,
}) {

    const emptyTimetable = {
        subjectId: "",
        teacherId: "",
        department: "",
        branch: "",
        semester: "",
        day: "MONDAY",
        startTime: "09:00",
        endTime: "10:00",
    };

    const [form, setForm] =
        useState(emptyTimetable);

    const [panelError, setPanelError] =
        useState("");

    async function save(e) {
        e.preventDefault();

        try {

            await call(
                form.id
                    ? `/timetables/${form.id}`
                    : "/timetables",
                {
                    method: form.id
                        ? "PUT"
                        : "POST",

                    body: JSON.stringify({
                        ...(form.id ? { id: form.id } : {}),
                        subjectId: Number(form.subjectId),
                        teacherId: Number(form.teacherId),
                        branch: form.branch,
                        semester: Number(form.semester),
                        day: form.day,
                        startTime: form.startTime,
                        endTime: form.endTime,
                    }),
                }
            );

            setForm(
                emptyTimetable
            );

            setPanelError("");

            await load();

        } catch (e) {

            setPanelError(
                e.message
            );
        }
    }

    return (
        <section>

            <h2>
                Timetable Management
            </h2>

            {panelError && (
                <div className="admin-error">
                    {panelError}
                </div>
            )}

            <form
                className="admin-form"
                onSubmit={save}
            >

                <select
                    value={
                        form.subjectId
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            subjectId:
                                e.target.value,
                        })
                    }
                    required
                >

                    <option value="">
                        Select Subject
                    </option>

                    {(
                        data.subjects || []
                    ).map(
                        (subject) => (

                            <option
                                value={
                                    subject.id
                                }
                                key={
                                    subject.id
                                }
                            >
                                {
                                    subject.subjectCode
                                }{" "}
                                -{" "}
                                {
                                    subject.subjectName
                                }
                            </option>

                        )
                    )}

                </select>

                <select
                    value={
                        form.teacherId
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            teacherId:
                                e.target.value,
                        })
                    }
                    required
                >

                    <option value="">
                        Select Teacher
                    </option>

                    {teachers.map(
                        (teacher) => (

                            <option
                                value={
                                    teacher.id
                                }
                                key={
                                    teacher.id
                                }
                            >
                                {
                                    teacher.name
                                }
                            </option>

                        )
                    )}

                </select>

                <select
                    value={form.department || ""}
                    onChange={(e) =>
                        setForm({
                            ...form,
                            department: e.target.value,
                            branch: "",
                        })
                    }
                    required
                >
                    <option value="">Select Department</option>
                    {departments.map((department) => (
                        <option key={department.id} value={department.name}>
                            {department.name}
                        </option>
                    ))}
                </select>

                <select
                    value={
                        form.branch
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            branch:
                                e.target.value,
                        })
                    }
                    required
                >

                    <option value="">
                        Select Branch
                    </option>

                    {branches
                        .filter(
                            (branch) =>
                                (branch.department?.name ||
                                    branch.departmentName ||
                                    "") === form.department
                        )
                        .map((branch) => (
                                            <option key={branch.id} value={branch.name}>
                                                {branch.name}
                                            </option>
                                        ))}

                </select>

                <input
                    type="number"
                    min="1"
                    placeholder="Semester"
                    value={
                        form.semester
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            semester:
                                e.target.value,
                        })
                    }
                    required
                />

                <select
                    value={
                        form.day
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            day:
                                e.target.value,
                        })
                    }
                >

                    {[
                        "MONDAY",
                        "TUESDAY",
                        "WEDNESDAY",
                        "THURSDAY",
                        "FRIDAY",
                        "SATURDAY",
                        "SUNDAY",
                    ].map(
                        (day) => (

                            <option
                                key={day}
                                value={day}
                            >
                                {day}
                            </option>

                        )
                    )}

                </select>

                <input
                    type="time"
                    value={
                        form.startTime
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            startTime:
                                e.target.value,
                        })
                    }
                    required
                />

                <input
                    type="time"
                    value={
                        form.endTime
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            endTime:
                                e.target.value,
                        })
                    }
                    required
                />

                <button type="submit">

                    {form.id
                        ? "Update Schedule"
                        : "Add Schedule"}

                </button>

                {form.id && (

                    <button
                        type="button"
                        className="secondary"
                        onClick={() =>
                            setForm(
                                emptyTimetable
                            )
                        }
                    >
                        Cancel
                    </button>

                )}

            </form>

            <Table
                heads={[
                    "Day",
                    "Time",
                    "Subject",
                    "Teacher",
                    "Class",
                    "Actions",
                ]}
                rows={(
                    data.timetables || []
                ).map(
                    (timetable) => [

                        timetable.day,

                        `${timetable.startTime} – ${timetable.endTime}`,

                        timetable.subjectName,

                        timetable.teacherName,

                        `${timetable.branch} / Sem ${timetable.semester}`,

                        <div
                            className="actions"
                            key={`timetable-${timetable.id}`}
                        >

                            <button
                                type="button"
                                onClick={() =>
                                    setForm({
                                        ...timetable,

                                        department: departmentForBranch(timetable.branch),

                                        subjectId:
                                            timetable.subjectId ||
                                            "",

                                        teacherId:
                                            timetable.teacherId ||
                                            "",
                                    })
                                }
                            >
                                Edit
                            </button>

                            <button
                                type="button"
                                className="danger"
                                onClick={() =>
                                    remove(
                                        `/timetables/${timetable.id}`
                                    )
                                }
                            >
                                Delete
                            </button>

                        </div>,
                    ]
                )}
            />

        </section>
    );
}

/* =========================================================
   STUDENT LOCATION MANAGEMENT PANEL

   IMPORTANT:
   Department + Branch based
========================================================= */

function LocationPanel({
    departments,
    branches,
    call,
}) {

    const emptyLocation = {
        placeName: "",
        department: "",
        branch: "",
        latitude: "",
        longitude: "",
        radiusMeters: 100,
    };

    const [form, setForm] =
        useState(emptyLocation);

    const [locations, setLocations] =
        useState([]);

    const [loadingLocations, setLoadingLocations] =
        useState(true);

    const [locationMessage, setLocationMessage] =
        useState("");

    const [
        locationError,
        setLocationError,
    ] = useState("");

    const [
        gettingLocation,
        setGettingLocation,
    ] = useState(false);

    /* =====================================================
       LOAD STUDENT LOCATIONS
    ===================================================== */

    async function loadLocations() {
        try {
            setLoadingLocations(true);

            const result =
                await call("/locations");

            setLocations(
                Array.isArray(result)
                    ? result
                    : result?.locations || []
            );

            setLocationError("");
        } catch (e) {
            setLocationError(e.message);
        } finally {
            setLoadingLocations(false);
        }
    }

    useEffect(() => {
        loadLocations();
    }, []);

    /* =====================================================
       SAVE STUDENT LOCATION
    ===================================================== */

    async function save(e) {
        e.preventDefault();

        setLocationError("");
        setLocationMessage("");

        if (
            !form.placeName ||
            !form.placeName.trim()
        ) {
            setLocationError(
                "Place name is required."
            );

            return;
        }

        if (!form.department) {
            setLocationError(
                "Department is required."
            );

            return;
        }

        if (!form.branch) {
            setLocationError(
                "Branch is required for student attendance location."
            );

            return;
        }

        const latitude =
            Number(form.latitude);

        const longitude =
            Number(form.longitude);

        const radiusMeters =
            Number(
                form.radiusMeters
            );

        if (
            Number.isNaN(latitude) ||
            latitude < -90 ||
            latitude > 90
        ) {
            setLocationError(
                "Please enter a valid latitude between -90 and 90."
            );

            return;
        }

        if (
            Number.isNaN(longitude) ||
            longitude < -180 ||
            longitude > 180
        ) {
            setLocationError(
                "Please enter a valid longitude between -180 and 180."
            );

            return;
        }

        if (
            Number.isNaN(
                radiusMeters
            ) ||
            radiusMeters <= 0
        ) {
            setLocationError(
                "Attendance radius must be greater than 0 meter."
            );

            return;
        }

        try {

            await call(
                form.id
                    ? `/locations/${form.id}`
                    : "/locations",
                {
                    method: form.id
                        ? "PUT"
                        : "POST",

                    body:
                        JSON.stringify({
                            placeName:
                                form.placeName.trim(),

                            department:
                                form.department,

                            branch:
                                form.branch,

                            latitude,

                            longitude,

                            radiusMeters,
                        }),
                }
            );

            setLocationMessage(
                form.id
                    ? "Student attendance location updated successfully."
                    : "Student attendance location added successfully."
            );

            setForm(
                emptyLocation
            );

            setLocationError("");

            await loadLocations();

        } catch (e) {

            setLocationError(
                e.message
            );
        }
    }

    /* =====================================================
       EDIT
    ===================================================== */

    function editLocation(
        location
    ) {
        setForm({
            id:
                location.id,

            placeName:
                location.placeName ||
                "",

            department:
                location.department ||
                "",

            branch:
                location.branch ||
                "",

            latitude:
                location.latitude ??
                "",

            longitude:
                location.longitude ??
                "",

            radiusMeters:
                location.radiusMeters ??
                100,
        });

        setLocationError("");
        setLocationMessage("");
    }

    /* =====================================================
       DELETE STUDENT LOCATION
    ===================================================== */

    async function deleteLocation(id) {
        const confirmed =
            window.confirm(
                "Delete this student attendance location?"
            );

        if (!confirmed) {
            return;
        }

        try {
            await call(
                `/locations/${id}`,
                {
                    method: "DELETE",
                }
            );

            setLocationMessage(
                "Student attendance location deleted successfully."
            );

            setLocationError("");

            if (form.id === id) {
                setForm(emptyLocation);
            }

            await loadLocations();
        } catch (e) {
            setLocationError(e.message);
        }
    }

    /* =====================================================
       CURRENT GPS
    ===================================================== */

    function useCurrentLocation() {

        if (
            !navigator.geolocation
        ) {
            setLocationError(
                "Geolocation is not supported by this browser."
            );

            return;
        }

        setGettingLocation(true);
        setLocationError("");
        setLocationMessage("");

        navigator.geolocation
            .getCurrentPosition(

                (position) => {

                    setForm(
                        (
                            currentForm
                        ) => ({
                            ...currentForm,

                            latitude:
                                position.coords.latitude.toFixed(
                                    7
                                ),

                            longitude:
                                position.coords.longitude.toFixed(
                                    7
                                ),
                        })
                    );

                    setGettingLocation(
                        false
                    );
                },

                (geoError) => {

                    setGettingLocation(
                        false
                    );

                    if (
                        geoError.code ===
                        geoError.PERMISSION_DENIED
                    ) {
                        setLocationError(
                            "Location permission denied. Please allow location access in your browser."
                        );

                    } else if (
                        geoError.code ===
                        geoError.POSITION_UNAVAILABLE
                    ) {
                        setLocationError(
                            "Current location is unavailable."
                        );

                    } else if (
                        geoError.code ===
                        geoError.TIMEOUT
                    ) {
                        setLocationError(
                            "Location request timed out. Please try again."
                        );

                    } else {
                        setLocationError(
                            "Unable to get current location."
                        );
                    }
                },

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

    return (
        <section className="location-section">

            {/* HEADER */}

            <div className="location-heading">

                <div>

                    <h2>
                        Student Attendance Location Management
                    </h2>

                    <p>
                        Configure department and
                        branch based GPS attendance
                        areas for students.
                    </p>

                </div>

                <div className="location-security-badge">
                    Student GPS Security
                </div>

            </div>

            {/* INFO */}

            <div className="location-info">

                <strong>
                    How it works
                </strong>

                <span>
                    Student attendance will only
                    be accepted when the student's
                    department and branch match
                    the configured location and
                    the student's GPS position is
                    inside the allowed radius.
                </span>

            </div>

            {locationError && (

                <div className="admin-error">
                    {locationError}
                </div>

            )}

            {locationMessage && (
                <div className="teacher-location-success">
                    {locationMessage}
                </div>
            )}

            {/* FORM */}

            <form
                className="admin-form location-form"
                onSubmit={save}
            >

                {/* PLACE */}

                <input
                    type="text"
                    placeholder="Place Name - e.g. Academic Building"
                    value={
                        form.placeName
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            placeName:
                                e.target.value,
                        })
                    }
                    required
                />

                {/* DEPARTMENT */}

                <select
                    value={
                        form.department
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            department:
                                e.target.value,

                            branch: "",
                        })
                    }
                    required
                >

                    <option value="">
                        Select Department
                    </option>

                    {departments.map((department) => (
                                    <option key={department.id} value={department.name}>
                                        {department.name}
                                    </option>
                                ))}

                </select>

                {/* BRANCH MANDATORY */}

                <select
                    value={
                        form.branch
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            branch:
                                e.target.value,
                        })
                    }
                    required
                >

                    <option value="">
                        Select Branch
                    </option>

                    {branches
                        .filter(
                            (branch) =>
                                (branch.department?.name ||
                                    branch.departmentName ||
                                    "") === form.department
                        )
                        .map((branch) => (
                                            <option key={branch.id} value={branch.name}>
                                                {branch.name}
                                            </option>
                                        ))}

                </select>

                {/* LATITUDE */}

                <input
                    type="number"
                    step="any"
                    min="-90"
                    max="90"
                    placeholder="Latitude"
                    value={
                        form.latitude
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            latitude:
                                e.target.value,
                        })
                    }
                    required
                />

                {/* LONGITUDE */}

                <input
                    type="number"
                    step="any"
                    min="-180"
                    max="180"
                    placeholder="Longitude"
                    value={
                        form.longitude
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            longitude:
                                e.target.value,
                        })
                    }
                    required
                />

                {/* RADIUS */}

                <select
                    value={
                        form.radiusMeters
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            radiusMeters:
                                e.target.value,
                        })
                    }
                    required
                >

                    <option value="50">
                        50 Meter Radius
                    </option>

                    <option value="100">
                        100 Meter Radius
                    </option>

                    <option value="150">
                        150 Meter Radius
                    </option>

                    <option value="200">
                        200 Meter Radius
                    </option>

                    <option value="300">
                        300 Meter Radius
                    </option>

                    <option value="500">
                        500 Meter Radius
                    </option>

                </select>

                {/* CURRENT LOCATION */}

                <button
                    type="button"
                    className="secondary location-button"
                    onClick={
                        useCurrentLocation
                    }
                    disabled={
                        gettingLocation
                    }
                >
                    {gettingLocation
                        ? "Getting GPS..."
                        : "Use Current Location"}
                </button>

                {/* SAVE */}

                <button
                    type="submit"
                    className="location-button"
                >
                    {form.id
                        ? "Update Student Location"
                        : "Add Student Location"}
                </button>

                {/* CANCEL */}

                {form.id && (

                    <button
                        type="button"
                        className="secondary location-button"
                        onClick={() => {

                            setForm(
                                emptyLocation
                            );

                            setLocationError(
                                ""
                            );

                            setLocationMessage(
                                ""
                            );
                        }}
                    >
                        Cancel
                    </button>

                )}

            </form>

            {/* TABLE */}

            {loadingLocations ? (
                <p className="no-records">
                    Loading student locations...
                </p>
            ) : (
            <Table
                heads={[
                    "Place",
                    "Department",
                    "Branch",
                    "Latitude",
                    "Longitude",
                    "Radius",
                    "Actions",
                ]}
                rows={locations.map(
                    (location) => [

                        location.placeName ||
                        "-",

                        location.department ||
                        "-",

                        location.branch ||
                        "-",

                        location.latitude ??
                        "-",

                        location.longitude ??
                        "-",

                        location.radiusMeters
                            ? `${location.radiusMeters} m`
                            : "-",

                        <div
                            className="actions"
                            key={`location-${location.id}`}
                        >

                            <button
                                type="button"
                                onClick={() =>
                                    editLocation(
                                        location
                                    )
                                }
                            >
                                Edit
                            </button>

                            <button
                                type="button"
                                className="danger"
                                onClick={() =>
                                    deleteLocation(
                                        location.id
                                    )
                                }
                            >
                                Delete
                            </button>

                        </div>,
                    ]
                )}
            />
            )}

        </section>
    );
}

/* =========================================================
   TEACHER LOCATION MANAGEMENT PANEL

   IMPORTANT:
   Department based only.
   NO BRANCH.
========================================================= */

function TeacherLocationPanel({
    departments,
    call,
}) {

    const emptyTeacherLocation = {
        placeName: "",
        department: "",
        latitude: "",
        longitude: "",
        radiusMeters: 100,
    };

    const [
        form,
        setForm,
    ] = useState(
        emptyTeacherLocation
    );

    const [
        locations,
        setLocations,
    ] = useState([]);

    const [
        teacherLocationError,
        setTeacherLocationError,
    ] = useState("");

    const [
        teacherLocationMessage,
        setTeacherLocationMessage,
    ] = useState("");

    const [
        gettingLocation,
        setGettingLocation,
    ] = useState(false);

    const [
        loadingLocations,
        setLoadingLocations,
    ] = useState(true);

    /* =====================================================
       LOAD TEACHER LOCATIONS
    ===================================================== */

    async function loadTeacherLocations() {

        try {

            setLoadingLocations(
                true
            );

            const result =
                await call(
                    "/teacher-locations"
                );

            setLocations(
                Array.isArray(result)
                    ? result
                    : []
            );

            setTeacherLocationError(
                ""
            );

        } catch (e) {

            setTeacherLocationError(
                e.message
            );

        } finally {

            setLoadingLocations(
                false
            );
        }
    }

    useEffect(() => {

        loadTeacherLocations();

    }, []);

    /* =====================================================
       SAVE TEACHER LOCATION
    ===================================================== */

    async function saveTeacherLocation(
        e
    ) {

        e.preventDefault();

        setTeacherLocationError("");
        setTeacherLocationMessage("");

        /* PLACE */

        if (
            !form.placeName ||
            !form.placeName.trim()
        ) {

            setTeacherLocationError(
                "Place name is required."
            );

            return;
        }

        /* DEPARTMENT */

        if (!form.department) {

            setTeacherLocationError(
                "Department is required."
            );

            return;
        }

        /* LOCATION VALUES */

        const latitude =
            Number(
                form.latitude
            );

        const longitude =
            Number(
                form.longitude
            );

        const radiusMeters =
            Number(
                form.radiusMeters
            );

        /* LATITUDE */

        if (
            Number.isNaN(latitude) ||
            latitude < -90 ||
            latitude > 90
        ) {

            setTeacherLocationError(
                "Please enter a valid latitude between -90 and 90."
            );

            return;
        }

        /* LONGITUDE */

        if (
            Number.isNaN(longitude) ||
            longitude < -180 ||
            longitude > 180
        ) {

            setTeacherLocationError(
                "Please enter a valid longitude between -180 and 180."
            );

            return;
        }

        /* RADIUS */

        if (
            Number.isNaN(
                radiusMeters
            ) ||
            radiusMeters <= 0
        ) {

            setTeacherLocationError(
                "Attendance radius must be greater than 0 meter."
            );

            return;
        }

        try {

            /* =============================================
               TEACHER LOCATION PAYLOAD

               NO BRANCH
            ============================================= */

            const payload = {

                placeName:
                    form.placeName
                        .trim(),

                department:
                    form.department,

                latitude,

                longitude,

                radiusMeters,
            };

            await call(
                form.id
                    ? `/teacher-locations/${form.id}`
                    : "/teacher-locations",
                {
                    method:
                        form.id
                            ? "PUT"
                            : "POST",

                    body:
                        JSON.stringify(
                            payload
                        ),
                }
            );

            setTeacherLocationMessage(
                form.id
                    ? "Teacher attendance location updated successfully."
                    : "Teacher attendance location added successfully."
            );

            setForm(
                emptyTeacherLocation
            );

            await loadTeacherLocations();

        } catch (e) {

            setTeacherLocationError(
                e.message
            );
        }
    }

    /* =====================================================
       EDIT
    ===================================================== */

    function editTeacherLocation(
        location
    ) {

        setForm({

            id:
                location.id,

            placeName:
                location.placeName ||
                "",

            department:
                location.department ||
                "",

            latitude:
                location.latitude ??
                "",

            longitude:
                location.longitude ??
                "",

            radiusMeters:
                location.radiusMeters ??
                100,
        });

        setTeacherLocationError("");
        setTeacherLocationMessage("");
    }

    /* =====================================================
       DELETE
    ===================================================== */

    async function deleteTeacherLocation(
        id
    ) {

        const confirmed =
            window.confirm(
                "Delete this teacher attendance location?"
            );

        if (!confirmed) {
            return;
        }

        try {

            await call(
                `/teacher-locations/${id}`,
                {
                    method:
                        "DELETE",
                }
            );

            setTeacherLocationMessage(
                "Teacher attendance location deleted successfully."
            );

            setTeacherLocationError(
                ""
            );

            if (
                form.id === id
            ) {
                setForm(
                    emptyTeacherLocation
                );
            }

            await loadTeacherLocations();

        } catch (e) {

            setTeacherLocationError(
                e.message
            );
        }
    }

    /* =====================================================
       CURRENT GPS
    ===================================================== */

    function useCurrentLocation() {

        if (
            !navigator.geolocation
        ) {

            setTeacherLocationError(
                "Geolocation is not supported by this browser."
            );

            return;
        }

        setGettingLocation(
            true
        );

        setTeacherLocationError(
            ""
        );

        setTeacherLocationMessage(
            ""
        );

        navigator.geolocation
            .getCurrentPosition(

                (position) => {

                    setForm(
                        (
                            currentForm
                        ) => ({
                            ...currentForm,

                            latitude:
                                position.coords.latitude.toFixed(
                                    7
                                ),

                            longitude:
                                position.coords.longitude.toFixed(
                                    7
                                ),
                        })
                    );

                    setGettingLocation(
                        false
                    );
                },

                (geoError) => {

                    setGettingLocation(
                        false
                    );

                    if (
                        geoError.code ===
                        geoError.PERMISSION_DENIED
                    ) {

                        setTeacherLocationError(
                            "Location permission denied. Please allow browser location access."
                        );

                    } else if (
                        geoError.code ===
                        geoError.POSITION_UNAVAILABLE
                    ) {

                        setTeacherLocationError(
                            "Current location is unavailable."
                        );

                    } else if (
                        geoError.code ===
                        geoError.TIMEOUT
                    ) {

                        setTeacherLocationError(
                            "Location request timed out. Please try again."
                        );

                    } else {

                        setTeacherLocationError(
                            "Unable to get current location."
                        );
                    }
                },

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

    /* =====================================================
       UI
    ===================================================== */

    return (
        <section className="location-section">

            {/* HEADER */}

            <div className="location-heading">

                <div>

                    <h2>
                        Teacher Attendance Location Management
                    </h2>

                    <p>
                        Configure department-based
                        GPS locations for teacher
                        self attendance.
                    </p>

                </div>

                <div className="location-security-badge">
                    Teacher GPS Attendance
                </div>

            </div>

            {/* INFO */}

            <div className="location-info">

                <strong>
                    How it works
                </strong>

                <span>
                    Teacher Present button click
                    karne par current GPS location
                    backend par send hogi. Teacher
                    ke department ke registered
                    location aur allowed radius se
                    compare karke backend Present
                    ya Absent decide karega.
                </span>

            </div>

            {/* ERROR */}

            {teacherLocationError && (

                <div className="admin-error">
                    {
                        teacherLocationError
                    }
                </div>

            )}

            {/* SUCCESS */}

            {teacherLocationMessage && (

                <div className="teacher-location-success">
                    {
                        teacherLocationMessage
                    }
                </div>

            )}

            {/* FORM */}

            <form
                className="admin-form location-form"
                onSubmit={
                    saveTeacherLocation
                }
            >

                {/* PLACE */}

                <input
                    type="text"
                    placeholder="Place Name - e.g. Main Academic Building"
                    value={
                        form.placeName
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            placeName:
                                e.target.value,
                        })
                    }
                    required
                />

                {/* DEPARTMENT */}

                <select
                    value={
                        form.department
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            department:
                                e.target.value,
                        })
                    }
                    required
                >

                    <option value="">
                        Select Department
                    </option>

                    {departments.map((department) => (
                                    <option key={department.id} value={department.name}>
                                        {department.name}
                                    </option>
                                ))}

                </select>

                {/* =========================================
                    NO BRANCH FOR TEACHER
                ========================================= */}

                {/* LATITUDE */}

                <input
                    type="number"
                    step="any"
                    min="-90"
                    max="90"
                    placeholder="Latitude"
                    value={
                        form.latitude
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            latitude:
                                e.target.value,
                        })
                    }
                    required
                />

                {/* LONGITUDE */}

                <input
                    type="number"
                    step="any"
                    min="-180"
                    max="180"
                    placeholder="Longitude"
                    value={
                        form.longitude
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            longitude:
                                e.target.value,
                        })
                    }
                    required
                />

                {/* RADIUS */}

                <select
                    value={
                        form.radiusMeters
                    }
                    onChange={(e) =>
                        setForm({
                            ...form,

                            radiusMeters:
                                e.target.value,
                        })
                    }
                    required
                >

                    <option value="50">
                        50 Meter Radius
                    </option>

                    <option value="100">
                        100 Meter Radius
                    </option>

                    <option value="150">
                        150 Meter Radius
                    </option>

                    <option value="200">
                        200 Meter Radius
                    </option>

                    <option value="300">
                        300 Meter Radius
                    </option>

                    <option value="500">
                        500 Meter Radius
                    </option>

                </select>

                {/* CURRENT LOCATION */}

                <button
                    type="button"
                    className="secondary location-button"
                    onClick={
                        useCurrentLocation
                    }
                    disabled={
                        gettingLocation
                    }
                >

                    {gettingLocation
                        ? "Getting GPS..."
                        : "Use Current Location"}

                </button>

                {/* SAVE */}

                <button
                    type="submit"
                    className="location-button"
                >

                    {form.id
                        ? "Update Teacher Location"
                        : "Add Teacher Location"}

                </button>

                {/* CANCEL */}

                {form.id && (

                    <button
                        type="button"
                        className="secondary location-button"
                        onClick={() => {

                            setForm(
                                emptyTeacherLocation
                            );

                            setTeacherLocationError(
                                ""
                            );

                            setTeacherLocationMessage(
                                ""
                            );
                        }}
                    >
                        Cancel
                    </button>

                )}

            </form>

            {/* TABLE */}

            {loadingLocations ? (

                <p className="no-records">
                    Loading teacher locations...
                </p>

            ) : (

                <Table
                    heads={[
                        "Place",
                        "Department",
                        "Latitude",
                        "Longitude",
                        "Radius",
                        "Actions",
                    ]}
                    rows={
                        locations.map(
                            (
                                location
                            ) => [

                                    location.placeName ||
                                    "-",

                                    location.department ||
                                    "-",

                                    location.latitude ??
                                    "-",

                                    location.longitude ??
                                    "-",

                                    location.radiusMeters
                                        ? `${location.radiusMeters} m`
                                        : "-",

                                    <div
                                        className="actions"
                                        key={`teacher-location-${location.id}`}
                                    >

                                        <button
                                            type="button"
                                            onClick={() =>
                                                editTeacherLocation(
                                                    location
                                                )
                                            }
                                        >
                                            Edit
                                        </button>

                                        <button
                                            type="button"
                                            className="danger"
                                            onClick={() =>
                                                deleteTeacherLocation(
                                                    location.id
                                                )
                                            }
                                        >
                                            Delete
                                        </button>

                                    </div>,
                                ]
                        )
                    }
                />

            )}

        </section>
    );
}

export default AdminDashboard;

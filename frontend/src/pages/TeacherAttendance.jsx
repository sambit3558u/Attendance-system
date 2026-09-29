import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./TeacherWorkspace.css";
import TeacherPageShell from "./TeacherPageShell";

function TeacherAttendance() {
    const navigate = useNavigate();
    const [data, setData] = useState(null);
    const [error, setError] = useState("");
    const [history, setHistory] = useState([]);
    const [dateFilter, setDateFilter] = useState("");
    const [monthFilter, setMonthFilter] = useState("");
    const [filtersOpen, setFiltersOpen] = useState(false);

    useEffect(() => {
        fetch("http://localhost:8080/api/teacher/dashboard", {
            headers: { Authorization: `Bearer ${localStorage.getItem("accessToken")}` },
        })
            .then(async (response) => {
                if (!response.ok) throw new Error("Unable to load your attendance.");
                return response.json();
            })
            .then(setData)
            .catch((loadError) => setError(loadError.message));
        fetch("http://localhost:8080/api/teacher/attendance/history", { headers: { Authorization: `Bearer ${localStorage.getItem("accessToken")}` } }).then((response) => response.ok ? response.json() : []).then(setHistory).catch(() => setHistory([]));
    }, []);

    if (!data) return <div className="teacher-workspace-loading">{error || "Loading attendance details..."}</div>;

    const overview = data.attendanceOverview || {};
    const present = Number(overview.presentCount) || 0;
    const absent = Number(overview.absentCount) || 0;
    const marked = Number(overview.totalMarkedDays) || 0;
    const presentPercent = Number(overview.presentPercentage) || 0;
    const absentPercent = Number(overview.absentPercentage) || 0;
    const filteredHistory = history.filter((item) => (!dateFilter || item.date === dateFilter) && (!monthFilter || item.date?.startsWith(monthFilter)));

    return (
        <TeacherPageShell><main className="teacher-workspace">
            <header className="teacher-workspace-header">
                <div><p>TEACHER WORKSPACE</p><h1>My Attendance</h1><span>Your personal attendance record for this month.</span></div>
                <button type="button" onClick={() => navigate("/teacher-dashboard")}>← Dashboard</button>
            </header>
            <section className="teacher-workspace-card teacher-attendance-detail">
                <div className="teacher-attendance-summary"><div className="teacher-attendance-chart" style={{ background: marked ? `conic-gradient(#24b878 0 ${presentPercent}%, #f35c6d ${presentPercent}% 100%)` : "#e6edf5" }}><div><strong>{presentPercent}%</strong><span>Present</span></div></div><div><span className={`teacher-attendance-today ${String(data.teacherAttendanceStatus || "NOT_MARKED").toLowerCase()}`}>{String(data.teacherAttendanceStatus || "NOT_MARKED").replace("_", " ")}</span><h2>This Month&apos;s Attendance</h2><p>Attendance is verified using the registered location for {data.teacher?.department || "your department"}.</p></div></div>
                <div className="teacher-attendance-bars"><article><div><span>Present</span><strong>{present}</strong></div><i><b style={{ width: `${presentPercent}%` }} /></i><small>{presentPercent}% of marked days</small></article><article><div><span>Absent</span><strong>{absent}</strong></div><i className="absent"><b style={{ width: `${absentPercent}%` }} /></i><small>{absentPercent}% of marked days</small></article><article><div><span>Total Marked Days</span><strong>{marked}</strong></div><small>Current month summary</small></article></div>
                <div className="teacher-attendance-history"><div><h2>Date-wise Attendance</h2><button type="button" className="attendance-filter-toggle" onClick={() => setFiltersOpen((open) => !open)}>⚲ Filter {filtersOpen ? "▲" : "▼"}</button>{filtersOpen && <div className="attendance-filter-options"><label className="filter-choice"><input type="checkbox" checked={!!dateFilter} onChange={() => { setDateFilter(""); setMonthFilter(""); }} /> Specific Date</label><label className="filter-choice"><input type="checkbox" checked={!!monthFilter} onChange={() => { setMonthFilter(""); setDateFilter(""); }} /> Month and Year</label><label>Choose Date<input type="date" value={dateFilter} onChange={(event) => { setDateFilter(event.target.value); setMonthFilter(""); }} /></label><label>Choose Month<input type="month" value={monthFilter} onChange={(event) => { setMonthFilter(event.target.value); setDateFilter(""); }} /></label><button type="button" onClick={() => { setDateFilter(""); setMonthFilter(""); }}>Clear</button></div>}</div>{filteredHistory.length ? <div className="teacher-workspace-table"><table><thead><tr><th>Date</th><th>Status</th><th>Location</th><th>Distance</th></tr></thead><tbody>{filteredHistory.map((item) => <tr key={item.date}><td>{item.date}</td><td><b className={`teacher-attendance-today ${item.status.toLowerCase()}`}>{item.status}</b></td><td>{item.placeName}</td><td>{item.distanceMeters} m</td></tr>)}</tbody></table></div> : <p className="teacher-workspace-empty">No attendance record found for this filter.</p>}</div>
            </section>
        </main></TeacherPageShell>
    );
}

export default TeacherAttendance;

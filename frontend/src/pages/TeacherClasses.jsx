import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./TeacherWorkspace.css";
import TeacherPageShell from "./TeacherPageShell";

const API = "http://localhost:8080/api/teacher/dashboard";

function TeacherClasses() {
    const navigate = useNavigate();
    const [data, setData] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        async function load() {
            try {
                const response = await fetch(API, {
                    headers: { Authorization: `Bearer ${localStorage.getItem("accessToken")}` },
                });
                if (!response.ok) throw new Error("Unable to load your classes.");
                setData(await response.json());
            } catch (loadError) {
                setError(loadError.message);
            }
        }
        load();
    }, []);

    if (!data) return <div className="teacher-workspace-loading">{error || "Loading your classes..."}</div>;

    const schedule = Array.isArray(data.todaySchedule) ? data.todaySchedule : [];
    const classes = Array.isArray(data.myClasses) ? data.myClasses : [];

    return (
        <TeacherPageShell><main className="teacher-workspace">
            <header className="teacher-workspace-header">
                <div>
                    <p>TEACHER WORKSPACE</p>
                    <h1>My Classes</h1>
                    <span>See how many classes you have and the complete class details.</span>
                </div>
                <button type="button" onClick={() => navigate("/teacher-dashboard")}>← Dashboard</button>
            </header>

            <section className="teacher-workspace-stats">
                <article><small>Total Classes</small><strong>{data.totalClasses || 0}</strong><span>Full academic year</span></article>
                <article><small>Classes Today</small><strong>{data.classesToday || 0}</strong><span>Today&apos;s schedule</span></article>
                <article><small>Completed Classes</small><strong>{classes.length}</strong><span>Available class records</span></article>
            </section>

            <section className="teacher-workspace-card">
                <h2>Today&apos;s Classes</h2>
                {schedule.length ? <div className="teacher-class-list">{schedule.map((item) => (
                    <article key={item.id}>
                        <time>{item.time}</time>
                        <div><strong>{item.subject}</strong><span>{item.branch} · Semester {item.semester}</span></div>
                        <b className={`teacher-status ${String(item.status).toLowerCase()}`}>{item.status}</b>
                    </article>
                ))}</div> : <p className="teacher-workspace-empty">No classes scheduled for today.</p>}
            </section>

            <section className="teacher-workspace-card">
                <h2>Completed Class Details</h2>
                {classes.length ? <div className="teacher-workspace-table"><table><thead><tr><th>Subject</th><th>Branch / Semester</th><th>Students</th><th>Schedule</th></tr></thead><tbody>{classes.map((item) => <tr key={item.id}><td>{item.className || "-"}</td><td>{item.course || "-"}</td><td>{item.studentCount ?? 0}</td><td>{item.schedule || "-"}</td></tr>)}</tbody></table></div> : <p className="teacher-workspace-empty">No completed class details are available yet.</p>}
            </section>
        </main></TeacherPageShell>
    );
}

export default TeacherClasses;

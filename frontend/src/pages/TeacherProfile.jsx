import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import TeacherPageShell from "./TeacherPageShell";
import "./TeacherWorkspace.css";

export default function TeacherProfile() {
    const navigate = useNavigate();
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    const [edit, setEdit] = useState(false);
    const [form, setForm] = useState(() => JSON.parse(localStorage.getItem("teacherProfile") || JSON.stringify({ name: user.name || "", phone: "", department: "" })));
    useEffect(() => { fetch("http://localhost:8080/api/teacher/dashboard", { headers: { Authorization: `Bearer ${localStorage.getItem("accessToken")}` } }).then(r => r.ok ? r.json() : null).then(d => d && setForm(x => ({ ...x, name: x.name || d.teacher?.name || "", department: x.department || d.teacher?.department || "" }))).catch(() => {}); }, []);
    const save = e => { e.preventDefault(); localStorage.setItem("teacherProfile", JSON.stringify(form)); setEdit(false); };
    return <TeacherPageShell><main className="teacher-workspace"><header className="teacher-workspace-header"><div><p>TEACHER WORKSPACE</p><h1>Profile</h1><span>Manage your personal and professional details.</span><button type="button" className="qr-back-dashboard" onClick={() => navigate("/teacher-dashboard")}>← Back to Dashboard</button></div></header><section className="teacher-workspace-card"><h2>My Profile</h2>{!edit ? <div className="profile-details"><p><b>Full Name</b><span>{form.name || "-"}</span></p><p><b>Email ID</b><span>{user.email || "-"}</span></p><p><b>Phone Number</b><span>{form.phone || "Not added"}</span></p><p><b>Department</b><span>{form.department || user.department || "Not assigned"}</span></p><button onClick={() => setEdit(true)}>Edit Profile</button></div> : <form className="teacher-create-form" onSubmit={save}><label>Full Name<input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} /></label><label>Email ID<input value={user.email || "-"} readOnly /></label><label>Phone Number<input value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} /></label><label>Department<input value={form.department} onChange={e => setForm({ ...form, department: e.target.value })} /></label><button>Save Profile</button></form>}</section></main></TeacherPageShell>;
}

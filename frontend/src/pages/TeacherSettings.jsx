import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TeacherPageShell from "./TeacherPageShell";
import "./TeacherWorkspace.css";

export default function TeacherSettings() {
    const navigate = useNavigate();
    const [settings, setSettings] = useState(() => JSON.parse(localStorage.getItem("teacherSettings") || '{"classReminders":true,"attendanceUpdates":true,"reports":true,"browser":false}'));
    const items = [["classReminders", "Class reminders", "Upcoming class schedule alerts"], ["attendanceUpdates", "Attendance updates", "Live class and attendance status alerts"], ["reports", "Reports ready", "Monthly report availability alerts"], ["browser", "Browser notifications", "Allow browser notification prompts"]];
    const save = () => { localStorage.setItem("teacherSettings", JSON.stringify(settings)); alert("Settings saved successfully."); };
    return <TeacherPageShell><main className="teacher-workspace"><header className="teacher-workspace-header"><div><p>TEACHER WORKSPACE</p><h1>Settings</h1><span>Choose which notifications you want to receive.</span><button type="button" className="qr-back-dashboard" onClick={() => navigate("/teacher-dashboard")}>← Back to Dashboard</button></div></header><section className="teacher-workspace-card teacher-settings-card"><h2>Notification Preferences</h2>{items.map(([key, title, text]) => <label className="setting-toggle" key={key}><span><strong>{title}</strong><small>{text}</small></span><input type="checkbox" checked={settings[key]} onChange={e => setSettings({ ...settings, [key]: e.target.checked })} /><i /></label>)}<button className="save-settings" onClick={save}>Save Settings</button></section></main></TeacherPageShell>;
}

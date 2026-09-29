import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./TeacherWorkspace.css";

const api = "http://localhost:8080/api";

function TeacherCreateClass() {
    const navigate = useNavigate();
    const [branches, setBranches] = useState([]);
    const [subjects, setSubjects] = useState([]);
    const [locations, setLocations] = useState([]);
    const [form, setForm] = useState({ branch: "", semester: "", subject: "", locationId: "", startTime: "", endTime: "" });
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [saving, setSaving] = useState(false);

    const tokenHeaders = useMemo(() => ({ Authorization: `Bearer ${localStorage.getItem("accessToken")}` }), []);

    useEffect(() => {
        fetch(`${api}/academic/branches`)
            .then(async (response) => {
                if (!response.ok) throw new Error("Branches could not be loaded. Please restart the backend.");
                return response.json();
            })
            .then((items) => setBranches(Array.isArray(items) ? items : []))
            .catch((loadError) => setError(loadError.message));
    }, []);

    useEffect(() => {
        if (!form.branch || !form.semester) {
            setSubjects([]);
            setLocations([]);
            return;
        }
        const params = new URLSearchParams({ branch: form.branch, semester: form.semester });
        fetch(`${api}/subjects?${params}`)
            .then(async (response) => {
                if (!response.ok) throw new Error("Subjects could not be loaded.");
                return response.json();
            })
            .then((items) => setSubjects(Array.isArray(items) ? items : []))
            .catch((loadError) => setError(loadError.message));

        fetch(`${api}/teacher/class/locations?branch=${encodeURIComponent(form.branch)}`, { headers: tokenHeaders })
            .then(async (response) => {
                if (!response.ok) throw new Error("Attendance locations could not be loaded.");
                return response.json();
            })
            .then((items) => setLocations(Array.isArray(items) ? items : []))
            .catch((loadError) => setError(loadError.message));
    }, [form.branch, form.semester, tokenHeaders]);

    const update = (field, value) => {
        setError("");
        setMessage("");
        setForm((current) => ({
            ...current,
            [field]: value,
            ...(field === "branch" ? { semester: "", subject: "", locationId: "" } : {}),
            ...(field === "semester" ? { subject: "", locationId: "" } : {}),
        }));
    };

    const submit = async (event) => {
        event.preventDefault();
        setSaving(true);
        setError("");
        try {
            const response = await fetch(`${api}/teacher/class/start`, {
                method: "POST",
                headers: { "Content-Type": "application/json", ...tokenHeaders },
                body: JSON.stringify({ subjectName: form.subject, branch: form.branch, semester: Number(form.semester), attendanceLocationId: Number(form.locationId), startTime: form.startTime, endTime: form.endTime }),
            });
            const result = await response.json().catch(() => ({}));
            if (!response.ok) throw new Error(result.message || "Class could not be created.");
            setMessage("Class created and attendance session started successfully.");
            setForm((current) => ({ ...current, subject: "", locationId: "" }));
        } catch (submitError) {
            setError(submitError.message);
        } finally {
            setSaving(false);
        }
    };

    return <main className="teacher-workspace"><header className="teacher-workspace-header"><div><p>TEACHER WORKSPACE</p><h1>Create Class</h1><span>Any logged-in teacher can create a class. Choose Branch, then Semester, then Subject.</span></div><button type="button" onClick={() => navigate("/teacher-dashboard")}>← Dashboard</button></header><section className="teacher-workspace-card"><h2>Create a Class Session</h2><p className="teacher-workspace-note">This class will be recorded under your account. It does not require the scheduled teacher to be absent.</p>{error && <div className="teacher-workspace-error">{error}</div>}{message && <div className="teacher-workspace-success">{message}</div>}<form className="teacher-create-form" onSubmit={submit}><label>1. Branch<select value={form.branch} onChange={(event) => update("branch", event.target.value)} required><option value="">Select Branch</option>{branches.map((branch) => <option key={branch.id} value={branch.name}>{branch.name}</option>)}</select></label><label>2. Semester<select value={form.semester} onChange={(event) => update("semester", event.target.value)} disabled={!form.branch} required><option value="">Select Semester</option>{[1, 2, 3, 4, 5, 6, 7, 8].map((semester) => <option key={semester} value={semester}>Semester {semester}</option>)}</select></label><label>3. Subject<select value={form.subject} onChange={(event) => update("subject", event.target.value)} disabled={!form.semester} required><option value="">Select Subject</option>{subjects.map((subject) => <option key={subject.id} value={subject.subjectName}>{subject.subjectCode ? `${subject.subjectCode} — ` : ""}{subject.subjectName}</option>)}</select></label><label>Attendance Location<select value={form.locationId} onChange={(event) => update("locationId", event.target.value)} disabled={!form.semester} required><option value="">Select Location</option>{locations.map((location) => <option key={location.id} value={location.id}>{location.placeName}</option>)}</select></label><label>Start Time<input type="time" value={form.startTime} onChange={(event) => update("startTime", event.target.value)} required /></label><label>End Time<input type="time" value={form.endTime} onChange={(event) => update("endTime", event.target.value)} required /></label><button type="submit" disabled={saving}>{saving ? "Creating Class..." : "Create Class & Start Attendance"}</button></form></section></main>;
}

export default TeacherCreateClass;

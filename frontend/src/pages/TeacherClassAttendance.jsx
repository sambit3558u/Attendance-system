import { useCallback, useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import QRCode from "qrcode";
import TeacherPageShell from "./TeacherPageShell";
import "./TeacherWorkspace.css";

const API = "http://localhost:8080/api/teacher/attendance";

async function responseData(response) {
    const text = await response.text();
    if (!text) return {};
    try {
        return JSON.parse(text);
    } catch {
        return { message: text };
    }
}

function requestError(data, response, fallback) {
    if (response.status === 401 || response.status === 403) {
        return "Teacher login expired or this class belongs to another teacher. Please log in again and open the active class from My Classes.";
    }
    return data.message || data.error || `${fallback} (${response.status})`;
}

export default function TeacherClassAttendance() {
    const { sessionId } = useParams();
    const navigate = useNavigate();
    const [qr, setQr] = useState("");
    const [seconds, setSeconds] = useState(15);
    const [students, setStudents] = useState([]);
    const [error, setError] = useState("");
    const [generating, setGenerating] = useState(true);
    const token = localStorage.getItem("accessToken");
    const headers = useMemo(() => ({
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
    }), [token]);

    const loadStudents = useCallback(async () => {
        const response = await fetch(`${API}/${sessionId}/students`, { headers });
        const data = await responseData(response);
        if (!response.ok) {
            throw new Error(requestError(data, response, "Unable to load students"));
        }
        setStudents(Array.isArray(data) ? data : []);
    }, [headers, sessionId]);

    const generateQr = useCallback(async () => {
        if (!token) throw new Error("Login session missing. Please log in again.");
        setGenerating(true);
        try {
            const response = await fetch(`${API}/${sessionId}/qr`, {
                method: "POST",
                headers,
                body: "{}",
            });
            const data = await responseData(response);
            if (!response.ok) {
                throw new Error(requestError(data, response, "Unable to generate QR"));
            }
            const image = await QRCode.toDataURL(
                JSON.stringify({ type: "GEO_ATTEND_QR", token: data.token }),
                { width: 320, margin: 2, errorCorrectionLevel: "M" }
            );
            setQr(image);
            setSeconds(Number(data.validForSeconds) || 15);
            setError("");
        } finally {
            setGenerating(false);
        }
    }, [headers, sessionId, token]);

    useEffect(() => {
        const start = window.setTimeout(() => {
            generateQr().catch((requestError) => setError(requestError.message));
            loadStudents().catch((requestError) => setError(requestError.message));
        }, 0);
        const rotate = window.setInterval(
            () => generateQr().catch((requestError) => setError(requestError.message)), 15000
        );
        const refresh = window.setInterval(() => loadStudents().catch(() => {}), 4000);
        const tick = window.setInterval(
            () => setSeconds((value) => (value <= 1 ? 15 : value - 1)), 1000
        );
        return () => {
            window.clearTimeout(start);
            window.clearInterval(rotate);
            window.clearInterval(refresh);
            window.clearInterval(tick);
        };
    }, [generateQr, loadStudents]);

    const markPresent = async (studentId) => {
        const response = await fetch(`${API}/${sessionId}/students/${studentId}/present`, {
            method: "PATCH",
            headers,
            body: "{}",
        });
        const data = await responseData(response);
        if (!response.ok) {
            setError(data.message || data.error || "Unable to mark student present");
            return;
        }
        setError("");
        await loadStudents();
    };

    return (
        <TeacherPageShell>
            <main className="teacher-workspace">
                <header className="teacher-workspace-header">
                    <div>
                        <p>LIVE ATTENDANCE</p>
                        <h1>Class QR &amp; Students</h1>
                        <span>QR automatically regenerates every 15 seconds.</span>
                        <button type="button" className="qr-back-dashboard" onClick={() => navigate("/teacher-dashboard")}>← Back to Dashboard</button>
                    </div>
                </header>
                {error && (
                    <div className="teacher-workspace-error">
                        <span>{error}</span>
                        <button type="button" onClick={() => generateQr().catch((requestError) => setError(requestError.message))}>
                            Retry QR
                        </button>
                    </div>
                )}
                <div className="qr-attendance-grid">
                    <section className="teacher-workspace-card qr-display">
                        <h2>Show QR</h2>
                        {generating && !qr && <div className="qr-loading">Generating secure QR…</div>}
                        {qr && <img src={qr} alt="Live class attendance QR" />}
                        {qr && <strong>Refreshes in {seconds}s</strong>}
                        <small>Students must scan while logged in. GPS is skipped only for a valid live QR.</small>
                    </section>
                    <section className="teacher-workspace-card">
                        <h2>Student Attendance</h2>
                        <div className="teacher-workspace-table">
                            <table>
                                <thead><tr><th>Roll</th><th>Student</th><th>Status</th><th>Action</th></tr></thead>
                                <tbody>
                                    {students.map((student) => (
                                        <tr key={student.studentId}>
                                            <td data-label="Roll">{student.rollNumber && !String(student.rollNumber).includes("@") ? student.rollNumber : "—"}</td><td data-label="Student">{student.name}</td><td data-label="Status">{student.status}</td>
                                            <td data-label="Action">{student.status !== "PRESENT" && (
                                                <button className="manual-present-btn" onClick={() => markPresent(student.studentId)}>Mark Present</button>
                                            )}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </section>
                </div>
            </main>
        </TeacherPageShell>
    );
}

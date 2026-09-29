import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Login.css";

export default function ForgotPassword() {
    const navigate = useNavigate();
    const [step, setStep] = useState(1);
    const [email, setEmail] = useState("");
    const [token, setToken] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [busy, setBusy] = useState(false);

    const submit = async (event) => {
        event.preventDefault(); setBusy(true); setError(""); setMessage("");
        try {
            const body = step === 1 ? { email: email.trim() } : { token, newPassword: password, confirmPassword };
            const endpoint = step === 1 ? "forgot-password" : "reset-password";
            const response = await fetch(`http://localhost:8080/api/auth/${endpoint}`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) });
            const data = await response.json();
            if (!response.ok) throw new Error(data.message || "Password reset failed");
            if (step === 1) { setToken(data.resetToken || ""); setMessage(`${data.message} Paste the reset token below.`); setStep(2); }
            else { setMessage(data.message); setTimeout(() => navigate("/login"), 900); }
        } catch (requestError) { setError(requestError.message); } finally { setBusy(false); }
    };

    return <div className="login-page"><main className="login-main forgot-password-main"><section className="login-card forgot-password-card">
        <div className="login-card-heading"><span>ACCOUNT RECOVERY</span><h2>Forgot Password</h2><p>{step === 1 ? "Choose your role and request a reset token." : "Set a new password for your account."}</p></div>
        <form className="login-form" onSubmit={submit}>
            {step === 1 ? <div className="form-group"><label>Email ID</label><input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required /></div> : <><div className="form-group"><label>Reset Token</label><input value={token} onChange={(event) => setToken(event.target.value)} required /></div><div className="form-group"><label>New Password</label><input type="password" minLength={6} value={password} onChange={(event) => setPassword(event.target.value)} required /></div><div className="form-group"><label>Confirm Password</label><input type="password" minLength={6} value={confirmPassword} onChange={(event) => setConfirmPassword(event.target.value)} required /></div></>}
            {message && <div className="login-success-message">{message}</div>}{error && <div className="login-error-message">{error}</div>}
            <button className="login-submit-button" disabled={busy}>{busy ? "Please wait..." : step === 1 ? "Request Reset Token" : "Reset Password"}</button>
            <button type="button" className="back-home-button" onClick={() => navigate("/login")}>← Back to Login</button>
        </form>
    </section></main></div>;
}

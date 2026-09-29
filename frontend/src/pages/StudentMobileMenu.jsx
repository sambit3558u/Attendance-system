import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { createPortal } from "react-dom";

const items = [
  ["Dashboard", "/student-dashboard", "▦"],
  ["Attendance", "/student/attendance", "☑"],
  ["Timetable", "/student/timetable", "▤"],
  ["Scan QR", "/student/attendance/scan-qr", "▣"],
  ["Profile", "/student/profile", "♙"],
  ["Notifications", "/student/notifications", "🔔"],
];

export default function StudentMobileMenu() {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const go = (path) => { setOpen(false); navigate(path); };
  const logout = () => {
    ["accessToken", "refreshToken", "user"].forEach((key) => localStorage.removeItem(key));
    navigate("/login");
  };
  const menu = <>
    <button type="button" className="student-mobile-menu-toggle" onClick={() => setOpen(true)} aria-label="Open navigation">☰</button>
    <div className={`student-mobile-menu-backdrop${open ? " open" : ""}`} onClick={() => setOpen(false)} />
    <aside className={`student-mobile-menu-panel${open ? " open" : ""}`} aria-hidden={!open}>
      <div className="student-mobile-menu-head"><strong>GEO-ATTEND</strong><button type="button" onClick={() => setOpen(false)} aria-label="Close navigation">×</button></div>
      <div className="student-mobile-menu-links">
        {items.map(([label, path, icon]) => <button key={path} type="button" className={location.pathname === path ? "active" : ""} onClick={() => go(path)}><span>{icon}</span>{label}</button>)}
        <button type="button" className="logout" onClick={logout}><span>⇥</span>Logout</button>
      </div>
    </aside>
  </>;
  return createPortal(menu, document.body);
}

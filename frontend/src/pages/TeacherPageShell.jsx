import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./TeacherDashboard.css";
import "./TeacherWorkspace.css";
export default function TeacherPageShell({ children }) {
 const nav=useNavigate(),{pathname}=useLocation(),[loading,setLoading]=useState(false),user=JSON.parse(localStorage.getItem("user")||"{}");
 const links=[["▦ Dashboard","/teacher-dashboard"],["▣ My Classes","/teacher/classes"],["☑ Attendance","/teacher/attendance"],["✎ Create Class","/teacher/create-class"],["▤ Reports","/teacher/reports"],["📅 Calendar","/teacher/calendar"],["⚙ Settings","/teacher/settings"],["♙ Profile","/teacher/profile"]];
 const go=path=>{if(path===pathname)return;setLoading(true);setTimeout(()=>{nav(path);setLoading(false)},350)};
 return <div className="teacher-dashboard">{loading&&<div className="teacher-nav-loader"><div><i>◌</i><strong>Loading workspace...</strong></div></div>}<aside className="sidebar"><div className="logo"><div className="logo-icon">🌐</div><div><h2>GEO-ATTEND</h2><p>Smart Attendance System</p></div></div><nav className="menu">{links.map(([label,path])=><button type="button" key={path} className={pathname===path?"menu-item active":"menu-item"} onClick={()=>go(path)}>{label}</button>)}<button type="button" className="menu-item logout" onClick={()=>{localStorage.clear();nav("/login")}}>⇥ Logout</button></nav><div className="teacher-mini-profile"><div className="profile-avatar">{user.name?.[0]?.toUpperCase()||"T"}</div><div><h4>{user.name||"Teacher"}</h4><p>{user.department||"Department"}</p><span>Teacher</span></div></div></aside><main className="dashboard-main teacher-page-content">{children}</main></div>;
}

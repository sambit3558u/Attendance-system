import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";

import TeacherDashboard from "./pages/TeacherDashboard";
import TeacherClasses from "./pages/TeacherClasses";
import TeacherAttendance from "./pages/TeacherAttendance";
import TeacherCreateClass from "./pages/TeacherCreateClass";
import TeacherExtraPages from "./pages/TeacherExtraPages";
import TeacherPageShell from "./pages/TeacherPageShell";
import TeacherSettings from "./pages/TeacherSettings";
import TeacherProfile from "./pages/TeacherProfile";
import TeacherClassAttendance from "./pages/TeacherClassAttendance";
import StudentQrAttendance from "./pages/StudentQrAttendance";
import StudentDashboard from "./pages/StudentDashboard";
import AdminDashboard from "./pages/AdminDashboard";

/*
 * Student Pages
 */
import StudentAttendance from "./pages/StudentAttendance";
import StudentTimetable from "./pages/StudentTimetable";
import StudentProfile from "./pages/StudentProfile";
import StudentSettings from "./pages/StudentSettings";
import StudentNotifications from "./pages/StudentNotifications";

import NotFound from "./pages/NotFound";
import "./Responsive.css";

function App() {
  return (
    <BrowserRouter>

      <Routes>

        {/* =====================================
                    PUBLIC PAGES
                ===================================== */}

        <Route
          path="/"
          element={<Home />}
        />

        <Route
          path="/login"
          element={<Login />}
        />

        <Route
          path="/register"
          element={<Register />}
        />
        <Route path="/forgot-password" element={<ForgotPassword />} />

        {/* =====================================
                    TEACHER
                ===================================== */}

        <Route
          path="/admin-dashboard"
          element={<AdminDashboard />}
        />

        <Route
          path="/teacher-dashboard"
          element={<TeacherDashboard />}
        />

        <Route path="/teacher/classes" element={<TeacherClasses />} />
        <Route path="/teacher/attendance" element={<TeacherAttendance />} />
        <Route path="/teacher/create-class" element={<TeacherPageShell><TeacherCreateClass /></TeacherPageShell>} />
        <Route path="/teacher/reports" element={<TeacherPageShell><TeacherExtraPages /></TeacherPageShell>} />
        <Route path="/teacher/calendar" element={<TeacherPageShell><TeacherExtraPages /></TeacherPageShell>} />
        <Route path="/teacher/settings" element={<TeacherSettings />} />
        <Route path="/teacher/profile" element={<TeacherProfile />} />
        <Route path="/teacher/attendance/:sessionId" element={<TeacherClassAttendance />} />

        {/* =====================================
                    STUDENT DASHBOARD
                ===================================== */}

        <Route
          path="/student-dashboard"
          element={<StudentDashboard />}
        />

        {/* =====================================
                    STUDENT ATTENDANCE
                ===================================== */}

        <Route
          path="/student/attendance"
          element={<StudentAttendance />}
        />
        <Route path="/student/attendance/scan-qr" element={<StudentQrAttendance />} />

        {/* =====================================
                    STUDENT TIMETABLE
                ===================================== */}

        <Route
          path="/student/timetable"
          element={<StudentTimetable />}
        />

        {/* =====================================
                    STUDENT PROFILE
                ===================================== */}

        <Route
          path="/student/profile"
          element={<StudentProfile />}
        />

        {/* =====================================
                    STUDENT SETTINGS
                ===================================== */}

        <Route
          path="/student/settings"
          element={<StudentSettings />}
        />
        {/* =====================================
                    STUDENT NOTIFICATIONS
                ===================================== */}
        <Route
          path="/student/notifications"
          element={<StudentNotifications />}
        />

        {/* =====================================
                    404 - PAGE NOT FOUND
                ===================================== */}

        <Route
          path="*"
          element={<NotFound />}
        />

      </Routes>

    </BrowserRouter>
  );
}

export default App;

import { useState } from "react";

const DAYS = [
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
];

const BRANCHES = [
    {
        value: "Aeronautical Engineering",
        label: "Aeronautical Engineering",
    },
    {
        value: "Aerospace Engineering",
        label: "Aerospace Engineering",
    },
    {
        value: "Agricultural Engineering",
        label: "Agricultural Engineering",
    },
    {
        value: "Automobile Engineering",
        label: "Automobile Engineering",
    },
    {
        value: "Bachelor of Business Administration",
        label: "Bachelor of Business Administration (BBA)",
    },
    {
        value: "Bachelor of Computer Applications",
        label: "Bachelor of Computer Applications (BCA)",
    },
    {
        value: "Biomedical Engineering",
        label: "Biomedical Engineering",
    },
    {
        value: "Biotechnology",
        label: "Biotechnology",
    },
    {
        value: "Chemical Engineering",
        label: "Chemical Engineering",
    },
    {
        value: "Civil Engineering",
        label: "Civil Engineering",
    },
    {
        value: "Computer Science & Engineering",
        label: "Computer Science & Engineering (CSE)",
    },
    {
        value: "Computer Science & Engineering - AI and ML",
        label: "Computer Science & Engineering (AI & ML)",
    },
    {
        value: "Computer Science & Engineering - Data Science",
        label: "Computer Science & Engineering (Data Science)",
    },
    {
        value: "Electrical & Electronics Engineering",
        label: "Electrical & Electronics Engineering",
    },
    {
        value: "Electrical Engineering",
        label: "Electrical Engineering",
    },
    {
        value: "Electronics & Communication Engineering",
        label: "Electronics & Communication Engineering",
    },
    {
        value: "Food Technology",
        label: "Food Technology",
    },
    {
        value: "Information Technology",
        label: "Information Technology",
    },
    {
        value: "Master of Business Administration",
        label: "Master of Business Administration (MBA)",
    },
    {
        value: "Master of Computer Applications",
        label: "Master of Computer Applications (MCA)",
    },
    {
        value: "Mechanical Engineering",
        label: "Mechanical Engineering",
    },
    {
        value: "Mining Engineering",
        label: "Mining Engineering",
    },
    {
        value: "Petroleum Engineering",
        label: "Petroleum Engineering",
    },
    {
        value: "Textile Engineering",
        label: "Textile Engineering",
    },
];

const blank = {
    id: null,
    subjectId: "",
    teacherId: "",
    day: "MONDAY",
    startTime: "09:00",
    endTime: "10:00",
};

export default function WeeklyTimetableManager({
    data,
    call,
    load,
}) {
    const [course, setCourse] = useState("");
    const [semester, setSemester] = useState("1");

    const [form, setForm] = useState(blank);

    const [message, setMessage] = useState("");

    /*
     * SUBJECT MANAGEMENT me add hua subject
     * yahan data.subjects se aa raha hai.
     *
     * Selected Branch + Semester ke according
     * subjects filter honge.
     */
    const subjects = data.subjects.filter(
        (subject) =>
            subject.branch === course &&
            String(subject.semester) === String(semester)
    );

    /*
     * Selected subject ki complete information.
     */
    const selectedSubject = data.subjects.find(
        (subject) =>
            String(subject.id) === String(form.subjectId)
    );

    /*
     * Saare teachers
     */
    const teachers = data.users.filter(
        (user) => user.role === "TEACHER"
    );

    /*
     * Selected Branch + Semester timetable
     */
    const schedule = data.timetables.filter(
        (timetable) =>
            timetable.branch === course &&
            String(timetable.semester) === String(semester)
    );

    async function save(e) {
        e.preventDefault();

        setMessage("");

        if (!course) {
            setMessage("Please select branch");
            return;
        }

        if (!form.subjectId) {
            setMessage("Please select subject");
            return;
        }

        if (!form.teacherId) {
            setMessage("Please select teacher");
            return;
        }

        if (form.startTime >= form.endTime) {
            setMessage(
                "End time must be after start time"
            );
            return;
        }

        const timetableData = {
            subjectId: form.subjectId,
            teacherId: form.teacherId,

            /*
             * Subject ke branch ko timetable me save karenge.
             */
            branch:
                selectedSubject?.branch || course,

            /*
             * Subject ka semester
             */
            semester: Number(
                selectedSubject?.semester || semester
            ),

            day: form.day,
            startTime: form.startTime,
            endTime: form.endTime,
        };

        try {
            if (form.id) {
                await call(
                    `/timetables/${form.id}`,
                    {
                        method: "PUT",
                        body: JSON.stringify(
                            timetableData
                        ),
                    }
                );

                setMessage(
                    "Class updated successfully"
                );
            } else {
                await call("/timetables", {
                    method: "POST",
                    body: JSON.stringify(
                        timetableData
                    ),
                });

                setMessage(
                    "Class added successfully"
                );
            }

            setForm(blank);

            /*
             * Subject / Timetable latest data
             * dobara dashboard me aa jayega.
             */
            await load();
        } catch (error) {
            setMessage(error.message);
        }
    }

    async function remove(id) {
        if (!confirm("Delete this class?")) {
            return;
        }

        try {
            await call(`/timetables/${id}`, {
                method: "DELETE",
            });

            setMessage(
                "Class deleted successfully"
            );

            if (form.id === id) {
                setForm(blank);
            }

            await load();
        } catch (error) {
            setMessage(error.message);
        }
    }

    function edit(timetable) {
        setForm({
            id: timetable.id,
            subjectId: timetable.subjectId,
            teacherId: timetable.teacherId,
            day: timetable.day,
            startTime:
                timetable.startTime.slice(0, 5),
            endTime:
                timetable.endTime.slice(0, 5),
        });
    }

    function cancelEdit() {
        setForm(blank);
        setMessage("");
    }

    return (
        <section className="weekly-manager">
            <div className="weekly-manager-head">
                <div>
                    <h2>
                        Course & Semester Weekly Planner
                    </h2>

                    <p>
                        Add, update and delete
                        Monday–Saturday classes.
                    </p>
                </div>

                <div className="weekly-course-filters">
                    {/* BRANCH */}

                    <select
                        value={course}
                        onChange={(e) => {
                            setCourse(e.target.value);

                            setForm(blank);

                            setMessage("");
                        }}
                    >
                        <option value="">
                            Select Branch
                        </option>

                        {BRANCHES.map((branch) => (
                            <option
                                key={branch.value}
                                value={branch.value}
                            >
                                {branch.label}
                            </option>
                        ))}
                    </select>

                    {/* SEMESTER */}

                    <select
                        value={semester}
                        onChange={(e) => {
                            setSemester(e.target.value);

                            setForm(blank);

                            setMessage("");
                        }}
                    >
                        {[1, 2, 3, 4, 5, 6, 7, 8].map(
                            (semesterNumber) => (
                                <option
                                    key={semesterNumber}
                                    value={semesterNumber}
                                >
                                    Semester{" "}
                                    {semesterNumber}
                                </option>
                            )
                        )}
                    </select>
                </div>
            </div>

            {message && (
                <div className="planner-message">
                    {message}
                </div>
            )}

            <form
                className="weekly-class-form"
                onSubmit={save}
            >
                {/* DAY */}

                <select
                    value={form.day}
                    onChange={(e) =>
                        setForm({
                            ...form,
                            day: e.target.value,
                        })
                    }
                >
                    {DAYS.map((day) => (
                        <option
                            key={day}
                            value={day}
                        >
                            {day}
                        </option>
                    ))}
                </select>

                {/* SUBJECT */}

                <select
                    required
                    value={form.subjectId}
                    onChange={(e) =>
                        setForm({
                            ...form,
                            subjectId: e.target.value,
                        })
                    }
                >
                    <option value="">
                        Select Subject
                    </option>

                    {subjects.map((subject) => (
                        <option
                            key={subject.id}
                            value={subject.id}
                        >
                            {subject.subjectCode} -{" "}
                            {subject.subjectName} -{" "}
                            {subject.branch} - Sem{" "}
                            {subject.semester}
                        </option>
                    ))}
                </select>

                {/* TEACHER */}

                <select
                    required
                    value={form.teacherId}
                    onChange={(e) =>
                        setForm({
                            ...form,
                            teacherId: e.target.value,
                        })
                    }
                >
                    <option value="">
                        Select Teacher
                    </option>

                    {teachers.map((teacher) => (
                        <option
                            key={teacher.id}
                            value={teacher.id}
                        >
                            {teacher.name}
                        </option>
                    ))}
                </select>

                {/* START TIME */}

                <input
                    type="time"
                    required
                    value={form.startTime}
                    onChange={(e) =>
                        setForm({
                            ...form,
                            startTime:
                                e.target.value,
                        })
                    }
                />

                {/* END TIME */}

                <input
                    type="time"
                    required
                    value={form.endTime}
                    onChange={(e) =>
                        setForm({
                            ...form,
                            endTime:
                                e.target.value,
                        })
                    }
                />

                <button
                    type="submit"
                    disabled={
                        !course ||
                        !subjects.length
                    }
                >
                    {form.id
                        ? "Update Class"
                        : "Add Class"}
                </button>

                {form.id && (
                    <button
                        type="button"
                        className="cancel-planner"
                        onClick={cancelEdit}
                    >
                        Cancel
                    </button>
                )}
            </form>

            {/* SELECTED SUBJECT DETAILS */}

            {selectedSubject && (
                <div className="selected-subject-info">
                    <h3>
                        Selected Subject Details
                    </h3>

                    <p>
                        <strong>
                            Subject Code:
                        </strong>{" "}
                        {selectedSubject.subjectCode}
                    </p>

                    <p>
                        <strong>
                            Subject Name:
                        </strong>{" "}
                        {selectedSubject.subjectName}
                    </p>

                    <p>
                        <strong>
                            Branch:
                        </strong>{" "}
                        {selectedSubject.branch}
                    </p>

                    <p>
                        <strong>
                            Semester:
                        </strong>{" "}
                        {selectedSubject.semester}
                    </p>
                </div>
            )}

            {!course ? (
                <div className="planner-empty">
                    Please select a branch.
                </div>
            ) : !subjects.length ? (
                <div className="planner-empty">
                    No subject found for{" "}
                    {course}, Semester{" "}
                    {semester}.
                    <br />
                    First add subject from
                    Subject Management.
                </div>
            ) : (
                <div className="weekly-days">
                    {DAYS.map((day) => {
                        const rows = schedule
                            .filter(
                                (timetable) =>
                                    timetable.day === day
                            )
                            .sort((a, b) =>
                                a.startTime.localeCompare(
                                    b.startTime
                                )
                            );

                        return (
                            <article
                                className="planner-day"
                                key={day}
                            >
                                <header>
                                    <b>
                                        {day.charAt(0) +
                                            day
                                                .slice(1)
                                                .toLowerCase()}
                                    </b>

                                    <span>
                                        {rows.length}
                                    </span>
                                </header>

                                {rows.length ? (
                                    rows.map(
                                        (timetable) => (
                                            <div
                                                className="planner-class"
                                                key={
                                                    timetable.id
                                                }
                                            >
                                                <time>
                                                    {timetable.startTime.slice(
                                                        0,
                                                        5
                                                    )}

                                                    <small>
                                                        to
                                                    </small>

                                                    {timetable.endTime.slice(
                                                        0,
                                                        5
                                                    )}
                                                </time>

                                                <div>
                                                    <strong>
                                                        {
                                                            timetable.subjectName
                                                        }
                                                    </strong>

                                                    <small>
                                                        {
                                                            timetable.teacherName
                                                        }
                                                    </small>

                                                    <small>
                                                        {
                                                            timetable.branch
                                                        }{" "}
                                                        / Semester{" "}
                                                        {
                                                            timetable.semester
                                                        }
                                                    </small>
                                                </div>

                                                <div className="planner-actions">
                                                    <button
                                                        type="button"
                                                        onClick={() =>
                                                            edit(
                                                                timetable
                                                            )
                                                        }
                                                    >
                                                        Edit
                                                    </button>

                                                    <button
                                                        type="button"
                                                        className="delete"
                                                        onClick={() =>
                                                            remove(
                                                                timetable.id
                                                            )
                                                        }
                                                    >
                                                        Delete
                                                    </button>
                                                </div>
                                            </div>
                                        )
                                    )
                                ) : (
                                    <div className="planner-no-class">
                                        No classes
                                    </div>
                                )}
                            </article>
                        );
                    })}
                </div>
            )}
        </section>
    );
}
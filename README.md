# 🎓 Grade Calculator — Java Console App

Console app for tracking student grades with **CSV persistence** and **Markdown report export**.  
Grading scale: **1 = best**, **5 = worst**.

---

## What it does
- Add multiple students in one run (interactive loop).
- Enter subjects and grades per student.
- Compute per-student average (two decimals).
- Show **Problems**:
  - `Failed` — if the student has at least one `5`.
  - `At risk` — if the student has at least one `4`.
- Print a formatted table row for each student in the console.
- Keep data **between runs** (CSV file).
- Export a **Markdown report** with a sorted table.

---

## Persistence (CSV)
- On each added student: **append** one CSV line to `data/students.csv`.
- On exit: **saveAll** rewrites the file using **safe-write**:
  1) write to `students.csv.tmp`  
  2) copy current `students.csv` → `students.csv.bak`  
  3) move `.tmp` → `students.csv` (atomic move if available)



## 🧠 Technologies / Concepts Used

- **Java Core**
  - Classes, objects, constructors; encapsulation; control flow
  - Exception handling (`try/catch`)
- **Collections**
  - Maps (`HashMap`), Lists (`ArrayList`)
- **Console I/O**
  - `Scanner` for interactive input
- **Formatting**
  - `String.format(...)` (two-decimal averages), `StringBuilder`
  - `Locale.US` for consistent decimal point
- **File I/O (NIO.2)**
  - `Path`, `Files`, `BufferedReader/Writer` (UTF-8 everywhere)
  - `StandardOpenOption` (`CREATE`, `APPEND`, `TRUNCATE_EXISTING`)
  - **Safe-write**: write to `.tmp` → copy `.bak` → atomic `move` to main file
- **CSV parsing**
  - Custom field separator (`separator`) and key–value separator (`kv.separator`)
  - Skip comments (`#`) and empty lines
  - Duplicate subjects: **last wins** (warn to log)
- **Configuration**
  - `config.properties` via `Properties`
  - Auto-create missing config and required directories
- **Reporting**
  - Markdown report generation (`out/report.md`)
  - Sort by average **ascending** (best first)
  - Simple summary: student count + mean of student averages
- **Logging**
  - Append-only parse/error log: `out/app.log`


## Markdown report
- Path from config (default `./out/report.md`).
- Sorted **ascending by Average** (best first).
- Columns: `# | Name | Subjects | Average | Problems`.
- `Subjects` shown like: `Math:2, IT:5, Bio:4`.
- `Problems` uses what the app prints (`Failed: …`, `At risk: …`).
- Footer stats included:
  - `Students` — total count
  - `Group average (mean of student averages)` — two decimals

Example snippet:
```md
# Grade Report — 2025-08-10 17:00

| # | Name   | Subjects                     | Average | Problems                 |
|---|--------|------------------------------|---------|--------------------------|
| 1 | Kirill | Deutsch:3, IT:2              | 2.50    |                          |
| 2 | Alena  | Math:2, IT:5, Bio:4          | 3.67    | Failed: IT, At risk: Bio |

**Students:** 2  
**Group average (mean of student averages):** 3.08
```

## ✍️ Author

**Alena Vodopianova** 



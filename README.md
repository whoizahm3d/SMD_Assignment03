# TaskFlow - Project Management App

TaskFlow is a robust project management Android application designed to help teams organize projects, track tasks, manage attachments, and collaborate efficiently. Built entirely in Java targeting modern Android standards (Material Components, Edge-to-Edge display, Fragments, and ViewPager2).

## Author & Course
- **Author:** Ali Ahmad (23L-2619)
- **Course:** Software for Mobile Devices (Assignment 3)

---

## Screens & User Flow
1. **Splash Screen:** Welcomes users with TaskFlow branding and provides a "Get Started" entry point.
2. **Home Screen:** Displays the workspace overview, list of active projects in a custom `ListView`, search filtering by name or tag, and project creation dialogs.
3. **Project Activity:** A tabbed workspace hosting four dedicated fragments:
   - **Overview:** Brief & Scope, project progress summary, 2x2 interactive task breakdown grid (instant card filtering), and quick links.
   - **Tasks:** Advanced searchable and filterable task manager supporting multiple filter pills (Status, Priority, Date, Sort), dynamic counts, and task CRUD dialogs.
   - **Files:** Attachment management screen supporting type filtering (All, Images, Text), real-time search, catalog linking, secure confirmation deletion, and image/text previewers.
   - **Discussion:** Team conversation placeholder ("Coming Soon").

---

## Key Features
- **Project & Task Management:** Create and edit projects; add, edit, delete and complete tasks with real-time summary recalculations.
- **Advanced Filtering & Sorting:** Comprehensive search and multi-criteria filters for both tasks and attachments.
- **Attachments Catalog:** Link local resources (images and raw text files) with type-safe validation and full content previews.
- **State Preservation:** Filter states (`TaskFilterState`, `FileFilterState`) and project data survive configuration changes and tab switches.

---

## Architecture Note
- **`ProjectHost` Interface:** Acts as a contract between `ProjectActivity` and its hosted fragments, decoupling UI presentation from state management.
- **Centralized State:** `TaskFilterState` and `FileFilterState` instances are owned and retained within `ProjectActivity`, ensuring filter states persist across tab transitions.
- **ID-Based Updates:** All records (projects, tasks, and attachments) are identified, queried, updated, and removed using unique IDs (`UUID`) rather than volatile list indices.
- **Activity Results:** Data modifications are passed back and forth between activities using standard `ActivityResultLauncher` and Intent extra passing (`key_project`).

---

## Project Structure (Key Classes)
- **Activities:** `SplashActivity`, `HomeActivity`, `ProjectActivity`
- **Fragments:** `OverviewFragment`, `TasksFragment`, `FilesFragment`, `DiscussionFragment`
- **Adapters:** `ProjectAdapter`, `TaskAdapter`, `AttachmentAdapter`, `ProjectPagerAdapter`
- **Dialogs & Pickers:** `ProjectFormDialog`, `CatalogPickerDialog`, `AttachmentPreviewDialog`
- **Data Models & State:** `Project`, `TaskItem`, `AttachmentLink`, `CatalogItem`, `TaskFilterState`, `FileFilterState`, `AttachmentCatalog`, `SampleData`, `DateUtils`

---

## Build and Run
1. Open the project in **Android Studio**.
2. Ensure the selected device or emulator runs at or above **minSdk 24** (Android 7.0+).
3. Build and run the `app` module (`assembleDebug`).

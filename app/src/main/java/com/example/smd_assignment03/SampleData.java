package com.example.smd_assignment03;

import com.example.smd_assignment03.models.AttachmentLink;
import com.example.smd_assignment03.models.CatalogItem;
import com.example.smd_assignment03.models.Project;
import com.example.smd_assignment03.models.TaskItem;

import java.util.ArrayList;
import java.util.Calendar;

public class SampleData {

    private static AttachmentLink makeLink(String catalogId) {
        CatalogItem item = AttachmentCatalog.findById(catalogId);
        if (item != null)
        {
            return new AttachmentLink(catalogId, item.getResId());
        }
        return new AttachmentLink(catalogId, 0);
    }

    public static ArrayList<Project> createProjects() {
        ArrayList<Project> list = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        // Project 1: User Interface / Service Page Website
        ArrayList<AttachmentLink> att1 = new ArrayList<>();
        att1.add(makeLink("cat_1"));
        att1.add(makeLink("cat_3"));
        att1.add(makeLink("cat_2"));

        ArrayList<TaskItem> tasks1 = new ArrayList<>();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_YEAR, -3);
        tasks1.add(new TaskItem("Review typography guidelines with design lead and stakeholders to ensure comprehensive brand alignment across all digital and print deliverables for the upcoming major product launch.", TaskItem.STATUS_TODO, TaskItem.PRIORITY_HIGH, cal.getTimeInMillis()));

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        tasks1.add(new TaskItem("Configure pricing table responsive grid", TaskItem.STATUS_IN_PROGRESS, TaskItem.PRIORITY_NORMAL, cal.getTimeInMillis()));

        cal.setTimeInMillis(System.currentTimeMillis());
        tasks1.add(new TaskItem("Build header layout", TaskItem.STATUS_TODO, TaskItem.PRIORITY_HIGH, cal.getTimeInMillis()));
        tasks1.add(new TaskItem("Draft hero copy and CTA text", TaskItem.STATUS_TODO, TaskItem.PRIORITY_NORMAL, cal.getTimeInMillis()));

        tasks1.add(new TaskItem("Add form validation", TaskItem.STATUS_TODO, TaskItem.PRIORITY_LOW, -1));
        tasks1.add(new TaskItem("Create low fidelity service page website", TaskItem.STATUS_TODO, TaskItem.PRIORITY_NORMAL, -1));

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_YEAR, 2);
        tasks1.add(new TaskItem("Optimize images for faster load times across mobile devices", TaskItem.STATUS_IN_PROGRESS, TaskItem.PRIORITY_HIGH, cal.getTimeInMillis()));

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_YEAR, 4);
        tasks1.add(new TaskItem("Setup analytics tracking pixels", TaskItem.STATUS_IN_PROGRESS, TaskItem.PRIORITY_LOW, cal.getTimeInMillis()));

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_YEAR, -5);
        tasks1.add(new TaskItem("Create brand guideline for website design", TaskItem.STATUS_DONE, TaskItem.PRIORITY_NORMAL, cal.getTimeInMillis()));
        tasks1.add(new TaskItem("Setup project repository and CI pipeline", TaskItem.STATUS_DONE, TaskItem.PRIORITY_HIGH, cal.getTimeInMillis()));
        tasks1.add(new TaskItem("Initial wireframes approval", TaskItem.STATUS_DONE, TaskItem.PRIORITY_NORMAL, cal.getTimeInMillis()));
        tasks1.add(new TaskItem("Color palette selection", TaskItem.STATUS_DONE, TaskItem.PRIORITY_LOW, cal.getTimeInMillis()));

        Project p1 = new Project(
                "User Interface",
                "Service Page Website",
                "Make a page display about services for the company website in blue and red and provide some price information on each product card.",
                tasks1,
                att1,
                3
        );
        list.add(p1);

        // Project 2: Backend / Login API
        ArrayList<AttachmentLink> att2 = new ArrayList<>();
        att2.add(makeLink("cat_6"));
        att2.add(makeLink("cat_7"));
        ArrayList<TaskItem> tasks2 = new ArrayList<>();
        tasks2.add(new TaskItem("Setup database schema", TaskItem.STATUS_DONE, TaskItem.PRIORITY_HIGH, -1));
        tasks2.add(new TaskItem("Implement JWT auth", TaskItem.STATUS_DONE, TaskItem.PRIORITY_HIGH, -1));
        tasks2.add(new TaskItem("Write unit tests for login endpoint", TaskItem.STATUS_DONE, TaskItem.PRIORITY_NORMAL, -1));
        tasks2.add(new TaskItem("Setup password hashing", TaskItem.STATUS_TODO, TaskItem.PRIORITY_HIGH, -1));
        tasks2.add(new TaskItem("Add rate limiting middleware", TaskItem.STATUS_TODO, TaskItem.PRIORITY_NORMAL, -1));
        tasks2.add(new TaskItem("Configure OAuth2 login provider", TaskItem.STATUS_TODO, TaskItem.PRIORITY_LOW, -1));
        tasks2.add(new TaskItem("Setup API documentation with Swagger", TaskItem.STATUS_IN_PROGRESS, TaskItem.PRIORITY_NORMAL, -1));
        tasks2.add(new TaskItem("Implement refresh token rotation", TaskItem.STATUS_TODO, TaskItem.PRIORITY_HIGH, -1));
        tasks2.add(new TaskItem("Add security headers", TaskItem.STATUS_TODO, TaskItem.PRIORITY_LOW, -1));
        tasks2.add(new TaskItem("Deploy staging environment", TaskItem.STATUS_TODO, TaskItem.PRIORITY_HIGH, -1));

        Project p2 = new Project(
                "Backend",
                "Login API",
                "Build robust authentication endpoints with JWT tokens, rate limiting, and secure password hashing.",
                tasks2,
                att2,
                2
        );
        list.add(p2);

        // Project 3: Mobile / Mobile Dashboard
        ArrayList<AttachmentLink> att3 = new ArrayList<>();
        att3.add(makeLink("cat_4"));
        ArrayList<TaskItem> tasks3 = new ArrayList<>();
        tasks3.add(new TaskItem("Design bottom navigation bar", TaskItem.STATUS_DONE, TaskItem.PRIORITY_NORMAL, -1));
        tasks3.add(new TaskItem("Implement dark mode theme", TaskItem.STATUS_DONE, TaskItem.PRIORITY_HIGH, -1));
        tasks3.add(new TaskItem("Setup offline data caching", TaskItem.STATUS_IN_PROGRESS, TaskItem.PRIORITY_HIGH, -1));
        tasks3.add(new TaskItem("Build profile settings screen", TaskItem.STATUS_TODO, TaskItem.PRIORITY_NORMAL, -1));
        tasks3.add(new TaskItem("Add push notification handling", TaskItem.STATUS_TODO, TaskItem.PRIORITY_HIGH, -1));
        tasks3.add(new TaskItem("Optimize list performance", TaskItem.STATUS_TODO, TaskItem.PRIORITY_LOW, -1));
        tasks3.add(new TaskItem("Write UI instrumentation tests", TaskItem.STATUS_TODO, TaskItem.PRIORITY_NORMAL, -1));
        tasks3.add(new TaskItem("Publish internal beta release", TaskItem.STATUS_TODO, TaskItem.PRIORITY_HIGH, -1));

        Project p3 = new Project(
                "Mobile",
                "Mobile Dashboard",
                "Develop cross-platform mobile dashboard with smooth navigation, offline caching, and real-time updates.",
                tasks3,
                att3,
                5
        );
        list.add(p3);

        // Project 4: Operations / Release Checklist
        ArrayList<TaskItem> tasks4 = new ArrayList<>();
        tasks4.add(new TaskItem("Code review completed", TaskItem.STATUS_DONE, TaskItem.PRIORITY_HIGH, -1));
        tasks4.add(new TaskItem("Security audit passed", TaskItem.STATUS_DONE, TaskItem.PRIORITY_HIGH, -1));
        tasks4.add(new TaskItem("Performance benchmarks verified", TaskItem.STATUS_DONE, TaskItem.PRIORITY_NORMAL, -1));
        tasks4.add(new TaskItem("Documentation updated", TaskItem.STATUS_DONE, TaskItem.PRIORITY_LOW, -1));
        tasks4.add(new TaskItem("Staging smoke tests passed", TaskItem.STATUS_DONE, TaskItem.PRIORITY_HIGH, -1));
        tasks4.add(new TaskItem("Production deployment signed off", TaskItem.STATUS_DONE, TaskItem.PRIORITY_HIGH, -1));

        Project p4 = new Project(
                "Operations",
                "Release Checklist",
                "Final checklist before production launch ensuring all security, performance, and documentation standards are met.",
                tasks4,
                new ArrayList<>(),
                0
        );
        list.add(p4);

        // Project 5: Design / Design Assets
        Project p5 = new Project(
                "Design",
                "Design Assets",
                "Collection of brand assets, icons, typography guidelines, and color palettes for the marketing team.",
                new ArrayList<>(),
                new ArrayList<>(),
                1
        );
        list.add(p5);

        return list;
    }
}

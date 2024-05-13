package com.example.thesisproject.init;

import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.repository.CourseRepository;
import com.example.thesisproject.repository.UserCourseRepository;
import com.example.thesisproject.service.RoleService;
import com.example.thesisproject.service.CourseService;
import com.example.thesisproject.service.UserService;
import com.example.thesisproject.service.UserCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class TestDataInitializer implements CommandLineRunner {

    private final UserCourseRepository userCourseRepository;
    private final UserService userService;
    private final CourseService courseService;

    private final UserCourseService userCourseService;

    private final RoleService roleService;

    @Autowired
    public TestDataInitializer(
            UserCourseService userCourseService,
            UserService userService,
            CourseService courseService,
            UserCourseRepository userCourseRepository,
            RoleService roleService

    ) {
        this.userCourseService = userCourseService;
        this.userService = userService;
        this.courseService = courseService;
        this.userCourseRepository = userCourseRepository;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {



        User user = new User("guthondr", "pass");
        User user1 = new User("mammamur", "pass");
        User user2 = new User("tester", "tester");

        Course course = new Course("BIE-PA2", true);
        Course course1 = new Course("BIE-AG1", true);

        courseService.createCourse(course);
        courseService.createCourse(course1);

        userService.createUser(user);
        userService.createUser(user1);
        userService.createUser(user2);

        roleService.createRole("ROLE_ADMIN");
        roleService.createRole("ROLE_TEACHER");



        userService.assignRoleToUser(user.getUsername(), "ROLE_ADMIN");
        userService.assignRoleToUser(user.getUsername(), "ROLE_TEACHER");
        userService.assignRoleToUser(user1.getUsername(), "ROLE_TEACHER");
        userService.assignRoleToUser(user2.getUsername(), "ROLE_ADMIN");



        }
}
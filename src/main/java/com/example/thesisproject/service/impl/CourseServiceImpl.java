package com.example.thesisproject.service.impl;


import com.example.thesisproject.datamodel.dto.CourseDataDto;
import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.Decision;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.CourseRepository;
import com.example.thesisproject.repository.UserCourseRepository;
import com.example.thesisproject.service.CourseService;
import com.example.thesisproject.service.UserCourseService;
import com.example.thesisproject.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;

    private UserCourseRepository userCourseRepository;

    private UserService userService;

    private UserCourseService userCourseService;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, UserCourseRepository userCourseRepository, UserCourseService userCourseService, UserService userService) {
        this.courseRepository = courseRepository;
        this.userCourseRepository = userCourseRepository;
        this.userCourseService = userCourseService;
        this.userService = userService;
    }


    @Override
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
    }


    @Override
    public List<Course> fetchCourses() {

        List<Course> courses = courseRepository.findAll(Sort.by(Sort.Direction.ASC, "courseCode"));

        for (Course course : courses) {
            updateCourseCoveredStatus(course.getId());
        }
        return courses;

    }

    public boolean createCourse(Course course) {
        Course existingCourse = courseRepository.findByCourseCode(course.getCourseCode());
        if (existingCourse == null) {
            courseRepository.save(course);
            return true;
        } else {
            System.out.println("Course with code " + course.getCourseCode() + " already exists.");
            return false;
        }
    }

    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with ID " + courseId + " not found"));

        userCourseRepository.deleteByCourseId(courseId);

        courseRepository.delete(course);
    }

    public boolean updateCourse(Course course) {
        Course existingCourse = courseRepository.findByCourseCode(course.getCourseCode());
        if (existingCourse != null && !existingCourse.getId().equals(course.getId())) {
            return false;
        }
        courseRepository.save(course);
        return true;
    }

    public void updateCourseCoveredStatus(Long courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            List<UserCourse> userCourses = userCourseRepository.findUserCoursesByCourseId(courseId);

            int currentLectures = 0;
            int currentSeminars = 0;
            int currentLabs = 0;

            int approvedLectures = 0;
            int approvedSeminars = 0;
            int approvedLabs = 0;

            int totalMinLab = 0;
            int totalMaxLab = 0;

            for (UserCourse uc : userCourses) {
                switch (uc.getTeachingType()) {
                    case LECTURE:
                        currentLectures++;
                        if (uc.getDecision() == Decision.YES) {
                            approvedLectures++;
                        }
                        break;
                    case SEMINAR:
                        currentSeminars++;
                        if (uc.getDecision() == Decision.YES) {
                            approvedSeminars++;
                        }
                        break;
                    case LAB:
                        currentLabs++;
                        if (uc.getDecision() == Decision.YES) {
                            approvedLabs++;
                            totalMinLab += uc.getMinLab();
                            totalMaxLab += uc.getMaxLab();
                        }
                        break;
                }
            }

            course.setCurrentLecture(currentLectures);
            course.setCurrentSeminar(currentSeminars);
            course.setCurrentLab(currentLabs);

            boolean allLecturesApproved = approvedLectures >= course.getNumLecture();
            boolean allSeminarsApproved = approvedSeminars >= course.getNumSeminar();

            boolean labRequirementsMet = true;
            if (course.isHasLabs()) {
                labRequirementsMet = course.getNumLab() <= totalMaxLab;
            }

            boolean newCoveredStatus = allLecturesApproved &&
                    allSeminarsApproved &&
                    (!course.isHasLabs() || labRequirementsMet);

            course.setCovered(newCoveredStatus);
            courseRepository.save(course);

        } else {
            throw new EntityNotFoundException("Course not found with id: " + courseId);
        }
    }


    @Override
    public CourseDataDto prepareCoursePageData(Long courseId) {
        List<User> users = userService.fetchUsers();
        Course course = courseRepository.findById(courseId).orElse(null);

        List<UserCourse> userCourses = userCourseService.getUserCoursesByCourseId(courseId);
        List<TeachingType> allTeachingTypes = Arrays.asList(TeachingType.values());

        int currentLectures = 0;
        int currentSeminars = 0;
        int currentLabs = 0;

        for (UserCourse uc : userCourses) {
            switch (uc.getTeachingType()) {
                case LECTURE:
                    currentLectures++;
                    break;
                case SEMINAR:
                    currentSeminars++;
                    break;
                case LAB:
                    currentLabs++;
                    break;
            }
        }
        course.setCurrentLecture(currentLectures);
        course.setCurrentSeminar(currentSeminars);
        course.setCurrentLab(currentLabs);

        Map<String, Map<String, Boolean>> usersMap = new HashMap<>();
        for (UserCourse userCourse : userCourses) {
            String username = userCourse.getUser().getUsername();
            String teachingType = userCourse.getTeachingType().name();

            Map<String, Boolean> teachingTypes = usersMap.getOrDefault(username, new HashMap<>());
            teachingTypes.put(teachingType, true);

            usersMap.put(username, teachingTypes);
        }

        Map<Long, Integer> currentLabSumsMax = new HashMap<>();
        Map<Long, Integer> currentLabSumsMin = new HashMap<>();

        int sumMaxLab = userCourseService.sumMaxLabByCourseId(course.getId());
        currentLabSumsMax.put(course.getId(), sumMaxLab);

        int sumMinLab = userCourseService.sumMinLabByCourseId(course.getId());
        currentLabSumsMin.put(course.getId(), sumMinLab);

        return new CourseDataDto(users, course, allTeachingTypes, usersMap, userCourses, currentLectures, currentSeminars, currentLabs, currentLabSumsMax, currentLabSumsMin);
    }


    @Override
    public void updateLabs(Long courseId, Map<String, String> allParams) {
        Map<UserCourse, List<Integer>> courseLabsMap = new HashMap<>();
        allParams.forEach((key, value) -> {
            if (key.startsWith("minLab[") || key.startsWith("maxLab[")) {
                Long userCourseId = Long.parseLong(key.replaceAll("\\D+", ""));
                UserCourse userCourse = userCourseService.getUserCourseById(userCourseId);
                int labValue = Integer.parseInt(value);
                if (key.startsWith("minLab")) {
                    courseLabsMap.computeIfAbsent(userCourse, k -> new ArrayList<>()).add(0, labValue);
                } else {
                    courseLabsMap.computeIfAbsent(userCourse, k -> new ArrayList<>()).add(1, labValue);
                }
            }
        });
        courseLabsMap.forEach((userCourse, labs) -> {
            int min = labs.get(0);
            int max = labs.get(1);
            userCourse.setMinLab(min);
            userCourse.setMaxLab(Math.max(min, max));
            userCourseService.saveUserCourse(userCourse);
        });
    }

    @Override
    public void changeDecision(Long courseId, Map<String, String[]> parameters) {
        for (String key : parameters.keySet()) {
            if (key.startsWith("decision-")) {
                Long userCourseId = Long.parseLong(key.split("-")[1]);
                String decisionValue = parameters.get(key)[0];
                UserCourse userCourse = userCourseService.getUserCourseById(userCourseId);
                userCourse.setDecision(Decision.valueOf(decisionValue));
                userCourseService.saveUserCourse(userCourse);
            }
        }
    }

    @Override
    public boolean addUserCourse(Long courseId, Long userId, TeachingType teachingType, UserCourse userCourse, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            System.out.println("error occurred");
            return false;
        }

        User user = userService.getUserById(userId);
        Course course = courseRepository.findById(courseId).orElse(null);

        boolean existUserWithLabs = userCourseService.existsByUserAndCourseAndTeachingType(user, course, TeachingType.LAB);
        if (existUserWithLabs && course.hasLabs() && teachingType != TeachingType.SEMINAR && teachingType != TeachingType.LECTURE) {
            redirectAttributes.addFlashAttribute("LabRecordExists", "User already has this course with lab class");
            return false;
        }

        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setDecision(Decision.PENDING);

        userCourseService.saveUserCourse(userCourse);
        return true;
    }

    @Override
    public void updateCourses(Long courseId, Map<String, String[]> parameters) throws Exception {
        List<UserCourse> userCourses = userCourseService.getUserCoursesByCourseId(courseId);

        for (UserCourse userCourse : userCourses) {
            String key = userCourse.getUser().getUsername() + "-" + userCourse.getTeachingType().name();
            if (!parameters.containsKey(key)) {
                userCourseService.deleteUserCourse(userCourse);
            }
        }

        for (String key : parameters.keySet()) {
            String[] parts = key.split("-");
            String username;
            String courseType;

            if (parts.length == 2) {
                username = parts[0];
                courseType = parts[1];
            } else {
                username = parts[0] + '-' + parts[1];
                courseType = parts[2];
            }

            Optional<UserCourse> userCourse = userCourses.stream().filter(
                    c -> c.getUser().getUsername().equals(username)
                            && c.getTeachingType().name().equals(courseType)
            ).findFirst();

            if (userCourse.isPresent()) {
                continue;
            }

            if (parameters.get(key)[0].equals("on")) {
                User user = userService.findUserByUsername(username);
                if (user == null) {
                    throw new Exception("User not found: " + username);
                }

                UserCourse newUserCourse = new UserCourse();
                newUserCourse.setUser(user);
                newUserCourse.setTeachingType(TeachingType.valueOf(courseType));
                newUserCourse.setCourse(courseRepository.findById(courseId).orElse(null));
                newUserCourse.setDecision(Decision.PENDING);
                newUserCourse.setMinLab(0);
                newUserCourse.setMaxLab(0);
                userCourseService.saveUserCourse(newUserCourse);
            }
        }
    }


}

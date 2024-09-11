/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.trainings.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.novaserve.fitness.config.Docker;
import com.novaserve.fitness.config.TestBeans;
import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.trainings.model.*;
import com.novaserve.fitness.trainings.model.enums.Intensity;
import com.novaserve.fitness.trainings.model.enums.Kind;
import com.novaserve.fitness.trainings.model.enums.Level;
import com.novaserve.fitness.trainings.model.enums.Type;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.model.enums.Role;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@Import(TestBeans.class)
class TrainingRepositoryTest {
    @Container
    public static PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>(DockerImageName.parse(Docker.POSTGRES));

    @Autowired
    private TrainingCriteriaBuilder trainingCriteriaBuilder;

    @Autowired
    private DbHelper helper;

    private List<Area> areas;

    private List<User> instructors;

    private List<Training> trainings;

    @DynamicPropertySource
    public static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        helper.deleteAll();

        User instructor1 =
                helper.user().seed(1).role(Role.ROLE_INSTRUCTOR).build().save(User.class);
        User instructor2 =
                helper.user().seed(2).role(Role.ROLE_INSTRUCTOR).build().save(User.class);
        instructors = new ArrayList<>(List.of(instructor1, instructor2));

        Area area1 = helper.area().seed(1).build().save(Area.class);
        Area area2 = helper.area().seed(2).build().save(Area.class);
        Area area3 = helper.area().seed(3).build().save(Area.class);
        Area area4 = helper.area().seed(4).build().save(Area.class);
        areas = new ArrayList<>(List.of(area1, area2, area3, area4));

        Training training1 = helper.training()
                .seed(1)
                .kind(Kind.Group)
                .type(Type.In_person)
                .intensity(Intensity.Moderate)
                .level(Level.Intermediate)
                .instructor(instructor1)
                .areas(area1, area2)
                .build()
                .save(Training.class);
        RepeatOption repeatOption1 = helper.repeatOption()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime("10:00:00")
                .endTime("11:30:00")
                .repeatSince("2024-06-01")
                .training(training1)
                .build()
                .save(RepeatOption.class);
        RepeatOption repeatOption2 = helper.repeatOption()
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .startTime("14:00:00")
                .endTime("15:30:00")
                .repeatSince("2024-06-10")
                .training(training1)
                .build()
                .save(RepeatOption.class);

        Training training2 = helper.training()
                .seed(2)
                .kind(Kind.Group)
                .type(Type.Virtual)
                .intensity(Intensity.High)
                .level(Level.Advanced)
                .instructor(instructor1)
                .areas(area1, area3, area4)
                .build()
                .save(Training.class);
        RepeatOption repeatOption3 = helper.repeatOption()
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime("08:00:00")
                .endTime("10:00:00")
                .repeatSince("2024-07-01")
                .training(training2)
                .build()
                .save(RepeatOption.class);
        RepeatOption repeatOption4 = helper.repeatOption()
                .dayOfWeek(DayOfWeek.THURSDAY)
                .startTime("16:00:00")
                .endTime("17:30:00")
                .repeatSince("2024-07-10")
                .training(training2)
                .build()
                .save(RepeatOption.class);

        trainings = new ArrayList<>(List.of(training1, training2));
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "INSTRUCTOR")
    public void getTrainings_shouldReturnAllActiveTrainings_whenNoFilter() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        List<Training> trainings =
                trainingCriteriaBuilder.getTrainings(startDate, null, null, null, null, null, null, null);
        assertEquals(2, trainings.size());
    }

    @Test
    @WithMockUser(username = "username1", password = "Password1!", roles = "INSTRUCTOR")
    public void getTrainings_shouldReturnFilteredActiveTrainings_whenFilteredByAreas() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        List<Training> trainings = trainingCriteriaBuilder.getTrainings(
                startDate, null, List.of(areas.get(2).getName()), null, null, null, null, null);
        assertEquals(1, trainings.size());
    }
}

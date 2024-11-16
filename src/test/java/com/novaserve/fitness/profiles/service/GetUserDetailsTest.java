/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import com.novaserve.fitness.profiles.model.Club;
// import com.novaserve.fitness.profiles.model.ClubAddress;
// import com.novaserve.fitness.profiles.model.ClubSchedule;
// import com.novaserve.fitness.helpers.builders.clubs.ClubAddressTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.clubs.ClubScheduleTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.clubs.ClubTestDataBuilder;
// import com.novaserve.fitness.helpers.builders.users.UserTestDataBuilder;
// import com.novaserve.fitness.profiles.converter.AdminEntityToAdminDetailsDtoConverter;
// import com.novaserve.fitness.profiles.dto.response.AdminDetailsDto;
// import com.novaserve.fitness.profiles.dto.response.UserDetailsBaseDto;
// import com.novaserve.fitness.profiles.model.*;
// import com.novaserve.fitness.profiles.repository.UserRepository;
//
// import java.time.LocalDate;
// import java.util.Optional;
// import java.util.Set;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Spy;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.modelmapper.ModelMapper;
//
// @ExtendWith(MockitoExtension.class)
// class GetUserDetailsTest {
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Spy
//    private ModelMapper modelMapper;
//
//    @Spy
//    private AdminEntityToAdminDetailsDtoConverter adminEntityToAdminDetailsDtoConverter;
//
//    @BeforeEach
//    public void beforeEach() {
//        modelMapper.addConverter(adminEntityToAdminDetailsDtoConverter);
//    }
//
//    @Test
//    public void testGetUserDetails_ShouldReturnAdminDetails() {
//        ClubAddress clubAddress = new ClubAddressTestDataBuilder()
//                .withSeed(1)
//                .build();
//        ClubSchedule clubSchedule = new ClubScheduleTestDataBuilder()
//                .withSeed(1)
//                .build();
//        Club club = new ClubTestDataBuilder()
//                .withSeed(1)
//                .with(c -> c.setAddress(clubAddress))
//                .with(c -> c.setSchedule(clubSchedule))
//                .build();
//        UserBase requestedUser = new UserTestDataBuilder<>(Admin.class)
//                .withSeed(1)
//                .with(u -> u.setId(1L))
//                .with(u -> u.setClubs(Set.of(club)))
//                .with(u -> u.setStartDate(LocalDate.now().minusWeeks(1)))
//                .build();
//        when(userRepository.findById(requestedUser.getId())).thenReturn(Optional.of(requestedUser));
//
//        UserDetailsBaseDto userDetailsDto = userService.getUserDetails(requestedUser.getId());
//        assertNotNull(userDetailsDto);
//        assertInstanceOf(AdminDetailsDto.class, userDetailsDto);
//        verify(userRepository).findById(requestedUser.getId());
//    }
// }

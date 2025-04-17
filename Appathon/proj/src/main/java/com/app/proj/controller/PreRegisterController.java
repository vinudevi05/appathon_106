package com.app.proj.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.proj.entities.PreRegister;
import com.app.proj.entities.User;
import com.app.proj.repository.UserRepository;
import com.app.proj.service.PreRegisterService;

@RestController
@RequestMapping("/api/preregister")
public class PreRegisterController {

    @Autowired
    private PreRegisterService preRegisterService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public PreRegister createPreRegister(@RequestBody Map<String, String> requestData) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        System.out.println("Incoming requestData: " + requestData);

        // Parse and print event date
        String dateStr = requestData.get("date");
        System.out.println("Date string: " + dateStr);
        LocalDate eventDate = LocalDate.parse(dateStr, dateFormatter);
        System.out.println("Parsed eventDate: " + eventDate);

        // Parse and print start time
        String startTimeStr = requestData.get("startTime");
        System.out.println("Start time string: " + startTimeStr);
        LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
        System.out.println("Parsed startTime: " + startTime);

        // Parse and print end time
        String endTimeStr = requestData.get("endTime");
        System.out.println("End time string: " + endTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);
        System.out.println("Parsed endTime: " + endTime);

        // Parse and print bookedById
        String bookedByIdStr = requestData.get("bookedById");
        System.out.println("BookedById string: " + bookedByIdStr);
        if (bookedByIdStr == null || bookedByIdStr.isEmpty()) {
            throw new IllegalArgumentException("Missing 'bookedById' in request");
        }
        Long bookedById = Long.parseLong(bookedByIdStr);
        System.out.println("Parsed bookedById: " + bookedById);

        // Fetch and print user
        User user = userRepository.findById(bookedById)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + bookedById));
        System.out.println("Fetched user: " + user);

        // Create and print PreRegister values
        PreRegister preRegister = new PreRegister();
        preRegister.setVisitorName(requestData.get("visitorName"));
        preRegister.setPurpose(requestData.get("purpose"));
        preRegister.setEmailId(requestData.get("email"));
        preRegister.setVehicleNumber(requestData.get("vehicleNumber"));
        preRegister.setNoOfMembers(Integer.parseInt(requestData.get("noOfMembers")));
        preRegister.setEventDate(eventDate);
        preRegister.setStartTime(startTime);
        preRegister.setEndTime(endTime);
        preRegister.setBookedBy(user);

        System.out.println("PreRegister object ready to be saved: " + preRegister);

        return preRegisterService.createPreRegister(preRegister);
    }

}

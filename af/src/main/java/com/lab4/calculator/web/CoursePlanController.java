package com.lab4.calculator.web;

import com.lab4.calculator.application.dto.CoursePlanRequest;
import com.lab4.calculator.application.dto.CoursePlanResponse;
import com.lab4.calculator.application.dto.SimulationRequest;
import com.lab4.calculator.application.dto.SimulationResponse;
import com.lab4.calculator.application.mapper.CoursePlanMapper;
import com.lab4.calculator.application.service.CoursePlanService;
import com.lab4.calculator.application.service.SimulationService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class CoursePlanController {

    private final CoursePlanService coursePlanService;
    private final SimulationService simulationService;

    @PostMapping
    public ResponseEntity<CoursePlanResponse> create(@Valid @RequestBody CoursePlanRequest request) {
        var coursePlan = coursePlanService.create(request);
        var response = CoursePlanMapper.toResponse(coursePlan);
        return ResponseEntity.created(URI.create("/api/v1/plans/" + response.getId())).body(response);
    }

    @GetMapping
    public List<CoursePlanResponse> list() {
        return coursePlanService.findAll().stream()
                .map(CoursePlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CoursePlanResponse get(@PathVariable Long id) {
        return CoursePlanMapper.toResponse(coursePlanService.findById(id));
    }

    @PostMapping("/{id}/simulate")
    public SimulationResponse simulate(@PathVariable Long id,
                                       @RequestParam(defaultValue = "false") boolean persist,
                                       @Valid @RequestBody(required = false) SimulationRequest request) {
        if (persist) {
            simulationService.persistSimulation(id, request);
        }
        return simulationService.simulate(id, request);
    }
}

package kz.partnerservice.controller;

import jakarta.validation.Valid;
import kz.partnerservice.exception.CustomException;
import kz.partnerservice.model.dto.JobDTO;
import kz.partnerservice.service.impl.JobServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs/v1")
@RequiredArgsConstructor
public class JobController {

    private final JobServiceImpl service;

    @PostMapping
    public JobDTO saveOne(@RequestBody @Valid JobDTO jobDTO) throws CustomException {
        return service.saveOne(jobDTO);
    }

    @GetMapping("/{id}")
    public JobDTO getOne(@PathVariable Long id) throws CustomException {
        return service.getOne(id);
    }

    @PutMapping("/{id}")
    public JobDTO updateOne(@PathVariable Long id, @RequestBody @Valid JobDTO jobDTO) throws CustomException {
        return service.updateOne(id, jobDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable Long id) throws CustomException {
        service.deleteOne(id);
    }
}

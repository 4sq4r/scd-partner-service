package kz.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kz.demo.exception.CustomException;
import kz.demo.model.dto.CompanyDTO;
import kz.demo.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/companies/v1")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;

    @PostMapping
    @Operation(summary = "Создать компанию.")
    @ApiResponse(description = "Метод для создания компании.")
    public CompanyDTO saveOne(@RequestBody @Valid CompanyDTO companyDTO) throws CustomException {
        return service.saveOne(companyDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о компании по ID.")
    @ApiResponse(description = "Метод для получения информации о компании по ID.")
    public CompanyDTO getOne(@PathVariable Long id) throws CustomException {
        return service.getOne(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить информацию о компании по ID.")
    @ApiResponse(description = "Метод для обновления информации о компании по ID.")
    public CompanyDTO updateOne(@PathVariable Long id,
                                @RequestBody CompanyDTO companyDTO) throws CustomException {
        return service.updateOne(id, companyDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить компанию по ID.")
    @ApiResponse(description = "Метод для удаления компании по ID.")
    public void deleteOne(@PathVariable Long id) throws CustomException {
        service.deleteOne(id);
    }
}

package kz.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kz.demo.exception.CustomException;
import kz.demo.model.dto.PointOfServiceDTO;
import kz.demo.service.impl.PointOfServiceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points-of-service/v1")
@RequiredArgsConstructor
public class PointOfServiceController {

    private final PointOfServiceServiceImpl service;

    @PostMapping
    @Operation(summary = "Создать точку обслувания.")
    @ApiResponse(description = "Метод для создания точки обслуживания.")
    public PointOfServiceDTO saveOne(@RequestBody @Valid PointOfServiceDTO pointOfServiceDTO) throws CustomException {
        return service.saveOne(pointOfServiceDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о точке обслуживания по ID.")
    @ApiResponse(description = "Метод для получения информации о точке обслуживания по ID.")
    public PointOfServiceDTO getOne(@PathVariable Long id) throws CustomException {
        return service.getOne(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить информацию точки обслуживания по ID.")
    @ApiResponse(description = "Метод для обновления информации точки обслуживания по ID.")
    public PointOfServiceDTO updateOne(@PathVariable Long id,
                                       @RequestBody PointOfServiceDTO pointOfServiceDTO) throws CustomException {
        return service.updateOne(id, pointOfServiceDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить точку обслуживания по ID.")
    @ApiResponse(description = "Метод для удаления точки обслуживания по ID.")
    public void deleteOne(@PathVariable Long id) throws CustomException {
        service.deleteOne(id);
    }
}

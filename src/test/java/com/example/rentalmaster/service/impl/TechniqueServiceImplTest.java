package com.example.rentalmaster.service.impl;


import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.db.repository.TechniqueRepository;
import com.example.rentalmaster.model.dto.request.TechniqueRequest;
import com.example.rentalmaster.model.dto.request.TechniqueUpdateRequest;
import com.example.rentalmaster.model.dto.response.TechniqueInfoResponse;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;
import com.example.rentalmaster.model.enums.Availability;
import com.example.rentalmaster.model.enums.TypeTechnique;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import static org.mockito.Mockito.eq;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TechniqueServiceImplTest {

    @Mock
    private TechniqueRepository techniqueRepository;

    @InjectMocks
    private TechniqueServiceImpl techniqueService;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Успешное добавление техники")
    public void testAddTechnique_Success() {
        TechniqueRequest request = TechniqueRequest.builder()
                .stateNumber("A123BC")
                .yearOfProduction("2022")
                .loadCapacity("2000")
                .weight("5000")
                .color("Красный")
                .baseCost(1500.0)
                .typeTechnique(TypeTechnique.EXCAVATOR)
                .build();

        Technique expectedTechnique = new Technique();
        expectedTechnique.setStateNumber("A123BC");
        expectedTechnique.setAvailability(Availability.AVAILABLE);

        when(techniqueRepository.findByStateNumber("A123BC"))
                .thenReturn(Optional.empty());

        when(objectMapper.convertValue(request, Technique.class)).thenReturn(expectedTechnique);

        TechniqueResponse response = techniqueService.addTechnique(request);

        assertNotNull(response);
        assertEquals("Техника с госномером A123BC успешно добавлена", response.getMessage());

        ArgumentCaptor<Technique> techniqueCaptor = ArgumentCaptor.forClass(Technique.class);
        verify(techniqueRepository).save(techniqueCaptor.capture());

        Technique savedTechnique = techniqueCaptor.getValue();
        assertEquals(Availability.AVAILABLE, savedTechnique.getAvailability());
        assertEquals("A123BC", savedTechnique.getStateNumber());
        verify(techniqueRepository, times(1)).save(any(Technique.class));
    }

    @Test
    @DisplayName("Ошибка при добавлении техники с уже существующим госномером")
    public void testAddTechnique_TechniqueAlreadyExists() {

        TechniqueRequest request = TechniqueRequest.builder()
                .stateNumber("A123BC")
                .yearOfProduction("2022")
                .loadCapacity("2000")
                .weight("5000")
                .color("Красный")
                .baseCost(1500.0)
                .typeTechnique(TypeTechnique.CRANE)
                .build();

        Technique existingTechnique = new Technique();
        existingTechnique.setStateNumber("A123BC");

        when(techniqueRepository.findByStateNumber("A123BC"))
                .thenReturn(Optional.of(existingTechnique));

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> techniqueService.addTechnique(request));

        assertEquals("Техника с госномером A123BC уже существует", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(techniqueRepository, never()).save(any());
    }

    @Test
    @DisplayName("Успешное обновление техники")
    public void testUpdateTechnique_Success() {
        String stateNumber = "A123BC";

        Technique existingTechnique = new Technique();
        existingTechnique.setStateNumber(stateNumber);

        TechniqueUpdateRequest updateRequest = TechniqueUpdateRequest.builder()
                .yearOfProduction("2023")
                .loadCapacity("2500")
                .weight("5100")
                .color("Желтый")
                .baseCost(1600.0)
                .typeTechnique(TypeTechnique.CRANE)
                .availability(Availability.AVAILABLE)
                .build();

        Technique updatedTechnique = new Technique();
        updatedTechnique.setStateNumber(stateNumber);

        when(techniqueRepository.findByStateNumber(stateNumber)).thenReturn(Optional.of(existingTechnique));
        when(techniqueRepository.save(any())).thenReturn(updatedTechnique);
        when(objectMapper.convertValue(any(), eq(TechniqueResponse.class))).thenReturn(new TechniqueResponse());

        TechniqueResponse response = techniqueService.updateTechnique(stateNumber, updateRequest);

        assertNotNull(response);
        assertEquals("Техника с госномером A123BC успешно обновлено", response.getMessage());
        verify(techniqueRepository).save(existingTechnique);
    }

    @Test
    @DisplayName("Ошибка при обновлении — техника не найдена")
    public void testUpdateTechnique_NotFound() {
        String stateNumber = "NOT_EXIST";

        TechniqueUpdateRequest request = new TechniqueUpdateRequest();

        when(techniqueRepository.findByStateNumber(stateNumber)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> techniqueService.updateTechnique(stateNumber, request));

        assertEquals("Техника с госномером NOT_EXIST не найден", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Успешное удаление техники")
    public void testDeleteTechnique_Success() {
        String stateNumber = "A123BC";
        Technique existingTechnique = new Technique();
        existingTechnique.setStateNumber(stateNumber);

        when(techniqueRepository.findByStateNumber(stateNumber)).thenReturn(Optional.of(existingTechnique));
        when(objectMapper.convertValue(existingTechnique, TechniqueResponse.class)).thenReturn(new TechniqueResponse());

        TechniqueResponse response = techniqueService.deleteTechnique(stateNumber);

        assertEquals("Техника с госномером A123BC успешно удаленно", response.getMessage());
        verify(techniqueRepository).delete(existingTechnique);
    }

    @Test
    @DisplayName("Ошибка при удалении — техника не найдена")
    public void testDeleteTechnique_NotFound() {
        String stateNumber = "NOT_EXIST";

        when(techniqueRepository.findByStateNumber(stateNumber)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> techniqueService.deleteTechnique(stateNumber));

        assertEquals("Техника с госномером NOT_EXIST не найден", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Успешный запрос данных о технике")
    public void testGetTechnique_Success() {
        String stateNumber = "A123BC";
        Technique technique = new Technique();
        technique.setStateNumber(stateNumber);

        when(techniqueRepository.findByStateNumber(stateNumber)).thenReturn(Optional.of(technique));

        TechniqueInfoResponse response = techniqueService.getTechnique(stateNumber);

        assertEquals("Данные об технике с госномер A123BC", response.getMessage());
        assertEquals(stateNumber, response.getStateNumber());
    }

    @Test
    @DisplayName("Ошибка при получении техники — не найдена")
    public void testGetTechnique_NotFound() {
        String stateNumber = "NOT_EXIST";

        when(techniqueRepository.findByStateNumber(stateNumber)).thenReturn(Optional.empty());

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> techniqueService.getTechnique(stateNumber));

        assertEquals("Техника с госномером NOT_EXIST не найден", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Успешный запрос всех техник")
    public void testGetAllTechnique_Success() {
        Technique tech1 = new Technique();
        tech1.setStateNumber("A123BC");
        tech1.setTypeTechnique(TypeTechnique.CRANE);

        Technique tech2 = new Technique();
        tech2.setStateNumber("B456DE");
        tech2.setTypeTechnique(TypeTechnique.EXCAVATOR);

        when(techniqueRepository.findAll()).thenReturn(List.of(tech1, tech2));
        when(objectMapper.convertValue(any(Technique.class), eq(TechniqueResponse.class)))
                .thenAnswer(invocation -> {
                    Technique tech = invocation.getArgument(0);
                    return TechniqueResponse.builder()
                            .message(tech.getTypeTechnique() + " ,госномер" + tech.getStateNumber())
                            .build();
                });

        List<TechniqueResponse> responseList = techniqueService.getAllTechnique();

        assertEquals(2, responseList.size());
        assertEquals("CRANE ,госномерA123BC", responseList.get(0).getMessage());
        assertEquals("EXCAVATOR ,госномерB456DE", responseList.get(1).getMessage());
    }

    @Test
    @DisplayName("Ошибка при запросе всех техник — список пуст")
    public void testGetAllTechnique_EmptyList() {
        when(techniqueRepository.findAll()).thenReturn(List.of());

        CommonBackendException exception = assertThrows(CommonBackendException.class,
                () -> techniqueService.getAllTechnique());

        assertEquals("Список технике пуст", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Успешное получение всех техник (объектов)")
    public void testGetAll_Success() {
        List<Technique> list = List.of(new Technique(), new Technique());
        when(techniqueRepository.findAll()).thenReturn(list);

        List<Technique> result = techniqueService.getAll();

        assertEquals(2, result.size());
    }
}
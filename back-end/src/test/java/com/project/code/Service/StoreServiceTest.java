package com.project.code.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.project.code.Model.Store;
import com.project.code.Model.StoreDto;
import com.project.code.Repo.StoreRepository;
import com.project.code.exception.StoreSaveException;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    private StoreDto storeDto;
    private Store store;

    @BeforeEach
    void setUp() {
        storeDto = new StoreDto("Test Store", "Test Address");
        store = new Store("Test Store", "Test Address");
        store.setId(1L);
    }

    @Test
    void isExistStore_shouldReturnTrue_whenStoreExists() {
        // Arrange
        when(storeRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = storeService.isExistStore(1L);

        // Assert
        assertThat(result).isTrue();
        verify(storeRepository).existsById(1L);
    }

    @Test
    void isExistStore_shouldReturnFalse_whenStoreDoesNotExist() {
        // Arrange
        when(storeRepository.existsById(1L)).thenReturn(false);

        // Act
        boolean result = storeService.isExistStore(1L);

        // Assert
        assertThat(result).isFalse();
        verify(storeRepository).existsById(1L);
    }

    @Test
    void addStore_shouldReturnTrue_whenStoreIsSavedSuccessfully() {
        // Arrange
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        // Act
        boolean result = storeService.addStore(storeDto);

        // Assert
        assertThat(result).isTrue();
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void addStore_shouldThrowStoreSaveException_whenDataIntegrityViolationOccurs() {
        // Arrange
        DataIntegrityViolationException dataIntegrityException = new DataIntegrityViolationException(
                "Constraint violation");
        dataIntegrityException.initCause(new java.sql.SQLIntegrityConstraintViolationException());
        when(storeRepository.save(any(Store.class))).thenThrow(dataIntegrityException);

        // Act & Assert
        assertThatThrownBy(() -> storeService.addStore(storeDto))
                .isInstanceOf(StoreSaveException.class)
                .hasMessage("Customer with same name and address already exists");

        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void addStore_shouldThrowException_whenOtherExceptionOccurs() {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Some other error");
        when(storeRepository.save(any(Store.class))).thenThrow(runtimeException);

        // Act & Assert
        assertThatThrownBy(() -> storeService.addStore(storeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Some other error");

        verify(storeRepository).save(any(Store.class));
    }
}

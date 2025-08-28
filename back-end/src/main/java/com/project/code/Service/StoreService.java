package com.project.code.Service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.code.Model.Store;
import com.project.code.Model.StoreDto;
import com.project.code.Repo.StoreRepository;
import com.project.code.exception.StoreSaveException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public boolean isExistStore(long storeId) {
        boolean isExist = storeRepository.existsById(storeId);

        return isExist;
    }

    public boolean addStore(StoreDto storeDto) {
        try {
            storeRepository.save(new Store(storeDto.name(), storeDto.address()));
        } catch (DataIntegrityViolationException e) {
            log.error("addStore exception: " + e.getMessage());
            Throwable cause = e.getMostSpecificCause();
            if (cause instanceof java.sql.SQLIntegrityConstraintViolationException) {
                throw new StoreSaveException("Customer with same name and address already exists");
            }
            throw e;
        } catch (Exception e) {
            log.error("addStore exception: " + e.getMessage());
            throw e;
        }

        return true;
    }
}

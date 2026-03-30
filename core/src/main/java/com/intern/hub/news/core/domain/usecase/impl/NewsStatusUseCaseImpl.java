package com.intern.hub.news.core.domain.usecase.impl;

import java.util.List;

import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.ExceptionConstant;
import com.intern.hub.news.core.domain.model.NewsStatusModel;
import com.intern.hub.news.core.domain.port.NewsStatusRepository;
import com.intern.hub.news.core.domain.usecase.NewsStatusUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NewsStatusUseCaseImpl implements NewsStatusUseCase {

    private final NewsStatusRepository newsStatusRepository;

    @Override
    public NewsStatusModel create(NewsStatusModel entity, Long userId) {
        log.info("[NewsStatus] Bắt đầu tạo NewsStatus: {}", entity);
        try {
            NewsStatusModel saved = newsStatusRepository.create(entity);
            log.info("[NewsStatus] Tạo NewsStatus thành công: {}", saved);
            return saved;
        } catch (Exception e) {
            log.error("[NewsStatus] Tạo NewsStatus thất bại: {}", e.getMessage(), e);
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
                    "Failed to Save News Status: " + e.getMessage());
        }
    }

    @Override
    public NewsStatusModel create(NewsStatusModel entity) {
        log.info("[NewsStatus] Bắt đầu tạo NewsStatus (no ticket): {}", entity);
        try {
            NewsStatusModel saved = newsStatusRepository.create(entity);
            log.info("[NewsStatus] Tạo NewsStatus thành công (no ticket): {}", saved);
            return saved;
        } catch (Exception e) {
            log.error("[NewsStatus] Tạo NewsStatus thất bại (no ticket): {}", e.getMessage(), e);
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
                    "Failed to Save News Status: " + e.getMessage());
        }
    }

    @Override
    public NewsStatusModel update(NewsStatusModel entity) {
        try {
            if (!newsStatusRepository.existsById(entity.getId())) {
                throw new IllegalArgumentException("NewsStatus not found with id: " + entity.getId());
            }
            return newsStatusRepository.create(entity);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
                    "Update failed: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
                    "Failed to update News Status: " + e.getMessage());
        }
    }

    @Override
    public NewsStatusModel getById(Long id) {
        try {
            return newsStatusRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("NewsStatus not found with id: " + id));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
                    "Get by id failed: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
                    "Failed to get News Status by id: " + e.getMessage());
        }
    }

    @Override
    public List<NewsStatusModel> getAll() {
        try {
            return newsStatusRepository.findAll();
        } catch (Exception e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
                    "Failed to get all News Status: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        try {
            if (!newsStatusRepository.existsById(id)) {
                throw new IllegalArgumentException("NewsStatus not found with id: " + id);
            }
            newsStatusRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
                    "Delete failed: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
                    "Failed to delete News Status: " + e.getMessage());
        }
    }
}
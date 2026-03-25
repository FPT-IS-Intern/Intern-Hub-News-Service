package com.intern.hub.news.core.domain.usecase.impl;

import com.intern.hub.news.core.domain.model.NewsTopicModel;
import com.intern.hub.news.core.domain.port.NewsTopicRepository;
import com.intern.hub.news.core.domain.usecase.NewsTopicUsecase;
import lombok.RequiredArgsConstructor;
import com.intern.hub.library.common.exception.BadRequestException;
import com.intern.hub.library.common.exception.ExceptionConstant;

import java.util.List;

@RequiredArgsConstructor
public class NewsTopicUseCaseImpl implements NewsTopicUsecase {

    private final NewsTopicRepository newsTopicRepository;

    @Override
    public NewsTopicModel create(NewsTopicModel entity) {
        try {
            return newsTopicRepository.create(entity);
        } catch (Exception e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to create News Topic");
        }
    }

    @Override
    public NewsTopicModel update(NewsTopicModel entity) {
        try {
            if (!newsTopicRepository.existsById(entity.getId())) {
                throw new IllegalArgumentException("NewsTopic not found with id: " + entity.getId());
            }
            return newsTopicRepository.create(entity);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to update News Topic");
        }
    }

    @Override
    public NewsTopicModel getById(Long id) {
        try {
            return newsTopicRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("NewsTopic not found with id: " + id));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to get News Topic by id");
        }
    }

    @Override
    public List<NewsTopicModel> getAll() {
        try {
            return newsTopicRepository.findAll();
        } catch (Exception e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to get all News Topics");
        }
    }

    @Override
    public void delete(Long id) {
        try {
            if (!newsTopicRepository.existsById(id)) {
                throw new IllegalArgumentException("NewsTopic not found with id: " + id);
            }
            newsTopicRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(ExceptionConstant.BAD_REQUEST_DEFAULT_CODE, "Failed to delete News Topic");
        }
    }
}
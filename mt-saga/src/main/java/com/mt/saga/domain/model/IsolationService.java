package com.mt.saga.domain.model;

import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public interface IsolationService {

    void removeActiveDtx(String lookUpId);

    void hasNoActiveDtx(Consumer<Void> consumer, String lookUpId);
}

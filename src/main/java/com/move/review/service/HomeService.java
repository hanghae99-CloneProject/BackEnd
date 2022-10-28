package com.move.review.service;

import com.move.review.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class HomeService {
    private final HeartRepository heartRepository;

    @Transactional(readOnly = true)
    public Long getMain() {
        Long maxReivewId = heartRepository.heartMaxCount();

        if(maxReivewId == null) {
            return Long.valueOf(0);
        }
         return maxReivewId;
    }
}

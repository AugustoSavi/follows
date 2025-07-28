package com.users.follows.service;

import com.users.follows.model.FollowAlert;
import com.users.follows.repository.FollowAlertRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FollowAlertService {
    private final FollowAlertRepository followAlertRepository;

    public FollowAlertService(FollowAlertRepository followAlertRepository) {
        this.followAlertRepository = followAlertRepository;
    }

    public List<FollowAlert> search(String username, LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (date != null) {
            return followAlertRepository.findByUsernameAndCreatedAtAfter(username, date, pageable);
        } else {
            return followAlertRepository.findByUsername(username, pageable);
        }
    }

}

package com.shopmart.module.returns.service;

import com.shopmart.module.returns.dto.CreateReturnRequest;
import com.shopmart.module.returns.dto.ReturnDecisionRequest;
import com.shopmart.module.returns.dto.ReturnResponse;

import java.util.List;

public interface ReturnService {
    ReturnResponse create(Long userId, CreateReturnRequest request);
    List<ReturnResponse> listMine(Long userId);
    ReturnResponse getMine(Long userId, Long id);
    ReturnResponse cancelMine(Long userId, Long id);

    // Admin
    List<ReturnResponse> listAll(String status);
    ReturnResponse decide(Long id, ReturnDecisionRequest request);
}

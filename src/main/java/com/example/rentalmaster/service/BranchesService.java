package com.example.rentalmaster.service;

import com.example.rentalmaster.model.dto.request.BranchesRequest;
import com.example.rentalmaster.model.dto.response.BranchesResponse;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;

import java.util.List;
import java.util.UUID;

public interface BranchesService  {
    BranchesResponse addBranches(BranchesRequest branchesRequest);

    BranchesResponse updateBranches(UUID branchId, BranchesRequest branchesRequest);

    BranchesResponse deleteClient(UUID branchId);

    List<BranchesResponse> getAllBranches();

    List<TechniqueResponse> getTechniquesByBranchId(UUID branchId);

    BranchesResponse addTechniqueToBranch(UUID branchId, String techniqueStateNumber);
}

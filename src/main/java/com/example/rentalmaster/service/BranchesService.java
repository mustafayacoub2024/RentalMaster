package com.example.rentalmaster.service;

import com.example.rentalmaster.model.dto.request.BranchesRequest;
import com.example.rentalmaster.model.dto.request.BranchesRequestUpdate;
import com.example.rentalmaster.model.dto.response.BranchesResponse;
import com.example.rentalmaster.model.dto.response.TechniqueInfoResponse;
import com.example.rentalmaster.model.dto.response.TechniqueResponse;

import java.util.List;
import java.util.UUID;

public interface BranchesService  {
    BranchesResponse addBranches(BranchesRequest branchesRequest);

    BranchesResponse updateBranches(String branchName, BranchesRequestUpdate branchesRequest);

    BranchesResponse deleteBranch(String branchName);

    List<BranchesResponse> getAllBranches();

    List<TechniqueInfoResponse> getTechniquesByBranchName(String branchName);

    BranchesResponse addTechniqueToBranch(String branchName, String techniqueStateNumber);

    BranchesResponse deleteTechniqueToBranch(String branchName, String techniqueStateNumber);
}

package com.example.rentalmaster.service;

import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.dto.request.BranchesRequest;
import com.example.rentalmaster.model.dto.request.BranchesRequestUpdate;
import com.example.rentalmaster.model.dto.response.BranchesResponse;
import com.example.rentalmaster.model.dto.response.DriverInfoResponse;
import com.example.rentalmaster.model.dto.response.TechniqueInfoResponse;

import java.util.List;


public interface BranchesService  {
    BranchesResponse addBranches(BranchesRequest branchesRequest);

    BranchesResponse updateBranches(String branchName, BranchesRequestUpdate branchesRequest);

    BranchesResponse deleteBranch(String branchName);

    List<BranchesResponse> getAllBranches();

    List<TechniqueInfoResponse> getTechniquesByBranchName(String branchName);

    BranchesResponse addTechniqueToBranch(String branchName, String techniqueStateNumber);

    BranchesResponse deleteTechniqueToBranch(String branchName, String techniqueStateNumber);

    List<Branches> getAll();

    Branches getBranchByBranchName(String branchName);

    List<DriverInfoResponse> getDriversByBranchName(String branchName);

    BranchesResponse addDriverToBranch(String branchName, String personalNumber);

    BranchesResponse deleteDriverToBranch(String branchName, String personalNumber);
}
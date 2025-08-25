package com.dsa360.api.controller;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.dto.LoanConditionDto;
import com.dsa360.api.dto.RegionsDto;
import com.dsa360.api.dto.RoleDto;
import com.dsa360.api.dto.SystemUserDto;
import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.service.AdminService;
import com.dsa360.api.service.DSAService;
import com.dsa360.api.service.LoanConditionService;
import com.dsa360.api.service.SystemUserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private LoanConditionService loanConditionService;

	@Autowired
	private DSAService dsaService;
	@Autowired
	SystemUserService userService;

	// system user

		@PreAuthorize("hasRole('ROLE_ADMIN')")
		@PostMapping("/create-user-profile")
		public ResponseEntity<SystemUserDto> createSystemUserProfile(@RequestBody @Valid SystemUserDto userDto) {
	
			SystemUserDto userProfile = adminService.createSystemUserProfile(userDto);
	
			return new ResponseEntity<>(userProfile, HttpStatus.CREATED);
		}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/delete-user/{username}")
	public ResponseEntity<String> deleteUser(@PathVariable String username) {
		adminService.deleteSystemUser(username);
		return ResponseEntity.ok("User Deleted Successfully");

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/update-user-profile")
	public ResponseEntity<SystemUserEntity> updateUserProfile(@RequestBody SystemUserDto userDto) {
		SystemUserEntity systemUser = userService.updateSystemUser(userDto);
		return ResponseEntity.ok(systemUser);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/add-role")
	public ResponseEntity<RoleDto> addRole(@RequestBody @Valid RoleDto roleDto) {
		adminService.addRole(roleDto);
		return new ResponseEntity<>(roleDto, HttpStatus.CREATED);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/get-roll-by-id/{rollId}")
	public ResponseEntity<RoleDto> getRollById(@PathVariable String rollId) {
		var roleDto = adminService.getRollById(rollId);
		return ResponseEntity.ok(roleDto);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')") // loadUserByUsername(username)
	@GetMapping("/get-all-roles")
	public ResponseEntity<List<RoleDto>> getAllRoles() {
		List<RoleDto> allRoles = adminService.getAllRole();
		return ResponseEntity.ok(allRoles);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/get-all-roles-by-ids")
	public ResponseEntity<List<RoleDto>> getAllRolesByIds(@RequestParam List<String> ids) {
		List<RoleDto> allRoles = adminService.getAllRoleByIds(ids);
		return ResponseEntity.ok(allRoles);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/get-role-by-name")
	public ResponseEntity<RoleDto> getRoleByName(@RequestParam String roleName) {
		var roleDto = adminService.getRoleByName(roleName);
		return ResponseEntity.ok(roleDto);
	}

	// update

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/update-role")
	public ResponseEntity<RoleDto> updateRole(@RequestBody RoleDto roleDto) {
		adminService.updateRole(roleDto);
		return new ResponseEntity<>(roleDto, HttpStatus.CREATED);
	}

	// Region APIS

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/add-region")
	public ResponseEntity<RegionsDto> addRegion(@RequestBody RegionsDto regionsDto) {
		adminService.addRegion(regionsDto);
		return new ResponseEntity<>(regionsDto, HttpStatus.CREATED);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/get-region-by-id")
	public ResponseEntity<RegionsDto> getARegionById(@RequestParam String id) {
		var regionsDto = adminService.getRegionById(id);
		return ResponseEntity.ok(regionsDto);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/get-region-by-name")
	public ResponseEntity<RegionsDto> getARegionByName(@RequestParam String regionName) {
		var regionsDto = adminService.getRegionByName(regionName);
		return ResponseEntity.ok(regionsDto);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/get-all-regions")
	public ResponseEntity<List<RegionsDto>> getAllRegions() {
		List<RegionsDto> allRegions = adminService.getAllRegions();
		return ResponseEntity.ok(allRegions);
	}

	// delete region

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/update-region")
	public ResponseEntity<RegionsDto> updateRegion(@RequestBody RegionsDto regionsDto) {
		adminService.updateRegion(regionsDto);
		return new ResponseEntity<>(regionsDto, HttpStatus.CREATED);
	}

	// count

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/get-count")
	public ResponseEntity<Map<String, Integer>> getCountOfSystemUser() {

		Map<String, Integer> countOfSystemUser = adminService.getCountOfSystemUser();

		return ResponseEntity.ok(countOfSystemUser);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/get-all-dsa-ids")
	public ResponseEntity<List<String>> getAllDsaIds() {

		List<String> allApprovedDsa = dsaService.getAllApprovedDsaWithNoSystemUser();
		return new ResponseEntity<>(allApprovedDsa, HttpStatus.OK);

	}

	// manage interest rate

	@PostMapping("/loan-condition/add")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<LoanConditionDto> createLoanCondition(@RequestBody LoanConditionDto loanCondition) {
		LoanConditionDto createdLoanCondition = loanConditionService.createLoanCondition(loanCondition);
		return ResponseEntity.ok(createdLoanCondition);
	}

	@GetMapping("/get-loan-condition/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<LoanConditionDto> getLoanConditionById(@PathVariable String id) {
		LoanConditionDto loanCondition = loanConditionService.getLoanConditionById(id);
		return ResponseEntity.ok(loanCondition);
	}

	@GetMapping("/get-all-loan-conditions")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<LoanConditionDto>> getAllLoanConditions() {
		List<LoanConditionDto> loanConditions = loanConditionService.getAllLoanConditions();
		return ResponseEntity.ok(loanConditions);
	}

	@PutMapping("/update-loan-condition")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<LoanConditionDto> updateLoanCondition(@RequestBody LoanConditionDto loanCondition) {
		LoanConditionDto updatedLoanCondition = loanConditionService.updateLoanCondition(loanCondition);
		return ResponseEntity.ok(updatedLoanCondition);
	}

	@DeleteMapping("/delete-loan-condition/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Void> deleteLoanCondition(@PathVariable String id) {

		loanConditionService.deleteLoanCondition(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/get-interest-rate")
	public ResponseEntity<Double> getInterestRate(@PathVariable String bankName) {
		return new ResponseEntity<>(loanConditionService.getIntrestRate(bankName), HttpStatus.OK);
	}

	@GetMapping("/get-all-bank-name")
	public ResponseEntity<List<String>> getAllBankName() {
		return new ResponseEntity<>(loanConditionService.getAllBanksNames(), HttpStatus.OK);
	}

}

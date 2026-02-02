package com.Renthub.controller;

import com.Renthub.entity.RentRequest;
import com.Renthub.entity.Property;
import com.Renthub.entity.User;
import com.Renthub.repository.RentRequestRepository;
import com.Renthub.repository.PropertyRepository;
import com.Renthub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rent-requests")
@CrossOrigin(origins = "*")
public class RentRequestController {

    @Autowired
    private RentRequestRepository rentRequestRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. GET ALL RENT REQUESTS (for admin)
    @GetMapping
    public List<RentRequest> getAllRentRequests() {
        return rentRequestRepository.findAll();
    }

    // 2. CREATE RENT REQUEST (tenant submits)
    @PostMapping
    public ResponseEntity<?> createRentRequest(@RequestBody RentRequestDTO requestDTO) {
        try {
            // Find property
            Optional<Property> propertyOpt = propertyRepository.findById(requestDTO.getPropertyId());
            if (propertyOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Property not found");
            }

            // Find tenant
            Optional<User> tenantOpt = userRepository.findById(requestDTO.getTenantId());
            if (tenantOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            Property property = propertyOpt.get();
            User tenant = tenantOpt.get();

            // Check if tenant already has a pending request for this property
            List<RentRequest> existingRequests = rentRequestRepository
                    .findByPropertyAndStatus(property, "PENDING");
            boolean alreadyRequested = existingRequests.stream()
                    .anyMatch(req -> req.getTenant().getId().equals(tenant.getId()));

            if (alreadyRequested) {
                return ResponseEntity.badRequest().body("You already have a pending request for this property");
            }

            // Create rent request
            RentRequest rentRequest = new RentRequest();
            rentRequest.setProperty(property);
            rentRequest.setTenant(tenant);
            rentRequest.setMessage(requestDTO.getMessage());
            rentRequest.setStatus("PENDING");

            RentRequest savedRequest = rentRequestRepository.save(rentRequest);
            return ResponseEntity.ok(savedRequest);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating rent request: " + e.getMessage());
        }
    }

    // 3. GET RENT REQUESTS BY TENANT ID
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<?> getRentRequestsByTenant(@PathVariable Long tenantId) {
        Optional<User> tenantOpt = userRepository.findById(tenantId);
        if (tenantOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Tenant not found");
        }

        List<RentRequest> requests = rentRequestRepository.findByTenant(tenantOpt.get());
        return ResponseEntity.ok(requests);
    }

    // 4. GET RENT REQUESTS BY PROPERTY ID
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<?> getRentRequestsByProperty(@PathVariable Long propertyId) {
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        if (propertyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Property not found");
        }

        List<RentRequest> requests = rentRequestRepository.findByProperty(propertyOpt.get());
        return ResponseEntity.ok(requests);
    }

    // 5. GET PENDING RENT REQUESTS (for landlords)
    @GetMapping("/pending")
    public List<RentRequest> getPendingRentRequests() {
        return rentRequestRepository.findByStatus("PENDING");
    }

    // 6. APPROVE RENT REQUEST (landlord approves)
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveRentRequest(@PathVariable Long id) {
        Optional<RentRequest> requestOpt = rentRequestRepository.findById(id);
        if (requestOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RentRequest rentRequest = requestOpt.get();
        rentRequest.setStatus("APPROVED");
        rentRequest.setResponseDate(LocalDateTime.now());

        RentRequest updatedRequest = rentRequestRepository.save(rentRequest);
        return ResponseEntity.ok(updatedRequest);
    }

    // 7. REJECT RENT REQUEST (landlord rejects)
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectRentRequest(@PathVariable Long id, @RequestBody(required = false) String reason) {
        Optional<RentRequest> requestOpt = rentRequestRepository.findById(id);
        if (requestOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RentRequest rentRequest = requestOpt.get();
        rentRequest.setStatus("REJECTED");
        rentRequest.setResponseDate(LocalDateTime.now());
        if (reason != null) {
            rentRequest.setMessage(rentRequest.getMessage() + " [Rejected: " + reason + "]");
        }

        RentRequest updatedRequest = rentRequestRepository.save(rentRequest);
        return ResponseEntity.ok(updatedRequest);
    }

    // 8. GET RENT REQUEST BY ID
    @GetMapping("/{id}")
    public ResponseEntity<RentRequest> getRentRequestById(@PathVariable Long id) {
        Optional<RentRequest> request = rentRequestRepository.findById(id);
        return request.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DTO class for creating rent requests
    public static class RentRequestDTO {
        private Long propertyId;
        private Long tenantId;
        private String message;

        // Getters and Setters
        public Long getPropertyId() { return propertyId; }
        public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

        public Long getTenantId() { return tenantId; }
        public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
package com.Renthub.repository;

import com.Renthub.entity.RentRequest;
import com.Renthub.entity.Property;
import com.Renthub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentRequestRepository extends JpaRepository<RentRequest, Long> {


    List<RentRequest> findByProperty(Property property);


    List<RentRequest> findByTenant(User tenant);

    List<RentRequest> findByStatus(String status);


    List<RentRequest> findByProperty_LandlordId(Long landlordId);


    Long countByProperty_LandlordIdAndStatus(Long landlordId, String status);

    List<RentRequest> findByPropertyAndStatus(Property property, String status);

}
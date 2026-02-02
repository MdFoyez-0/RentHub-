package com.Renthub.repository;

import com.Renthub.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Find properties by status
    List<Property> findByStatus(String status);

    // Find properties by location
    List<Property> findByLocationContainingIgnoreCase(String location);

    // Find properties by landlord
    List<Property> findByLandlordId(Long landlordId);

    // Find properties within price range
    @Query("SELECT p FROM Property p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Property> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice);

    // Find properties with bedrooms
    List<Property> findByBedrooms(Integer bedrooms);

    // Find available properties
    List<Property> findByStatusOrderByPriceAsc(String status);

    // Search properties by multiple criteria
    @Query("SELECT p FROM Property p WHERE " +
            "(:location IS NULL OR p.location LIKE %:location%) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:bedrooms IS NULL OR p.bedrooms = :bedrooms) AND " +
            "p.status = 'available'")
    List<Property> searchProperties(@Param("location") String location,
                                    @Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice,
                                    @Param("bedrooms") Integer bedrooms);
}
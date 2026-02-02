package com.Renthub.controller;

import com.Renthub.entity.Property;
import com.Renthub.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    // 1. GET ALL PROPERTIES
    @GetMapping
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // 2. GET AVAILABLE PROPERTIES
    @GetMapping("/available")
    public List<Property> getAvailableProperties() {
        return propertyRepository.findByStatus("available");
    }

    // 3. CREATE PROPERTY
    @PostMapping
    public Property createProperty(@RequestBody Property property) {
        if (property.getStatus() == null) {
            property.setStatus("available");
        }
        return propertyRepository.save(property);
    }

    // 4. GET PROPERTY BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        Optional<Property> property = propertyRepository.findById(id);
        return property.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. SEARCH BY LOCATION
    @GetMapping("/search")
    public List<Property> searchProperties(@RequestParam(required = false) String location) {
        if (location == null || location.isEmpty()) {
            return propertyRepository.findAll();
        }
        return propertyRepository.findByLocationContainingIgnoreCase(location);
    }

    // 6. ADD SAMPLE DATA
    @PostMapping("/sample")
    public String addSampleData() {
        if (propertyRepository.count() > 0) {
            return "Data already exists. Current count: " + propertyRepository.count();
        }

        // Sample property 1
        Property p1 = new Property();
        p1.setTitle("Modern Apartment");
        p1.setDescription("Beautiful 2-bedroom apartment with modern amenities");
        p1.setPrice(BigDecimal.valueOf(10000));
        p1.setLocation("Uttara, Dhaka");
        p1.setAddress("Road 10, Sector 7, Uttara Dhaka 1230");
        p1.setBedrooms(2);
        p1.setBathrooms(2);
        p1.setArea(950.0);
        p1.setPropertyType("apartment");
        p1.setStatus("available");
        p1.setImageUrl("https://images.unsplash.com/photo-1568605114967-8130f3a36994");

        // Sample property 2
        Property p2 = new Property();
        p2.setTitle("Cozy House");
        p2.setDescription("Spacious 3-bedroom house with garden");
        p2.setPrice(BigDecimal.valueOf(12500));
        p2.setLocation("Bashundhara, Dhaka");
        p2.setAddress("Block A, Road 4, Bashundhara Residential Area");
        p2.setBedrooms(3);
        p2.setBathrooms(2);
        p2.setArea(1250.0);
        p2.setPropertyType("house");
        p2.setStatus("available");
        p2.setImageUrl("https://images.unsplash.com/photo-1518780664697-55e3ad937233");

        // Sample property 3
        Property p3 = new Property();
        p3.setTitle("Luxury Condo");
        p3.setDescription("Luxurious 2-bedroom condo with premium facilities");
        p3.setPrice(BigDecimal.valueOf(14000));
        p3.setLocation("Banasree, Dhaka");
        p3.setAddress("Block K, Banasree, Dhaka");
        p3.setBedrooms(2);
        p3.setBathrooms(2);
        p3.setArea(1100.0);
        p3.setPropertyType("condo");
        p3.setStatus("available");
        p3.setImageUrl("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00");

        propertyRepository.save(p1);
        propertyRepository.save(p2);
        propertyRepository.save(p3);

        return "Added 3 sample properties. Total: " + propertyRepository.count();
    }

    // 7. DELETE ALL (for testing)
    @DeleteMapping("/all")
    public String deleteAll() {
        long count = propertyRepository.count();
        propertyRepository.deleteAll();
        return "Deleted " + count + " properties";
    }

    // 8. COUNT PROPERTIES
    @GetMapping("/count")
    public String countProperties() {
        return "Total properties: " + propertyRepository.count();
    }

    // 9. UPDATE PROPERTY
    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id,
                                                   @RequestBody Property propertyDetails) {
        Optional<Property> propertyOptional = propertyRepository.findById(id);

        if (propertyOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Property property = propertyOptional.get();

        // Update fields if provided
        if (propertyDetails.getTitle() != null) {
            property.setTitle(propertyDetails.getTitle());
        }
        if (propertyDetails.getDescription() != null) {
            property.setDescription(propertyDetails.getDescription());
        }
        if (propertyDetails.getPrice() != null) {
            property.setPrice(propertyDetails.getPrice());
        }
        if (propertyDetails.getLocation() != null) {
            property.setLocation(propertyDetails.getLocation());
        }
        if (propertyDetails.getBedrooms() != null) {
            property.setBedrooms(propertyDetails.getBedrooms());
        }
        if (propertyDetails.getBathrooms() != null) {
            property.setBathrooms(propertyDetails.getBathrooms());
        }
        if (propertyDetails.getStatus() != null) {
            property.setStatus(propertyDetails.getStatus());
        }

        Property updatedProperty = propertyRepository.save(property);
        return ResponseEntity.ok(updatedProperty);
    }

    // 10. DELETE SINGLE PROPERTY
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id) {
        if (!propertyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        propertyRepository.deleteById(id);
        return ResponseEntity.ok("Property deleted successfully");
    }
}
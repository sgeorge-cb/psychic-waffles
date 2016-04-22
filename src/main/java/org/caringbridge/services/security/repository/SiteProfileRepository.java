package org.caringbridge.services.security.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.caringbridge.services.security.model.SiteProfile;
import org.springframework.data.repository.CrudRepository;

public interface SiteProfileRepository extends CrudRepository<SiteProfile, ObjectId> {
    
    public List<SiteProfile> findByUserId(Integer userId);  
}

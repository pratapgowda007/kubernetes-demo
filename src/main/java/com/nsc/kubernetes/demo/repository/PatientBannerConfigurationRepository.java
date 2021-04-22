package com.nsc.kubernetes.demo.repository;

import com.nsc.kubernetes.demo.model.PatientBannerConfiguration;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface PatientBannerConfigurationRepository extends CrudRepository<PatientBannerConfiguration, String> {
    Optional<Iterable<PatientBannerConfiguration>> findByOrganizationId(String orgId);
    Optional<PatientBannerConfiguration> findByUnitId(String unitId);
    Optional<PatientBannerConfiguration> findTopByUnitIdOrIsDefault(String unitId, boolean fetchDefault);

    void deleteByOrganizationId(String orgId);

    // Method is only allowed for a query. Use execute or executeUpdate instead of executeQuery
    @Modifying
    @Query(value = "delete from patient_banner_item_list where clinical_item_id not in :ids", nativeQuery = true)
    void deleteItemsNotExistInItemTable(@Param("ids") Set<String> ids);
}

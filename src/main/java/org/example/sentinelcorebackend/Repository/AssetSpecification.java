package org.example.sentinelcorebackend.Repository;

import org.example.sentinelcorebackend.Entity.Asset;
import org.springframework.data.jpa.domain.Specification;

public class AssetSpecification {

    public static Specification<Asset> searchAssets(
            String search,
            String status
    ) {
        return (root, query, criteriaBuilder) -> {
            Specification<Asset> specification = null;

            // Search across assetName, ipAddress, location, AND assetType
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";

                Specification<Asset> searchSpec = (root1, query1, cb) ->
                        cb.or(
                                cb.like(cb.lower(root1.get("assetName")), searchPattern),
                                cb.like(cb.lower(root1.get("ipAddress")), searchPattern),
                                cb.like(cb.lower(root1.get("location")), searchPattern),
                                cb.like(cb.lower(root1.get("assetType")), searchPattern)
                        );

                specification = searchSpec;
            }

            // Filter by status (case insensitive)
            if (status != null && !status.trim().isEmpty()) {
                String statusPattern = status.trim().toUpperCase();

                Specification<Asset> statusSpec = (root1, query1, cb) ->
                        cb.equal(
                                cb.upper(root1.get("status")),
                                statusPattern
                        );

                specification = specification == null
                        ? statusSpec
                        : specification.and(statusSpec);
            }

            return specification == null
                    ? criteriaBuilder.conjunction()
                    : specification.toPredicate(root, query, criteriaBuilder);
        };
    }
}
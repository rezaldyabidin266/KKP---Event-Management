
package com.skyline.skatecross.modules.role.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skyline.skatecross.modules.role.entity.RoleAccess;

@Repository
public interface RoleAccessRepository
        extends JpaRepository<RoleAccess, Long> {

    List<RoleAccess> findAllByRole_Id(Long roleId);

    Optional<RoleAccess> findByRole_IdAndPageKey(
            Long roleId,
            String pageKey);

    boolean existsByRole_IdAndPageKey(
            Long roleId,
            String pageKey);
}

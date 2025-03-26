package vttpb_final_project.travelplanner.repositories;

import java.util.Optional;

import vttpb_final_project.travelplanner.models.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}



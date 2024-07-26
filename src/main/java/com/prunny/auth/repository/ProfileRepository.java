package com.prunny.auth.repository;

import com.prunny.auth.model.Profile;
import com.prunny.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
   Profile findByUser(User landlord);
}
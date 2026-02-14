package com.helinok.pzbad_registration.Repositories;

import com.helinok.pzbad_registration.Models.RegisteredCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegisteredCategoryRepository extends JpaRepository<RegisteredCategory, Long> {

}

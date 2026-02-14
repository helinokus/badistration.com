package com.helinok.pzbad_registration.Repositories;

import com.helinok.pzbad_registration.Models.ExportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportHistoryRepository extends JpaRepository<ExportHistory, Long> {

}

package ro.sapientia.furniture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.sapientia.furniture.model.entities.CuttingSheet;

public interface CuttingSheetRepository extends JpaRepository<CuttingSheet, Long> {
}

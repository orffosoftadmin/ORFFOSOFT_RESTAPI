package co.orffosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.orffosoft.entity.StockAdjustment;

public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {

}

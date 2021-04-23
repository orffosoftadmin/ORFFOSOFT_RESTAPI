package co.orffosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.SequenceConfig;
import co.orffosoft.enums.SequenceName;

public interface SequenceConfigRepository extends JpaRepository<SequenceConfig, Long> {

	@Query("SELECT sg FROM SequenceConfig sg WHERE sg.sequenceName=:sequenceName and sg.entityId=:entityId")
	SequenceConfig findBySequenceName(@Param("sequenceName") SequenceName sequenceName,@Param("entityId") Long entityId);
	
	@Query("SELECT sg FROM SequenceConfig sg WHERE sg.entityId=:entityId")
	SequenceConfig checkEntityPresence(@Param("entityId") Long entityId);
}

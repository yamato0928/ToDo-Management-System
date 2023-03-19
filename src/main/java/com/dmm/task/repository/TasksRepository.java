package com.dmm.task.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmm.task.entity.Tasks;

public interface TasksRepository extends JpaRepository<Tasks, String>{
	
	Tasks findById(Integer id);
	void deleteById(Integer id);

	@Query("select a from Tasks a where a.date between :from and :to and a.name = :name")
	List<Tasks> findByDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("name") String name);
	
	
}

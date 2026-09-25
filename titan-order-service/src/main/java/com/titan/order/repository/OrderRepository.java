package com.titan.order.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.titan.order.entity.OrderEntity;

@Repository
public interface OrderRepository extends CrudRepository<OrderEntity, Long> {
	@Modifying
	@Transactional
	@Query("UPDATE OrderEntity o SET o.status = :_status WHERE o.id = :_id")
	void updateStatus(@Param("_status") String _status, @Param("_id") long _id);

}

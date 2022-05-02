package com.game.repository;

import com.game.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<PlayerEntity, Long>, JpaSpecificationExecutor<PlayerEntity> {



}

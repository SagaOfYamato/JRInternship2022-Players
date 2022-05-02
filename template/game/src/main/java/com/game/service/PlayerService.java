package com.game.service;

import com.game.entity.PlayerEntity;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PlayerService {
    Integer getAllPlayersCount();
    PlayerEntity createPlayer(PlayerEntity player);
    PlayerEntity getPlayerById(Long playerId);
    PlayerEntity updatePlayer(Long playerId, PlayerEntity playerRequired);
    void deletePlayerById(Long playerId);
    Long idChecker(String id);
    void paramsChecker(PlayerEntity playerRequired);

    List<PlayerEntity> getAllExistingPlayersList(Specification<PlayerEntity> specification);
    Page<PlayerEntity> getAllExistingPlayersList(Specification<PlayerEntity> specification, Pageable sortedByName);

    Specification<PlayerEntity> nameFilter(String name);
    Specification<PlayerEntity> titleFilter(String title);
    Specification<PlayerEntity> raceFilter(Race race);
    Specification<PlayerEntity> professionFilter(Profession profession);
    Specification<PlayerEntity> dateFilter(Long after, Long before);
    Specification<PlayerEntity> bannedFilter(Boolean isBanned);
    Specification<PlayerEntity> experienceFilter(Integer min, Integer max);
    Specification<PlayerEntity> levelFilter(Integer min, Integer max);
}

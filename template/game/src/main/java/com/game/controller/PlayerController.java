package com.game.controller;

import com.game.entity.PlayerEntity;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rest")
public class PlayerController {
    private PlayerService playerService;



    @Autowired
    public PlayerController (PlayerService clientService) {
        playerService = clientService;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public List<PlayerEntity> getAllExistingPlayersList(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "race", required = false) Race race,
        @RequestParam(value = "profession", required = false) Profession profession,
        @RequestParam(value = "after", required = false) Long after,
        @RequestParam(value = "before", required = false) Long before,
        @RequestParam(value = "banned", required = false) Boolean isBaned,
        @RequestParam(value = "minExperience", required = false) Integer minExperience,
        @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
        @RequestParam(value = "minLevel", required = false) Integer minLevel,
        @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
        @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
        @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

            return playerService.getAllExistingPlayersList(
                            Specification.where(
                                            playerService.nameFilter(name)
                                    .and(playerService.titleFilter(title)))
                                    .and(playerService.raceFilter(race))
                                    .and(playerService.professionFilter(profession)
                                    .and(playerService.dateFilter(after, before))
                                    .and(playerService.bannedFilter(isBaned))
                                    .and(playerService.experienceFilter(minExperience, maxExperience))
                                    .and(playerService.levelFilter(minLevel, maxLevel))), pageable)
                    .getContent();
    }

    @GetMapping("/players/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer getCount(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "race", required = false) Race race,
                            @RequestParam(value = "profession", required = false) Profession profession,
                            @RequestParam(value = "after", required = false) Long after,
                            @RequestParam(value = "before", required = false) Long before,
                            @RequestParam(value = "banned", required = false) Boolean isBaned,
                            @RequestParam(value = "minExperience", required = false) Integer minExperience,
                            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                            @RequestParam(value = "minLevel", required = false) Integer minLevel,
                            @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        return playerService.getAllExistingPlayersList(
                Specification.where(
                                playerService.nameFilter(name)
                                .and(playerService.titleFilter(title)))
                                .and(playerService.raceFilter(race))
                                .and(playerService.professionFilter(profession)
                                .and(playerService.dateFilter(after, before))
                                .and(playerService.bannedFilter(isBaned))
                                .and(playerService.experienceFilter(minExperience, maxExperience))
                                .and(playerService.levelFilter(minLevel, maxLevel)))).size();
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public PlayerEntity createPlayer(@RequestBody PlayerEntity playerRequired) {
        return playerService.createPlayer(playerRequired);
    }


    @GetMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerEntity getPlayerById(@PathVariable("id") String id){
        Long iD = playerService.idChecker(id);
        return playerService.getPlayerById(iD);
    }

    @PostMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerEntity updatePlayer(@PathVariable("id") String id, @RequestBody PlayerEntity playerRequired) {
        Long iD = playerService.idChecker(id);
        return playerService.updatePlayer(iD, playerRequired);
    }

    @DeleteMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(@PathVariable("id") String id) {
        Long iD = playerService.idChecker(id);
        playerService.deletePlayerById(iD);
    }
}

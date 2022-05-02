package com.game.service;

import com.game.entity.PlayerEntity;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
@Service
@RequiredArgsConstructor
public class DefaultPlayerService implements PlayerService {

    private final PlayerRepository playerRepository;
    @Override
    public void paramsChecker(PlayerEntity playerRequired) {
        if (playerRequired.getName() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The player name absents");

        if (playerRequired.getName() != null && (playerRequired.getName().length() < 1 || playerRequired.getName().length() > 12)
        || playerRequired.getName().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The player name is too long or absent");
        }

        if (playerRequired.getTitle() != null && playerRequired.getTitle().length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The player title is too long or absent");
        }

        if (playerRequired.getBirthday() != null && dateConvert(playerRequired.getBirthday()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The birthday is out of range");
        }

        if (playerRequired.getExperience() != null && (playerRequired.getExperience() < 0 || playerRequired.getExperience() > 10_000_000)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The experience is out of range");
        }

        if (playerRequired.getBirthday() != null) {
            Calendar date = Calendar.getInstance();
            date.setTime(playerRequired.getBirthday());
            if (date.get(Calendar.YEAR) < 2000 || date.get(Calendar.YEAR) > 3000) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The date of player birthday is out of range");
            }
        }
    }

    @Override
    public List<PlayerEntity> getAllExistingPlayersList(Specification<PlayerEntity> specification) {
        return playerRepository.findAll(specification);
    }

    @Override
    public Page<PlayerEntity> getAllExistingPlayersList(Specification<PlayerEntity> specification, Pageable pageable) {
        return playerRepository.findAll(specification, pageable);
    }

    @Override
    public Specification<PlayerEntity> nameFilter(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<PlayerEntity> titleFilter(String title) {
        return (root, query, criteriaBuilder) -> title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    @Override
    public Specification<PlayerEntity> raceFilter(Race race) {
        return (root, query, criteriaBuilder) -> race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    @Override
    public Specification<PlayerEntity> professionFilter(Profession profession) {
        return (root, query, criteriaBuilder) -> profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    @Override
    public Specification<PlayerEntity> dateFilter(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) {
                return null;
            }
            if (after == null) {
                Date before1 = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), before1);
            }
            if (before == null) {
                Date after1 = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), after1);
            }
            //time difference
            Date before1 = new Date(before);
            Date after1 = new Date(after);
            return criteriaBuilder.between(root.get("birthday"), after1, before1);
        };
    }

    @Override
    public Specification<PlayerEntity> bannedFilter(Boolean isBanned) {
        return (root, query, criteriaBuilder) -> {
            if (isBanned == null) {
                return null;
            }
            if (isBanned) {
                return criteriaBuilder.isTrue(root.get("banned"));
            } else {
                return criteriaBuilder.isFalse(root.get("banned"));
            }
        };
    }

    @Override
    public Specification<PlayerEntity> experienceFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), min);
            }
            return criteriaBuilder.between(root.get("experience"), min, max);
        };
    }

    @Override
    public Specification<PlayerEntity> levelFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("level"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), min);
            }
            return criteriaBuilder.between(root.get("level"), min, max);
        };
    }

    @Override
    public Long idChecker(String id) {
        if (id == null || id.equals("0")|| id.equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is incorrect");
        }
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is not a number", e);
        }
    }

    private Long dateConvert (Date date) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
        long milliseconds = 0;
        try {
            Date newDate = f.parse(String.valueOf(date));
            milliseconds = newDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }

    private Integer currentPlayerLevel(PlayerEntity playerRequired) {
        return (int) ((Math.sqrt(2500+200*playerRequired.getExperience()) - 50) / 100);
    }

    private Integer currentExperienceToNextLevel(PlayerEntity playerRequired) {
        return 50*(playerRequired.getLevel()+1)*(playerRequired.getLevel()+2) - playerRequired.getExperience();
    }

    @Override
    public Integer getAllPlayersCount() {
        return null;
    }

    @Override
    public PlayerEntity createPlayer(PlayerEntity playerRequired) {
        if (playerRequired.getName() == null
                || playerRequired.getTitle() == null
                || playerRequired.getRace() == null
                || playerRequired.getProfession() == null
                || playerRequired.getBirthday() == null
                || playerRequired.getExperience() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in all required fields");
        }

        paramsChecker(playerRequired);
        if (playerRequired.getBanned() == null)
            playerRequired.setBanned(false);

        playerRequired.setLevel(currentPlayerLevel(playerRequired));
        playerRequired.setUntilNextLevel(currentExperienceToNextLevel(playerRequired));

        return playerRepository.save(playerRequired);

    }

    @Override
    public PlayerEntity getPlayerById(Long playerId) {
        if (!playerRepository.existsById(playerId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found: id = " + playerId);

        return playerRepository.findById(playerId).get();
    }
    public void paramsCheckerForUpdate(PlayerEntity playerRequired) {
        if (playerRequired.getExperience() != null && (playerRequired.getExperience() < 0 || playerRequired.getExperience() > 10_000_000)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The experience is out of range");
        }

        if (playerRequired.getBirthday() != null) {
            Calendar date = Calendar.getInstance();
            date.setTime(playerRequired.getBirthday());
            if (date.get(Calendar.YEAR) < 2000 || date.get(Calendar.YEAR) > 3000) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The date of player birthday is out of range");
            }
        }
    }
    @Override
    public PlayerEntity updatePlayer(Long playerId, PlayerEntity playerRequired) {
        if (!playerRepository.existsById(playerId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found: id = " + playerId);


        PlayerEntity playerChanged = getPlayerById(playerId);

        if (playerRequired.getName() == null && playerRequired.getTitle() == null
                && playerRequired.getRace() == null && playerRequired.getProfession() == null
                && playerRequired.getBirthday() == null && playerRequired.getBanned() == null
                && playerRequired.getExperience() == null)
            return playerRepository.save(playerChanged);

        paramsCheckerForUpdate(playerRequired);

            if (playerRequired.getName() != null)
            playerChanged.setName(playerRequired.getName());

        if (playerRequired.getTitle() != null)
            playerChanged.setTitle(playerRequired.getTitle());

        if (playerRequired.getRace() != null)
            playerChanged.setRace(playerRequired.getRace());

        if (playerRequired.getProfession() != null)
            playerChanged.setProfession(playerRequired.getProfession());

        if (playerRequired.getBirthday() != null)
            playerChanged.setBirthday(playerRequired.getBirthday());

        if (playerRequired.getBanned() != null)
            playerChanged.setBanned(playerRequired.getBanned());

        if (playerRequired.getExperience() != null) {
            playerChanged.setExperience(playerRequired.getExperience());
            playerChanged.setLevel(currentPlayerLevel(playerChanged));
            playerChanged.setUntilNextLevel(currentExperienceToNextLevel(playerChanged));
        }

        return playerRepository.save(playerChanged);
    }

    @Override
    public void deletePlayerById(Long playerId) {
        if (playerRepository.existsById(playerId))
            playerRepository.delete(playerRepository.findById(playerId).get());
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found: id = " + playerId);
    }
}

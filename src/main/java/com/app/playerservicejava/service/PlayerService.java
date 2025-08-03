package com.app.playerservicejava.service;

import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.model.PaginatedPlayersResponse;
import com.app.playerservicejava.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class PlayerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private PlayerRepository playerRepository;

    public Players getPlayers() {
        Players players = new Players();
        playerRepository.findAll()
                .forEach(players.getPlayers()::add);
        return players;
    }

    /**
     * Async version of getPlayers - uses default thread pool
     */
    @Async
    public CompletableFuture<Players> getPlayersAsync() {
        LOGGER.info("message=Starting async getPlayers; thread={}", Thread.currentThread().getName());
        Players players = new Players();
        playerRepository.findAll()
                .forEach(players.getPlayers()::add);
        LOGGER.info("message=Completed async getPlayers; thread={}, count={}",
                   Thread.currentThread().getName(), players.getPlayers().size());
        return CompletableFuture.completedFuture(players);
    }

    public PaginatedPlayersResponse getPlayersPaginated(int page, int size, String sortBy, String sortDirection) {
        try {
            // Validate and set default values
            page = Math.max(0, page); // Page cannot be negative
            size = Math.max(1, Math.min(100, size)); // Size between 1 and 100

            // Set default sort if not provided
            if (sortBy == null || sortBy.trim().isEmpty()) {
                sortBy = "playerId";
            }

            // Set default sort direction if not provided
            if (sortDirection == null || sortDirection.trim().isEmpty()) {
                sortDirection = "asc";
            }

            // Create sort object
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection.toLowerCase()), sortBy);

            // Create pageable object
            Pageable pageable = PageRequest.of(page, size, sort);

            // Get paginated data
            Page<Player> playerPage = playerRepository.findAll(pageable);

            // Create response
            PaginatedPlayersResponse response = new PaginatedPlayersResponse();
            response.setPlayers(playerPage.getContent());
            response.setPage(page);
            response.setSize(size);
            response.setTotalElements(playerPage.getTotalElements());
            response.setTotalPages(playerPage.getTotalPages());
            response.setHasNext(playerPage.hasNext());
            response.setHasPrevious(playerPage.hasPrevious());
            response.setFirst(playerPage.isFirst());
            response.setLast(playerPage.isLast());

            LOGGER.info("message=Paginated players retrieved successfully; page={}, size={}, totalElements={}",
                       page, size, playerPage.getTotalElements());

            return response;

        } catch (Exception e) {
            LOGGER.error("message=Exception in getPlayersPaginated; exception={}", e.toString());
            throw new RuntimeException("Failed to retrieve paginated players", e);
        }
    }

    /**
     * Async version of getPlayersPaginated - uses paginated thread pool
     */
    @Async("paginatedTaskExecutor")
    public CompletableFuture<PaginatedPlayersResponse> getPlayersPaginatedAsync(int page, int size, String sortBy, String sortDirection) {
        LOGGER.info("message=Starting async pagination; thread={}, page={}, size={}",
                   Thread.currentThread().getName(), page, size);
        try {
            PaginatedPlayersResponse response = getPlayersPaginated(page, size, sortBy, sortDirection);
            LOGGER.info("message=Completed async pagination; thread={}, page={}, size={}",
                       Thread.currentThread().getName(), page, size);
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            LOGGER.error("message=Exception in async pagination; thread={}, exception={}",
                        Thread.currentThread().getName(), e.toString());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Cacheable("players")
    public Optional<Player> getPlayerById(String playerId) {
        LOGGER.info("==> Fetching player with ID {} from database.", playerId);
        Optional<Player> player = null;

        /* simulated network delay */
        try {
            player = playerRepository.findById(playerId);
            Thread.sleep(2000); // Increased delay to make caching obvious
        } catch (Exception e) {
            LOGGER.error("message=Exception in getPlayerById; exception={}", e.toString());
            return Optional.empty();
        }
        return player;
    }

    /**
     * Async version of getPlayerById - uses player thread pool
     */
    @Async("playerTaskExecutor")
    public CompletableFuture<Optional<Player>> getPlayerByIdAsync(String playerId) {
        LOGGER.info("message=Starting async getPlayerById; thread={}, playerId={}",
                   Thread.currentThread().getName(), playerId);
        try {
            Optional<Player> player = getPlayerById(playerId);
            LOGGER.info("message=Completed async getPlayerById; thread={}, playerId={}, found={}",
                       Thread.currentThread().getName(), playerId, player.isPresent());
            return CompletableFuture.completedFuture(player);
        } catch (Exception e) {
            LOGGER.error("message=Exception in async getPlayerById; thread={}, playerId={}, exception={}",
                        Thread.currentThread().getName(), playerId, e.toString());
            return CompletableFuture.failedFuture(e);
        }
    }

    public Player savePlayer(Player player) {
        try {
            Player savedPlayer = playerRepository.save(player);
            LOGGER.info("message=Player saved successfully; playerId={}", savedPlayer.getPlayerId());
            return savedPlayer;
        } catch (Exception e) {
            LOGGER.error("message=Exception in savePlayer; exception={}", e.toString());
            throw new RuntimeException("Failed to save player", e);
        }
    }

    /**
     * Async version of savePlayer - uses player thread pool
     */
    @Async("playerTaskExecutor")
    public CompletableFuture<Player> savePlayerAsync(Player player) {
        LOGGER.info("message=Starting async savePlayer; thread={}, playerId={}",
                   Thread.currentThread().getName(), player.getPlayerId());
        try {
            Player savedPlayer = savePlayer(player);
            LOGGER.info("message=Completed async savePlayer; thread={}, playerId={}",
                       Thread.currentThread().getName(), savedPlayer.getPlayerId());
            return CompletableFuture.completedFuture(savedPlayer);
        } catch (Exception e) {
            LOGGER.error("message=Exception in async savePlayer; thread={}, playerId={}, exception={}",
                        Thread.currentThread().getName(), player.getPlayerId(), e.toString());
            return CompletableFuture.failedFuture(e);
        }
    }

}

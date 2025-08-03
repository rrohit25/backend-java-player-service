package com.app.playerservicejava.controller;

import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.model.PaginatedPlayersResponse;
import com.app.playerservicejava.service.PlayerService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "v1/players", produces = { MediaType.APPLICATION_JSON_VALUE })
public class PlayerController {
    @Resource
    private PlayerService playerService;

    @GetMapping("/paginated")
    public ResponseEntity<?> getPlayers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "playerId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            // If no pagination parameters are provided, return all players
            if (page == null && size == null) {
                Players players = playerService.getPlayers();
                return ok(players);
            }
            
            // If pagination parameters are provided, return paginated results
            // Use default values if only one parameter is provided
            int pageNum = (page != null) ? page : 0;
            int sizeNum = (size != null) ? size : 10;
            
            PaginatedPlayersResponse response = playerService.getPlayersPaginated(pageNum, sizeNum, sortBy, sortDirection);
            return ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Async version for getting all players (no pagination parameters)
     */
    @GetMapping("/async")
    public CompletableFuture<ResponseEntity<Players>> getAllPlayersAsync() {
        return playerService.getPlayersAsync()
            .thenApply(players -> ok(players))
            .exceptionally(ex -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * Async version for paginated players
     */
    @GetMapping("/paginated/async")
    public CompletableFuture<ResponseEntity<PaginatedPlayersResponse>> getPlayersPaginatedAsync(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "playerId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        return playerService.getPlayersPaginatedAsync(page, size, sortBy, sortDirection)
            .thenApply(response -> ok(response))
            .exceptionally(ex -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<Player> getPlayerById(@PathVariable String playerId) {
        try {
            Optional<Player> player = playerService.getPlayerById(playerId);
            return player.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Async version of getPlayerById
     */
    @GetMapping("/{playerId}/async")
    public CompletableFuture<ResponseEntity<Player>> getPlayerByIdAsync(@PathVariable String playerId) {
        return playerService.getPlayerByIdAsync(playerId)
            .thenApply(player -> player.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)))
            .exceptionally(ex -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        try {
            Player savedPlayer = playerService.savePlayer(player);
            return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Async version of createPlayer
     */
    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<Player>> createPlayerAsync(@RequestBody Player player) {
        return playerService.savePlayerAsync(player)
            .thenApply(savedPlayer -> new ResponseEntity<>(savedPlayer, HttpStatus.CREATED))
            .exceptionally(ex -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @PostMapping("/create")
    public ResponseEntity<Player> createPlayerSpecific(@RequestBody Player player) {
        try {
            Player savedPlayer = playerService.savePlayer(player);
            return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Async version of createPlayerSpecific
     */
    @PostMapping("/create/async")
    public CompletableFuture<ResponseEntity<Player>> createPlayerSpecificAsync(@RequestBody Player player) {
        return playerService.savePlayerAsync(player)
            .thenApply(savedPlayer -> new ResponseEntity<>(savedPlayer, HttpStatus.CREATED))
            .exceptionally(ex -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}

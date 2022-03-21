package com.app.casino.service;

import com.app.casino.model.Player;
import com.app.casino.model.RequestDto;
import com.app.casino.model.Transaction;
import com.app.casino.repository.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public ResponseEntity<Object> save(Player player) {
        return new ResponseEntity<>(playerRepository.save(player), HttpStatus.CREATED);
    }

    public ResponseEntity<Object> list() {
        List<Player> playerList = playerRepository.findAll();
        if (playerList.isEmpty()) {
            return new ResponseEntity<>("There is no player in the database", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(playerRepository.findAll(), HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> getPlayerBalanceByPlayerId(Integer playerId) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isPresent()) {
            return new ResponseEntity<>("Current balance: " + player.get().getCurrentBalance(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("There is no player against this Id", HttpStatus.BAD_REQUEST);
        }
    }

    public Optional<Player> getPlayerById(Integer playerId) {
        return playerRepository.findById(playerId);
    }

    public ResponseEntity<Object> findPlayerByUsername(RequestDto requestDto) {
        if (requestDto.getTopSecret().equals("swordfish")) {
            Optional<Player> player = playerRepository.findPlayerByUsername(requestDto.getUsername());
            if (!player.isPresent()) {
                return new ResponseEntity<>("There is no user against this username", HttpStatus.OK);
            }
            List<Transaction> allTransactionsOfPlayer = player.get().getPlayerTransactions();
            List<Transaction> last10Transactions = new ArrayList<>();
            for (int i = allTransactionsOfPlayer.size() - 1; i >= 0; i--) {
                last10Transactions.add(allTransactionsOfPlayer.get(i));
                if (last10Transactions.size() == 10) {
                    break;
                }
            }
            if (last10Transactions.isEmpty()) {
                return new ResponseEntity<>("There is no transaction made by this user", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(last10Transactions, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("You are not an Authorize user to make this request", HttpStatus.UNAUTHORIZED);
        }
    }
}

package com.app.casino.controller;

import com.app.casino.model.Player;
import com.app.casino.model.RequestDto;
import com.app.casino.model.Transaction;
import com.app.casino.service.PlayerService;
import com.app.casino.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDateTime;
import java.util.Optional;

@EnableSwagger2
@RestController
@RequestMapping("/player")
public class PlayerController {
    private final PlayerService playerService;
    private final TransactionService transactionService;

    @Autowired
    public PlayerController(PlayerService playerService, TransactionService transactionService) {
        this.playerService = playerService;
        this.transactionService = transactionService;
    }

    @GetMapping("/list")
    public ResponseEntity<Object> list() {
        return playerService.list();
    }

    @PostMapping("/create")
    public ResponseEntity<Object> add(@RequestBody Player player) {
        if (player.getCurrentBalance() == null) {
            player.setCurrentBalance(0.0);
        }
        return playerService.save(player);
    }

    @GetMapping("/balance")
    public ResponseEntity<Object> viewPlayersBalance(@RequestParam(name = "playerId") Integer playerId) {
        return playerService.getPlayerBalanceByPlayerId(playerId);
    }

    @PostMapping("/wageringMoney")
    public ResponseEntity<Object> wageringMoney(@RequestParam("playerId") Integer playerId,
                                                @RequestParam(name = "transactionId") Integer transactionId,
                                                @RequestParam(required = false, name = "wagerAmount") Double wageAmount) {
        Optional<Player> player = playerService.getPlayerById(playerId);
        if (!player.isPresent()) {
            return new ResponseEntity<>("There is no player against this player ID", HttpStatus.BAD_REQUEST);
        } else if (transactionService.getTransactionById(transactionId).isPresent()) {
            return new ResponseEntity<>("This is the same transaction id that is already done", HttpStatus.OK);
        } else if (player.isPresent() && player.get().getCurrentBalance() < wageAmount) {
            return new ResponseEntity<>("This player runs out of funds, its a teapot", HttpStatus.OK);
        } else {
            Transaction transaction = new Transaction(transactionId, wageAmount, "wage", LocalDateTime.now());
            player.get().getPlayerTransactions().add(transaction);
            player.get().setCurrentBalance(player.get().getCurrentBalance() - wageAmount);
            playerService.save(player.get());
            return new ResponseEntity<>("Wage amount has been deducted from the player's balance his new balance is now:" + player.get().getCurrentBalance(), HttpStatus.OK);
        }
    }

    @PostMapping("/winningMoney")
    public ResponseEntity<Object> winningMoney(@RequestParam("playerId") Integer playerId,
                                               @RequestParam(name = "transactionId") Integer transactionId,
                                               @RequestParam(name = "winAmount") Double winAmount) {
        Optional<Player> player = playerService.getPlayerById(playerId);
        if (!player.isPresent()) {
            return new ResponseEntity<>("There is no player against this player ID", HttpStatus.BAD_REQUEST);
        } else if (transactionService.getTransactionById(transactionId).isPresent()) {
            return new ResponseEntity<>("This is the same transaction that is already done", HttpStatus.OK);
        } else {
            Transaction transaction = new Transaction(transactionId, winAmount, "Win Amount", LocalDateTime.now());
            player.get().getPlayerTransactions().add(transaction);
            player.get().setCurrentBalance(player.get().getCurrentBalance() + winAmount);
            playerService.save(player.get());
            return new ResponseEntity<>("Win amount has been added in the player's balance, his new balance is now:" + player.get().getCurrentBalance(), HttpStatus.OK);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<Object> getLastTenTransactions(@RequestBody RequestDto requestDto) {
        return playerService.findPlayerByUsername(requestDto);
    }

}

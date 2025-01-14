package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.Rank;
import com.whitelabel.martialarts.service.service.RankService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ranks")
public class RankController {

    @Autowired
    private RankService rankService;

    @GetMapping
    public List<Rank> getAllRanks() {
        return rankService.getAllRanks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rank> getRankById(@PathVariable Long id) {
        Rank rank = rankService.getRankById(id);
        return ResponseEntity.ok(rank);
    }

    @PostMapping
    public Rank createRank(@RequestBody Rank rank) {
        return rankService.createRank(rank);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rank> updateRank(@PathVariable Long id, @RequestBody Rank rank) {
        Rank updatedRank = rankService.updateRank(id, rank);
        return ResponseEntity.ok(updatedRank);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRank(@PathVariable Long id) {
        rankService.deleteRank(id);
        return ResponseEntity.noContent().build();
    }
}

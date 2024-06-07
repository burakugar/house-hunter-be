
    package com.house.hunter.controller;

    import com.house.hunter.model.dto.stats.StatsDTO;
    import com.house.hunter.service.StatsService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.ResponseStatus;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/api/v1/stats")
    @Tag(name = "Stats Controller", description = "Endpoints for retrieving application statistics")
    public class StatsController {

        private final StatsService statsService;

        @Autowired
        public StatsController(StatsService statsService) {
            this.statsService = statsService;
        }

        @GetMapping
        @Operation(summary = "Get application statistics")
        @ResponseStatus(HttpStatus.OK)
        public ResponseEntity<StatsDTO> getStats() {
            StatsDTO stats = statsService.getStats();
            return ResponseEntity.ok(stats);
        }
    }

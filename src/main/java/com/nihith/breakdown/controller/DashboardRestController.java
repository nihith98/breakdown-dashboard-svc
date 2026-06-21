package com.nihith.breakdown.controller;

import com.nihith.breakdown.dashboard.service.DashboardSummaryService;
import com.nihith.breakdown.doc.DashboardApi;
import com.nihith.breakdown.model.response.ResponseStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for dashboard summary operations.
 * {@link DashboardApi}
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardRestController implements DashboardApi {

    private static final Logger logger = LogManager.getLogger(DashboardRestController.class);

    @Autowired
    private DashboardSummaryService dashboardSummaryService;

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link DashboardSummaryService#getDashboardSummary()}.</p>
     */
    @Override
    @GetMapping("/summary")
    public ResponseStructure getDashboardSummary(
            @RequestHeader(value = "X-User-Id", required = true) String userId) {
        logger.info("Entered getDashboardSummary");
        return dashboardSummaryService.getDashboardSummary();
    }
}

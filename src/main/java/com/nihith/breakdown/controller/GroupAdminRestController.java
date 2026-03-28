package com.nihith.breakdown.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/admin/group")
public class GroupAdminRestController {

    private static final Logger logger = LogManager.getLogger(GroupAdminRestController.class);

    @Autowired
    private GroupAdminService groupAdminService;

    @PostMapping("/create")
    public String postMethodName(@RequestBody AdminGroupRequest request) {
        logger.info("Entered createGroup endpoint with request: {}", request);
        return groupAdminService.createGroup(request);
    }
    

}

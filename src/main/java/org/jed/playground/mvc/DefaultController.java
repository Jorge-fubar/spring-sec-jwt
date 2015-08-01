package org.jed.playground.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jorge España
 */
@RestController
public class DefaultController {

    @RequestMapping("/**")
    public ResponseEntity<String> notFound(HttpServletRequest request, HttpServletResponse response) {
        return new ResponseEntity<>("Resource " + request.getPathInfo() + " not found", HttpStatus.NOT_FOUND);
    }
}

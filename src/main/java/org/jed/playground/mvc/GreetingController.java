package org.jed.playground.mvc;

import org.jed.playground.data.Greeting;
import org.jed.playground.data.GreetingRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/{tenant}/greetings")
public class GreetingController {
    @Autowired private GreetingRepository greetingRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public List<Greeting> findAll() {
        Iterable<Greeting> messages = greetingRepository.findAll();
        return Lists.newArrayList(messages);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Greeting save(@Valid @RequestBody Greeting greeting, HttpServletRequest req, HttpServletResponse response) {
        return greetingRepository.save(greeting);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public Greeting findOne(@PathVariable Long id) {
        return greetingRepository.findOne(id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@PathVariable("id") Long id, RedirectAttributes redirect) {
        greetingRepository.delete(greetingRepository.findOne(id));
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@ModelAttribute Greeting message) {
        return "messages/compose";
    }
}

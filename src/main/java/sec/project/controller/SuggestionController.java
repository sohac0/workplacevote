package sec.project.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sec.project.domain.Suggestion;
import sec.project.domain.User;
import sec.project.repository.SuggestionRepository;
import sec.project.repository.UserRepository;

@Controller
public class SuggestionController {

    @Autowired
    private SuggestionRepository suggestionRepository;
    
    @Autowired
    private UserRepository userRepository; 
    
 

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/main";
    }
    
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createNewIitem() {
        
       
        return "create";
    }
    
    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String list(Authentication auth, Model model) {
        final List<Suggestion> supported = getUserSuggestions(auth);
        
        final List<Suggestion> allSuggestions = suggestionRepository.findAll();
        
        List<Suggestion> nonsupported = new ArrayList<Suggestion>();
        for(Suggestion s: allSuggestions) {
            if(!supported.contains(s)) {
                nonsupported.add(s);
            }
        }
        
        model.addAttribute("supported", supported);
        model.addAttribute("nonsupported", nonsupported);
        
        
       
        return "main";
    }
    
    @RequestMapping(value = "/suggestions/like/{itemId}", method = RequestMethod.POST)
    @Transactional
    public String like(Authentication auth, @PathVariable Long itemId) {
        final Suggestion sugg = suggestionRepository.findById(itemId);
        
        if(auth.getName() != null) {
            User user = getUser(auth);
            
            
            if(!sugg.getLikedUsers().contains(user)) {
              sugg.getLikedUsers().add(user);
              suggestionRepository.save(sugg);
             }
           
            
            
        }
        
        return "redirect:/main";
    }
    
    @RequestMapping(value = "/suggestions/info/{itemId}", method = RequestMethod.GET)
    public String like(Model model, @PathVariable Long itemId) {
        final Suggestion sugg = suggestionRepository.findById(itemId);
        
        if(sugg != null) {
            model.addAttribute("item", sugg);
        }
        
        return "info";
    }

    private User getUser(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        if(user == null) {
            user = new User(auth.getName());
            userRepository.save(user);
        }
        return user;
    }


    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String submitForm(@RequestParam String topic, @RequestParam String description) {
        suggestionRepository.save(new Suggestion(topic, description));
        return "redirect:/main";
    }

    
    public List<Suggestion> getUserSuggestions(Authentication auth) {
        
        User user = getUser(auth);
        final List<Suggestion> suggestions = suggestionRepository.findAll();
        List<Suggestion> userSuggestions = new LinkedList<Suggestion>();
        for(Suggestion sugg: suggestions) {
            if(sugg.getLikedUsers() != null && sugg.getLikedUsers().contains(user)) {
                userSuggestions.add(sugg);
            }
        }
        return userSuggestions;
    }
    
    
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String admin(Authentication auth, Model model) {
        model.addAttribute("suggestions", suggestionRepository.findAll());
        
        
       // TODO: 
       return "admin";
        
       
    }
    
    @RequestMapping(value = "/admin/delete/{itemId}", method = RequestMethod.DELETE)
    public String delete(Authentication auth, @PathVariable Long itemId) {
        final Suggestion sugg = suggestionRepository.findById(itemId);
       
        if(sugg != null && auth.getName() != null) {
              suggestionRepository.delete(sugg);
        }
        
        return "redirect:/main";
    }
    

}

package sec.project.controller;

import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Suggestion;
import sec.project.domain.User;
import sec.project.repository.SuggestionRepository;
import sec.project.repository.UserRepository;

@Controller
public class AccountDetailsController {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private UserRepository userRepo;
    
    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public String load(Model model, @RequestParam String username) {
        
        final User user = userRepo.findByUsername(username);
        if(user == null) {
            // how can this even happen??
            return "redirect:/main";
            
        }    
        List<Suggestion> userSuggestions = getUserSuggestions(user);
        model.addAttribute("suggestions", userSuggestions);
        
        return "details";
        
    }

    public List<Suggestion> getUserSuggestions(final User user) {
        final List<Suggestion> suggestions = suggestionRepository.findAll();
        List<Suggestion> userSuggestions = new LinkedList<Suggestion>();
        for(Suggestion sugg: suggestions) {
            if(sugg.getLikedUsers() != null && sugg.getLikedUsers().contains(user)) {
                userSuggestions.add(sugg);
            }
        }
        return userSuggestions;
    }

}

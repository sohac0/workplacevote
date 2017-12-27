package sec.project.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Suggestion extends AbstractPersistable<Long> {


    private String topic;
    private String description;
    
    @OneToMany
    private List<User> likedUsers;

    public Suggestion() {
        super();
    }

    public Suggestion(String topic, String description) {
        this();
        this.topic = topic;
        this.description = description;
    }

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public List<User> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(List<User> likedUsers) {
        this.likedUsers = likedUsers;
    }
    
    
    public int likedcount() {
        if(likedUsers != null) {
            return likedUsers.size();
        }
        return 0;
    }
}

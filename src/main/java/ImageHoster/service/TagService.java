package ImageHoster.service;

import ImageHoster.model.Tag;
import ImageHoster.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TagService {

    /**
     * This class needs an object of TagRepository class
     * One way is to simply declare the object of TagRepository class in this class using new operator
     * But declaring the object using the new operator makes this class tightly coupled to TagRepository class
     * Therefore in order to achieve loose coupling, we use the concept of dependency injection
     *
     * @Autowired annotation injects the TagRepository bean in this class from the Spring container, which has been declared in the Spring container at the time you run the application
     */
    @Autowired
    private TagRepository tagRepository;

    //The method calls the findTag() method in the Repository and passes the title of the tag which is to be retrieved from the database
    public Tag getTagByName(String title) {
        return tagRepository.findTag(title);
    }

    //The method calls the createTag() method in the Repository and passes the tag to be persisted in the database
    public Tag createTag(Tag tag) {
        return tagRepository.createTag(tag);
    }
}

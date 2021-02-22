package ImageHoster.service;

import ImageHoster.model.Comment;
import ImageHoster.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CommentService {

    /**
     * This class needs an object of CommentRepository class
     * One way is to simply declare the object of CommentRepository class in this class using new operator
     * But declaring the object using the new operator makes this class tightly coupled to CommentRepository class
     * Therefore in order to achieve loose coupling, we use the concept of dependency injection
     *
     * @Autowired annotation injects the CommentRepository bean in this class from the Spring container, which has been declared in the Spring container at the time you run the application
     */
    @Autowired
    private CommentRepository commentRepository;

    //The method calls the saveComment() method in the Repository and passes the comment to be persisted in the database
    public void saveComment(Comment comment) {
        commentRepository.saveComment(comment);
    }

}

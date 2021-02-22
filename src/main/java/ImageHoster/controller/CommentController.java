package ImageHoster.controller;

import ImageHoster.model.Comment;
import ImageHoster.model.Image;
import ImageHoster.model.User;
import ImageHoster.service.CommentService;
import ImageHoster.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * This is a controller class containing all the request handling methods to handle comment operations in the ImageHoster application
 * DispatcherServlet scans the classes annotated with @Controller annotation and looks for the appropriate handler method to serve the client request
 */
@Controller
public class CommentController {

    /**
     * This class needs an object of CommentService class
     * One way is to simply declare the object of CommentService class in this class using new operator
     * But declaring the object using the new operator makes this class tightly coupled to CommentService class
     * Therefore in order to achieve loose coupling, we use the concept of dependency injection
     *
     * @Autowired annotation injects the CommentService bean in this class from the Spring container, which has been declared in the Spring container at the time you run the application
     */
    @Autowired
    private CommentService commentService;

    /**
     * This class needs an object of ImageService class
     * One way is to simply declare the object of ImageService class in this class using new operator
     * But declaring the object using the new operator makes this class tightly coupled to ImageService class
     * Therefore in order to achieve loose coupling, we use the concept of dependency injection
     *
     * @Autowired annotation injects the ImageService bean in this class from the Spring container, which has been declared in the Spring container at the time you run the application
     */
    @Autowired
    private ImageService imageService;

    /**
     * After the user enters the comment, this request handling method is called
     * The method persists the comment in the database after setting all its attributes and redirects to the request handling method with request mapping of type '/images/{imageId}/{title}'
     *
     * @param comment    - This request parameter contains the comment message entered by the user
     * @param imageTitle - This dynamic parameter contains the title of the image for which the user has commented
     * @param imageId    - This dynamic parameter contains the id of the image for which the user has commented
     * @param session    - Http session containing the details of the logged in user
     * @return - Redirects to the request handling method with request mapping of type '/images/{imageId}/{title}'
     */
    @RequestMapping(value = "/image/{imageId}/{imageTitle}/comments", method = RequestMethod.POST)
    public String createComment(@RequestParam("comment") String comment, @PathVariable("imageTitle") String imageTitle, @PathVariable("imageId") Integer imageId, HttpSession session) {
        User user = (User) session.getAttribute("loggeduser");

        Image image = imageService.getImage(imageId);
        Comment newComment = new Comment(image, comment, user);
        commentService.saveComment(newComment);
        return "redirect:/images/" + image.getId() + "/" + image.getTitle();
    }
}

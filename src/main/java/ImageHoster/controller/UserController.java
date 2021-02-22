package ImageHoster.controller;

import ImageHoster.model.Image;
import ImageHoster.model.User;
import ImageHoster.model.UserProfile;
import ImageHoster.service.ImageService;
import ImageHoster.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * This is a controller class containing all the request handling methods to handle user operations in the ImageHoster application
 * DispatcherServlet scans the classes annotated with @Controller annotation and looks for the appropriate handler method to serve the client request
 */
@Controller
public class UserController {

    /**
     * This class needs an object of UserService class
     * One way is to simply declare the object of UserService class in this class using new operator
     * But declaring the object using the new operator makes this class tightly coupled to UserService class
     * Therefore in order to achieve loose coupling, we use the concept of dependency injection
     *
     * @Autowired annotation injects the UserService bean in this class from the Spring container, which has been declared in the Spring container at the time you run the application
     */
    @Autowired
    private UserService userService;

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

    //

    /**
     * This request handling method is called when the request pattern is of type 'users/registration'
     * This method declares User type and UserProfile type object
     * Sets the user profile with UserProfile type object
     * Adds User type object to a model and returns 'users/registration.html' file
     *
     * @param model - model is an object of Type Model, a class provided by the Spring. You can add the attributes in this Model type object and then access these attributes in the HTML files
     * @return - This method returns the 'users/registration.html' file
     */
    @RequestMapping("users/registration")
    public String registration(Model model) {
        User user = new User();
        UserProfile profile = new UserProfile();
        user.setProfile(profile);
        model.addAttribute("User", user);
        return "users/registration";
    }


    /**
     * This request handling method is called when the request pattern is of type 'users/registration' and also the incoming request is of POST type
     * This method calls the business logic and after the user record is persisted in the database, directs to login page
     * You need to handle the case. The password must contain atleast one alphabet, one number and one special character
     * How to ensure that the password must contain atleast one alphabet, one number and one special character in Business logic?
     * Initialize three variables 'alphabet' for the count of alphabet characters, 'number' for the count of number characters and 'specialCharacter' for the count of special characters. Iterate the string and increment the variable according to the type of character. If all the variables are non zero, the password is fine and redirect to the login page after adding the current user in the session
     * If any of the variable is zero, the password is not acceptable and you need to print the error message
     * Also, add the passwordTypeError, and User type object in the Model type object and redirect to 'users/registration.html' file
     *
     * @param user  - User type object which contains the user details and is to be persisted in the database
     * @param model - model is an object of Type Model, a class provided by the Spring. You can add the attributes in this Model type object and then access these attributes in the HTML files
     * @return - This method returns 'users/registration.html' file if the user is not persisted in the database due to any of the two reasons and retuns 'users/login.html' file if the registration is successful
     */
    @RequestMapping(value = "users/registration", method = RequestMethod.POST)
    public String registerUser(User user, Model model) {

        int alphabet = 0;
        int number = 0;
        int specialCharacter = 0;
        for (int i = 0; i < user.getPassword().length(); i++) {
            char c = user.getPassword().charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                alphabet++;
            } else if (c >= '0' && c <= '9') {
                number++;
            } else {
                specialCharacter++;
            }
        }

        if (alphabet != 0 && number != 0 && specialCharacter != 0) {
            userService.registerUser(user);
            return "users/login";
        } else {
            String error = "Password must contain atleast 1 alphabet, 1 number & 1 special character";
            User user1 = new User();
            UserProfile profile1 = new UserProfile();
            user1.setProfile(profile1);
            model.addAttribute("User", user1);
            model.addAttribute("passwordTypeError", error);
            return "users/registration";
        }

    }


    /**
     * This request handling method is called when the request pattern is of type 'users/login'
     *
     * @return - This method returns the'users/login.html' file
     */
    @RequestMapping("users/login")
    public String login() {
        return "users/login";
    }


    /**
     * This request handling method is called when the request pattern is of type 'users/login' and also the incoming request is of POST type
     * The return type of the business logic is User type. The login() method in the business logic checks whether the user with entered username and password exists in the database and returns the User type object if user with entered username and password exists in the database, else returns null
     * If user with entered username and password exists in the database, add the logged in user in the Http Session and direct to user homepage displaying all the images in the application
     * If user with entered username and password does not exist in the database, redirect to the same login page
     *
     * @param user    - A User type object containing the username and password details of the user
     * @param session - Http session containing the details of the logged in user
     * @return - If user with entered username and password exists in the database, add the logged in user in the Http Session and direct to user homepage displaying all the images in the application. If user with entered username and password does not exist in the database, redirect to the same login page
     */
    @RequestMapping(value = "users/login", method = RequestMethod.POST)
    public String loginUser(User user, HttpSession session) {
        User existingUser = userService.login(user);
        if (existingUser != null) {
            session.setAttribute("loggeduser", existingUser);
            return "redirect:/images";
        } else {
            return "users/login";
        }
    }

    /**
     * This request handling method is called when the request pattern is of type 'users/logout' and also the incoming request is of POST type
     * The method receives the Http Session and the Model type object
     * session is invalidated
     * All the images are fetched from the database and added to the model with 'images' as the key
     * 'index.html' file is returned showing the landing page of the application and displaying all the images in the application
     *
     * @param model   - model is an object of Type Model, a class provided by the Spring. You can add the attributes in this Model type object and then access these attributes in the HTML files
     * @param session - Http session containing the details of the logged in user
     * @return - This method returns the 'index.html' file showing the landing page of the application and displaying all the images in the application
     */
    @RequestMapping(value = "users/logout", method = RequestMethod.POST)
    public String logout(Model model, HttpSession session) {
        session.invalidate();

        List<Image> images = imageService.getAllImages();
        model.addAttribute("images", images);
        return "index";
    }
}
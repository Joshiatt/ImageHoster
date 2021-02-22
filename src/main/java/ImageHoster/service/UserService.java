package ImageHoster.service;

import ImageHoster.model.User;
import ImageHoster.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    /**
     * This class needs an object of UserRepository class
     * One way is to simply declare the object of UserRepository class in this class using new operator
     * But declaring the object using the new operator makes this class tightly coupled to UserRepository class
     * Therefore in order to achieve loose coupling, we use the concept of dependency injection
     *
     * @Autowired annotation injects the UserRepository bean in this class from the Spring container, which has been declared in the Spring container at the time you run the application
     */
    @Autowired
    private UserRepository userRepository;

    //Call the registerUser() method in the UserRepository class to persist the user record in the database
    public void registerUser(User newUser) {
        userRepository.registerUser(newUser);
    }

    /**
     * This method receives the User type object
     * Calls the checkUser() method in the Repository passing the username and password which checks the username and password in the database
     * The Repository returns User type object if user with entered username and password exists in the database
     * Else returns null
     *
     * @param user - User type object
     * @return - This method returns the User type object if user with entered username and password exists in the database, else returns null
     */
    public User login(User user) {
        User existingUser = userRepository.checkUser(user.getUsername(), user.getPassword());
        if (existingUser != null) {
            return existingUser;
        } else {
            return null;
        }
    }
}
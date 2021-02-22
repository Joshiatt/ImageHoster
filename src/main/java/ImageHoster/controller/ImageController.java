package ImageHoster.controller;

import ImageHoster.model.Image;
import ImageHoster.model.Tag;
import ImageHoster.model.User;
import ImageHoster.service.ImageService;
import ImageHoster.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * This is a controller class containing all the request handling methods to handle image operations in the ImageHoster application
 * DispatcherServlet scans the classes annotated with @Controller annotation and looks for the appropriate handler method to serve the client request
 */
@Controller
public class ImageController {

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
     * This class needs an object of TagService class
     * One way is to simply declare the object of TagService class in this class using new operator
     * But declaring the object using the new operator makes this class tightly coupled to TagService class
     * Therefore in order to achieve loose coupling, we use the concept of dependency injection
     *
     * @Autowired annotation injects the TagService bean in this class from the Spring container, which has been declared in the Spring container at the time you run the application
     */
    @Autowired
    private TagService tagService;

    /**
     * This request handling method displays all the images in the user home page after successful login
     * The method adds a list of images in the Model type object with 'images' as the key and returns the 'images.html' file displaying all the images in the application in the user homepage after successful login
     *
     * @param model - model is an object of Type Model, a class provided by the Spring. You can add the attributes in this Model type object and then access these attributes in the HTML files
     * @return - The method returns the 'images.html' file displaying all the images in the application in the user homepage after successful login
     */
    @RequestMapping("images")
    public String getUserImages(Model model) {
        List<Image> images = imageService.getAllImages();
        model.addAttribute("images", images);
        return "images";
    }


    /**
     * This request handling method is called when the details of the specific image with corresponding id are to be displayed
     * The logic is to get the image from the database with corresponding image id. After getting the image from the database the details are shown
     * First receive the dynamic parameter 'imageId' in the incoming request URL in a string variable 'imageId' and also the Model type object
     * Call the getImage() method in the business logic to fetch all the details of that image
     * Add the image in the Model type object with 'image' as the key
     * Add the image tags in the Model type object with 'tags' as the key
     * Add the image comments in the Model type object with 'comments' as the key
     * Return 'images/image.html' file
     *
     * @param title   - This dynamic parameter contains the title of the image.
     * @param imageId - This dynamic parameter contains the id of the image for which the details are to be displayed
     * @param model   - model is an object of Type Model, a class provided by the Spring. You can add the attributes in this Model type object and then access these attributes in the HTML files
     * @return - This method returns the 'images/image.html' file showing the details of the particular image
     */
    @RequestMapping("/images/{imageId}/{title}")
    public String showImage(@PathVariable("title") String title, @PathVariable("imageId") Integer imageId, Model model) {
        Image image = imageService.getImage(imageId);
        model.addAttribute("image", image);
        model.addAttribute("tags", image.getTags());
        return "images/image";
    }


    /**
     * This controller method is called when the request pattern is of type 'images/upload'
     * The method returns 'images/upload.html' file
     *
     * @return - The method returns 'images/upload.html' file where you can upload the details of the image
     */
    @RequestMapping("/images/upload")
    public String newImage() {
        return "images/upload";
    }


    /**
     * This request handling method is called when the request pattern is of type 'images/upload' and also the incoming request is of POST type
     * The method receives all the details of the image to be stored in the database, and now the image will be sent to the business logic to be persisted in the database
     * After you get the imageFile, set the user of the image by getting the logged in user from the Http Session
     * Convert the image to Base64 format and store it as a string in the 'imageFile' attribute
     * Convert the string of all the tags separated by a comma to a list of tags using the findOrCreateTags() method and set the tags attribute of an image as a list of these tags
     * findOrCreateTags() method also persists the non existing tags in the database
     * Set the date on which the image is posted
     * After storing the image, this method directs to the logged in user homepage displaying all the images
     *
     * @param file     - This request parameter contains the image file
     * @param tags     - This request parameter contains the string of all the tags separated by a comma
     * @param newImage - This is an object of type Image containing the image details
     * @param session  - Http session containing the details of the logged in user
     * @return - This method redirects to the request handling method with request mapping of type '/images' displaying all the images in the database
     * @throws IOException
     */
    @RequestMapping(value = "/images/upload", method = RequestMethod.POST)
    public String createImage(@RequestParam("file") MultipartFile file, @RequestParam("tags") String tags, Image newImage, HttpSession session) throws IOException {

        User user = (User) session.getAttribute("loggeduser");
        newImage.setUser(user);
        String uploadedImageData = convertUploadedFileToBase64(file);
        newImage.setImageFile(uploadedImageData);
        List<Tag> imageTags = findOrCreateTags(tags);
        newImage.setTags(imageTags);
        newImage.setDate(new Date());
        imageService.uploadImage(newImage);
        return "redirect:/images";
    }

    //This controller method is called when the request pattern is of type 'editImage'
    //This method fetches the image with the corresponding id from the database and adds it to the model with the key as 'image'
    //The method then returns 'images/edit.html' file wherein you fill all the updated details of the image

    //The method first needs to convert the list of all the tags to a string containing all the tags separated by a comma and then add this string in a Model type object
    //This string is then displayed by 'edit.html' file as previous tags of an image
    @RequestMapping(value = "/editImage")
    public String editImage(@RequestParam("imageId") Integer imageId, Model model) {
        Image image = imageService.getImage(imageId);

        String tags = convertTagsToString(image.getTags());
        model.addAttribute("image", image);
        model.addAttribute("tags", tags);
        return "images/edit";
    }

    //This controller method is called when the request pattern is of type 'images/edit' and also the incoming request is of PUT type
    //The method receives the imageFile, imageId, updated image, along with the Http Session
    //The method adds the new imageFile to the updated image if user updates the imageFile and adds the previous imageFile to the new updated image if user does not choose to update the imageFile
    //Set an id of the new updated image
    //Set the user using Http Session
    //Set the date on which the image is posted
    //Call the updateImage() method in the business logic to update the image
    //Direct to the same page showing the details of that particular updated image

    //The method also receives tags parameter which is a string of all the tags separated by a comma using the annotation @RequestParam
    //The method converts the string to a list of all the tags using findOrCreateTags() method and sets the tags attribute of an image as a list of all the tags
    @RequestMapping(value = "/editImage", method = RequestMethod.PUT)
    public String editImageSubmit(@RequestParam("file") MultipartFile file, @RequestParam("imageId") Integer imageId, @RequestParam("tags") String tags, Image updatedImage, HttpSession session) throws IOException {

        Image image = imageService.getImage(imageId);
        String updatedImageData = convertUploadedFileToBase64(file);
        List<Tag> imageTags = findOrCreateTags(tags);

        if (updatedImageData.isEmpty())
            updatedImage.setImageFile(image.getImageFile());
        else {
            updatedImage.setImageFile(updatedImageData);
        }

        updatedImage.setId(imageId);
        User user = (User) session.getAttribute("loggeduser");
        updatedImage.setUser(user);
        updatedImage.setTags(imageTags);
        updatedImage.setDate(new Date());

        imageService.updateImage(updatedImage);
        return "redirect:/images/" + updatedImage.getTitle();
    }


    //This controller method is called when the request pattern is of type 'deleteImage' and also the incoming request is of DELETE type
    //The method calls the deleteImage() method in the business logic passing the id of the image to be deleted
    //Looks for a controller method with request mapping of type '/images'
    @RequestMapping(value = "/deleteImage", method = RequestMethod.DELETE)
    public String deleteImageSubmit(@RequestParam(name = "imageId") Integer imageId) {
        imageService.deleteImage(imageId);
        return "redirect:/images";
    }


    //This method converts the image to Base64 format
    private String convertUploadedFileToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    //findOrCreateTags() method has been implemented, which returns the list of tags after converting the ‘tags’ string to a list of all the tags and also stores the tags in the database if they do not exist in the database. Observe the method and complete the code where required for this method.
    //Try to get the tag from the database using getTagByName() method. If tag is returned, you need not to store that tag in the database, and if null is returned, you need to first store that tag in the database and then the tag is added to a list
    //After adding all tags to a list, the list is returned
    private List<Tag> findOrCreateTags(String tagNames) {
        StringTokenizer st = new StringTokenizer(tagNames, ",");
        List<Tag> tags = new ArrayList<Tag>();

        while (st.hasMoreTokens()) {
            String tagName = st.nextToken().trim();
            Tag tag = tagService.getTagByName(tagName);

            if (tag == null) {
                Tag newTag = new Tag(tagName);
                tag = tagService.createTag(newTag);
            }
            tags.add(tag);
        }
        return tags;
    }


    //The method receives the list of all tags
    //Converts the list of all tags to a single string containing all the tags separated by a comma
    //Returns the string
    private String convertTagsToString(List<Tag> tags) {
        StringBuilder tagString = new StringBuilder();

        for (int i = 0; i <= tags.size() - 2; i++) {
            tagString.append(tags.get(i).getName()).append(",");
        }

        Tag lastTag = tags.get(tags.size() - 1);
        tagString.append(lastTag.getName());

        return tagString.toString();
    }
}

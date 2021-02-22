package ImageHoster.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

//@Entity annotation specifies that the corresponding class is a JPA entity
@Entity
//@Table annotation provides more options to customize the mapping.
//Here the name of the table to be created in the database is explicitly mentioned as 'Comment'. Hence the table named 'Comment' will be created in the database with all the columns mapped to all the attributes in 'Comment' class
@Table(name = "Comment")
public class Comment implements Serializable {

    //@Id annotation specifies that the corresponding attribute is a primary key
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //@Column annotation specifies that the attribute will be mapped to the column in the database.
    //Here the column name is explicitly mentioned as 'id'
    @Column(name = "id")
    private int id;

    //By default hibernate maps String to varchar(255) in PostgeSQL. If you want to store a long string and you have no idea how long it may become, use columnDefinition="TEXT"
    @Column(columnDefinition = "TEXT")
    private String text;

    @Column
    private LocalDate createdDate;

    //The 'comment' table is mapped to 'users' table with Many:One mapping
    //FetchType is LAZY
    //This column references the 'id' column in the 'users' table
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    //The 'comment' table is mapped to 'images' table with Many:One mapping
    //FetchType is LAZY
    //This column references the 'id' column in the 'images' table
    @ManyToOne(fetch = FetchType.LAZY)
    private Image image;

    public Comment() {
    }

    public Comment(Image image, String text, User user) {
        this.image = image;
        this.text = text;
        this.user = user;
        this.createdDate = LocalDate.now();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}

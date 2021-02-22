package ImageHoster.repository;

import ImageHoster.model.Tag;
import org.springframework.stereotype.Repository;

import javax.persistence.*;

//The annotation is a special type of @Component annotation which describes that the class defines a data repository
@Repository
public class TagRepository {
    //Get an instance of EntityManagerFactory from persistence unit with name as 'imageHoster'
    @PersistenceUnit(unitName = "imageHoster")
    private EntityManagerFactory emf;

    /**
     * The method receives the Tag object to be persisted in the database
     * Creates an instance of EntityManager
     * Starts a transaction
     * The transaction is committed if it is successful
     * The transaction is rolled back in case of unsuccessful transaction
     *
     * @param tag - Tag object to be persisted in the database
     * @return - This method returns the tag persisted in the database
     */
    public Tag createTag(Tag tag) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(tag);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
        return tag;
    }

    /**
     * The method creates an instance of EntityManager
     * Executes JPQL query to fetch tag from the database with the received tag name
     * Returns the fetched tag
     * Returns null in case of NoResultException
     *
     * @param tagName - name of the tag which is to be fetched from the database
     * @return - Returns the tag fetched from the database and null in case of NoResultException
     */
    public Tag findTag(String tagName) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Tag> typedQuery = em.createQuery("SELECT t from Tag t where t.name =:tagName", Tag.class).setParameter("tagName", tagName);
            return typedQuery.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}

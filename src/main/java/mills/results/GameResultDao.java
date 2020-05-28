package mills.results;

import com.google.inject.persist.Transactional;
import util.jpa.GenericJpaDao;

import java.util.List;

/**
 * DAO class for the {@link GameResult} entity.
 */
public class GameResultDao extends GenericJpaDao<GameResult>{

    public GameResultDao() {
        super(GameResult.class);
    }

    /**
     * Returns the list of {@code n} most recent games played.
     *
     * @param n the maximum number of results to be returned
     * @return the list of {@code n} most recently played games
     */
    @Transactional
    public List<GameResult> listN(int n) {

        return entityManager.createQuery("SELECT r FROM GameResult r order by r.finished DESC", GameResult.class)
                .setMaxResults(n)
                .getResultList();


    }
}

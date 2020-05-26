package mills.results;

import com.google.inject.persist.Transactional;
import util.jpa.GenericJpaDao;

import java.util.List;

public class GameResultDao extends GenericJpaDao<GameResult>{

    public GameResultDao() {
        super(GameResult.class);
    }

    @Transactional
    public List<GameResult> listN(int n) {

        return entityManager.createQuery("SELECT r FROM GameResult r order by r.finished DESC", GameResult.class)
                .setMaxResults(n)
                .getResultList();


    }
}

package mills.stats;

import com.google.inject.persist.Transactional;
import util.jpa.GenericJpaDao;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

public class PlayerStatDao extends GenericJpaDao<PlayerStat> {

    public PlayerStatDao() {
        super(PlayerStat.class);
    }

    @Transactional
    public long getWins(String player) {

        Query qm= entityManager.createQuery("SELECT count(r) as wins FROM PlayerStat r where  r.player = :player and r.win=true").setParameter("player",player);

        return (long) qm.getSingleResult ();

    }

    @Transactional
    public Double getMoves(String player){
        Query qm= entityManager.createQuery("SELECT avg(r.moves) FROM PlayerStat r where  r.player = :player GROUP BY r.player").setParameter("player",player);

        return (Double) qm.getSingleResult();
    }

    @Transactional
    public Optional<PlayerStat> checkPlayerInDb(String player) {
        try {
        return Optional.of(entityManager.createQuery("SELECT r FROM PlayerStat r where  r.player = :player",PlayerStat.class).setParameter("player",player).setMaxResults(1).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }

    }

    @Transactional
    public long getGamesCount(String player){
        Query qm= entityManager.createQuery("SELECT count(r) FROM PlayerStat r where  r.player = :player GROUP BY r.player").setParameter("player",player);

        return (long) qm.getSingleResult();
    }


    @Transactional
    public long getAvgDuration(String player){
        List<PlayerStat> qm = entityManager.createQuery("SELECT r FROM PlayerStat r where r.player=:player", PlayerStat.class)
                .setParameter("player",player)
                .getResultList();

        long durationsum=0;

        for (PlayerStat ps: qm) {
            durationsum+=ps.getDuration().toMillis();
        }
        return durationsum/qm.size();
    }

    @Transactional
    public long getDraws(String player){
        Query qm= entityManager.createQuery("SELECT count(r) FROM PlayerStat r where  r.player = :player and r.draw=true").setParameter("player",player);

        return (long) qm.getSingleResult();

    }
}

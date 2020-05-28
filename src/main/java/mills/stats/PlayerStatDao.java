package mills.stats;

import com.google.inject.persist.Transactional;
import util.jpa.GenericJpaDao;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

/**
 * DAO class for the {@link PlayerStat} entity.
 */
public class PlayerStatDao extends GenericJpaDao<PlayerStat> {

    public PlayerStatDao() {
        super(PlayerStat.class);
    }

    /**
     * Returns the number of winning games the player has achieved
     *
     * @param player the name of the player whose statistics we are querying
     * @return the number of winning games of the player
     */
    @Transactional
    public long getWins(String player) {

        Query qm= entityManager.createQuery("SELECT count(r) as wins FROM PlayerStat r where  r.player = :player and r.win=true").setParameter("player",player);

        return (long) qm.getSingleResult ();

    }

    /**
     * Returns the number of average moves the player has made per game
     *
     * @param player the name of the player whose statistics we are querying
     * @return the number of average moves the player has made per game
     */
    @Transactional
    public Double getMoves(String player){
        Query qm= entityManager.createQuery("SELECT avg(r.moves) FROM PlayerStat r where  r.player = :player GROUP BY r.player").setParameter("player",player);

        return (Double) qm.getSingleResult();
    }

    /**
     * Checks whether the player is in the database or not
     *
     * @param player the name of the player we are looking for
     * @return {@code Optional<PlayerStat>} object if the player is in the database, {@code Optional.empty} otherwise
     */
    @Transactional
    public Optional<PlayerStat> checkPlayerInDb(String player) {
        try {
        return Optional.of(entityManager.createQuery("SELECT r FROM PlayerStat r where  r.player = :player",PlayerStat.class).setParameter("player",player).setMaxResults(1).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }

    }

    /**
     * Returns the number of total games the player has player
     *
     * @param player the name of the player whose statistics we are querying
     * @return the number of games the player has played
     */
    @Transactional
    public long getGamesCount(String player){
        Query qm= entityManager.createQuery("SELECT count(r) FROM PlayerStat r where  r.player = :player GROUP BY r.player").setParameter("player",player);

        return (long) qm.getSingleResult();
    }

    /**
     * Returns the average duration of the player's games
     *
     * @param player the name of the player whose statistics we are querying
     * @return the average duration of the player's games in Millis format
     */
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

    /**
     * Returns the number of draw games the player has had
     *
     * @param player the name of the player whose statistics we are querying
     * @return the number of draw games of the player
     */
    @Transactional
    public long getDraws(String player){
        Query qm= entityManager.createQuery("SELECT count(r) FROM PlayerStat r where  r.player = :player and r.draw=true").setParameter("player",player);

        return (long) qm.getSingleResult();

    }
}

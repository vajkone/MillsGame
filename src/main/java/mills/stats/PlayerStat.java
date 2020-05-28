package mills.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PlayerStat {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the player.
     */
    @Column(nullable = false)
    private String player;

    /**
     * Indicates whether the player has won or not.
     */
    private boolean win;

    /**
     * The number of moves the player has made.
     */
    private int moves;


    /**
     * Indicates whether the game has ended in a draw or not.
     */
    private boolean draw;

    /**
     * The duration of the game.
     */
    @Column(nullable = false)
    private Duration duration;


}

package mills.results;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class GameResult {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of player one.
     */
    @Column(nullable = false)
    private String player1;

    /**
     * The name of player two.
     */
    @Column(nullable = false)
    private String player2;

    /**
     * The name of the winner of the game.
     */

    private String winner;

    /**
     * The combined number of the moves the two players made.
     */
    private int moves;



    /**
     * The duration of the game.
    */
    @Column(nullable = false)
    private Duration duration;

    /**
     * The timestamp when the result was saved.
    */
    @Column(nullable = false)
    private ZonedDateTime finished;

    @PrePersist
    protected void onPersist() {
        finished = ZonedDateTime.now();
    }




}

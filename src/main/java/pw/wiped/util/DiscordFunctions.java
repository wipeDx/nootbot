package pw.wiped.util;

import net.dv8tion.jda.core.entities.Game;
import pw.wiped.Bot;

/**
 * Helper class which consists of all the functions that are exclusive to discord like changing the game played by the
 * bot.
 */
public class DiscordFunctions {

    /**
     * Changes the game the bot is currently playing.
     * @param game Game name
     */
    public static void changeGamePlayed (String game) {
        Bot.getJDA().getPresence().setGame(Game.of(game));
    }

}

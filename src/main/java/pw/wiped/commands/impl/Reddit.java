package pw.wiped.commands.impl;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import pw.wiped.Bot;
import pw.wiped.commands.AbstractCommand;
import pw.wiped.commands.Command;
import pw.wiped.util.permissions.Permissions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Reddit functionality
 */
public class Reddit extends AbstractCommand {

    public Reddit() {
        Bot.cmdMng.addCommand(new Command("Reddit", Permissions.MEMBER, "reddit", "r") {

            private JSONParser parser;
            private static final int MAX_NSFW_SKIPS = 20;

            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                String SUBREDDIT = "aww";
                String SETTING = "hot";

                if (args.length == 2 && (args[1].equals("hot") || args[1].equals("new"))) {
                    SUBREDDIT = args[0];
                    SETTING = args[1];
                } else if (args.length == 2)
                    SUBREDDIT = args[0];
                else if (args.length == 1 && (args[0].equals("hot") || args[0].equals("new")))
                    SETTING = args[0];
                else if (args.length == 1)
                    SUBREDDIT = args[0];

                boolean hot = SETTING.equals("hot");
                JSONObject reddit = null;
                try {
                    parser = new JSONParser();
                    reddit = (JSONObject) parser.parse(readUrl("https://reddit.com/r/"+SUBREDDIT+"/"+SETTING+".json?limit=" + (MAX_NSFW_SKIPS+1)));

                } catch (Exception ev) {
                    ev.printStackTrace();
                }

                int count = 0;
                JSONObject first = (JSONObject) reddit.get("data");
                JSONArray t = (JSONArray) first.get("children");
                first = (JSONObject) t.get(count);

                JSONObject t2 = (JSONObject) first.get("data");
                boolean isSticky = (boolean) t2.get("stickied");

                // Let through everything if channel is marked NSFW
                boolean over18;
                if (e.getChannelType() == ChannelType.PRIVATE)
                    over18 = false;
                else
                    over18 = (!e.getTextChannel().isNSFW() && (boolean) t2.get("over_18"));
                int i = 0;

                while ((isSticky || over18) && i < MAX_NSFW_SKIPS) {
                    first = (JSONObject) reddit.get("data");
                    JSONArray t3 = (JSONArray) first.get("children");
                    first = (JSONObject) t3.get(++count);
                    t2 = (JSONObject) first.get("data");
                    isSticky = (boolean) t2.get("stickied");
                    if (e.getChannelType() == ChannelType.PRIVATE)
                        over18 = false;
                    else
                        over18 = (!e.getTextChannel().isNSFW() && (boolean) t2.get("over_18"));
                    if (e.getChannelType() != ChannelType.PRIVATE && !e.getTextChannel().isNSFW()) {
                        i++;
                        LOG.info(i);
                    }
                }
                String textToSend;
                if (i == MAX_NSFW_SKIPS) {
                    textToSend = "Too many NSFW links. I went over the limit (" + MAX_NSFW_SKIPS +")! Try different settings";
                }
                else {
                    first = (JSONObject) first.get("data");
                    String imageurl = (String) first.get("url");
                    String title = (String) first.get("title");
                    String commentlink = (String) first.get("id");
                    textToSend = (hot ? "Hottest" : "Newest") + " /r/" + SUBREDDIT + ": " + title + (imageurl.contains("imgur.com") || imageurl.contains("i.redd.it") ? "\nDirect link: " + imageurl : "") + "\nComments: https://redd.it/" + commentlink;
                }
                e.getChannel().sendMessage(textToSend).complete();
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return true;
            }

            @Override
            public String help() {
                return "Returns whatever you want on reddit";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(0, 1, 2);
                sb.append("1 Argument:\n - hot / new: clarifies whether to get the hottest or newest from /r/aww");
                sb.append("\n 2 Arguments:\n - <subreddit>: Which subreddit to get the result from.");
                sb.append("\n - hot / new: clarifies whether to get the hottest or newest from /r/<subreddit>");
                sb.append("\n\n.r - Displays the currently hottest entry from /r/aww");
                sb.append("\n.r new - Displays the currently newest entry from /r/aww");
                sb.append("\n.reddit funny - Displays the currently hottest entry from /r/funny");
                sb.append("\n.reddit funny new - Displays the currently newest entry from /r/funny");
                sb.append("\n\nExcludes NSFW content! (Except the channel is NSFW)");
                return sb.toString();
            }

            private String readUrl(String urlString) throws Exception {
                LOG.info(urlString);
                URLConnection urlConnection = new URL(urlString).openConnection();
                urlConnection.addRequestProperty("User-Agent", "RedditCommand Noot-Bot V0.1 by /u/weiped");
                try (InputStream is = urlConnection.getInputStream()) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    StringBuilder buffer = new StringBuilder();
                    int read;
                    char[] chars = new char[1024];
                    while ((read = rd.read(chars)) != -1)
                        buffer.append(chars, 0, read);

                    return buffer.toString();
                }
            }
        });
    }

}

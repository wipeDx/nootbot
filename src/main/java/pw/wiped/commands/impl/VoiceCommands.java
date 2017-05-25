package pw.wiped.commands.impl;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import pw.wiped.Bot;
import pw.wiped.commands.AbstractCommand;
import pw.wiped.commands.Command;
import pw.wiped.util.Config;
import pw.wiped.util.audio.AudioPlayerSendHandler;
import pw.wiped.util.permissions.Permissions;
import pw.wiped.util.audio.TrackScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements every voice command there is.
 * This means youtube, playing local files etc etc!
 * Using https://github.com/sedmelluq/LavaPlayer
 */
public class VoiceCommands extends AbstractCommand {

    private static AudioPlayerManager apm = null;
    private static AudioPlayerSendHandler apsh;
    private static boolean isConnected;
    private static String connectedGuildID;
    private static TrackScheduler trackScheduler;

    private boolean isLocalFile (String param) throws IOException {
        File f = new File(Config.getSoundFolder().getName() + File.separator + param + ".mp3");
        return f.exists();
    }

    public VoiceCommands() {
        apm = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(apm);
        AudioPlayer ap = apm.createPlayer();
        apsh = new AudioPlayerSendHandler(ap);
        trackScheduler = new TrackScheduler(ap);
        isConnected = false;
        connectedGuildID = null;

        Bot.cmdMng.addCommand(new Command("Join channel", Permissions.MEMBER, "join") {
            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                //Convert param to first VoiceChannel possible
                //No check if it's actually a text channel, as we already did that in called()
                List<VoiceChannel> channel = e.getGuild().getVoiceChannelsByName(param, true);
                if (channel.size() == 0 && !e.getMember().getVoiceState().inVoiceChannel()) {
                    e.getChannel().sendMessage("Couldn't find that VoiceChannel").complete();
                }
                else if(isConnected && connectedGuildID.equals(e.getGuild().getId())) {
                    e.getChannel().sendMessage("I'm already connected somewhere else.").complete();
                }
                else if(param.equals("join") && e.getMember().getVoiceState().inVoiceChannel()) {
                    channel = new ArrayList<>();
                    channel.add(e.getMember().getVoiceState().getChannel());
                }

                AudioManager am = e.getGuild().getAudioManager();
                am.openAudioConnection(channel.get(0));
                am.setSendingHandler(apsh);
                isConnected = true;
                connectedGuildID = e.getGuild().getId();

            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return e.isFromType(ChannelType.TEXT) && param.length() > 0;
            }

            @Override
            public String help() {
                return "Joins a channel";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(0, 1);
                sb.append("Joins either the given channel name or the one you're connected to.");
                return sb.toString();
            }
        })
        .addCommand(new Command("Leave channel", Permissions.MEMBER, "leave") {
            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                AudioManager am = e.getGuild().getAudioManager();
                am.closeAudioConnection();
                isConnected = false;
                connectedGuildID = null;
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return e.isFromType(ChannelType.TEXT) && e.getGuild().getAudioManager().isConnected();
            }

            @Override
            public String help() {
                return "Leaves the channel";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(0, 1);
                sb.append("Leaves the channel Nootbot is connected to.");
                return sb.toString();
            }
        })
        .addCommand(new Command("Play sound", Permissions.MEMBER, "play") {
            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                if (connectedGuildID.equals(e.getGuild().getId())) {

                    String itemString;
                    try {
                        if (isLocalFile(param)) {
                            itemString = Config.getSoundFolder().getName() + File.separator + param + ".mp3";
                        }
                        else {
                            itemString = param;
                        }

                        apm.loadItem(itemString, new AudioLoadResultHandler() {
                            @Override
                            public void trackLoaded(AudioTrack track) {
                                trackScheduler.queue(track);
                            }

                            @Override
                            public void playlistLoaded(AudioPlaylist playlist) {
                                for (AudioTrack track : playlist.getTracks()) {
                                    trackScheduler.queue(track);
                                }
                            }

                            @Override
                            public void noMatches() {
                                e.getTextChannel().sendMessage("I didn't find anything!");
                            }

                            @Override
                            public void loadFailed(FriendlyException throwable) {
                                e.getTextChannel().sendMessage("Loading failed :disappointed_relieved:\nNotify the admin!!");
                                LOG.fatal(throwable.getMessage());
                            }
                        });
                    } catch (IOException e1) {
                        e.getTextChannel().sendMessage("I don't know what went wrong. RUN!");
                        e1.printStackTrace();
                    }
                    trackScheduler.nextTrack();
                }
                else {
                    e.getChannel().sendMessage("I'm connected somewhere else, can't play a sound from here!");
                }
            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return e.isFromType(ChannelType.TEXT) && args.length > 0;
            }

            @Override
            public String help() {
                return "Plays a sound from various sources";
            }

            @Override
            public String moreHelp() {
                StringBuilder sb = getHelpText(1);
                sb.append("Can play a sound from various sources.\nEven has a reservoir of local files!");
                return sb.toString();
            }
        });

    }

}

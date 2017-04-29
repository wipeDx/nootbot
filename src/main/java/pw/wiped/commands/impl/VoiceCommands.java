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
import pw.wiped.util.audio.AudioPlayerSendHandler;
import pw.wiped.util.permissions.Permissions;
import pw.wiped.util.audio.TrackScheduler;

/**
 * This class implements every voice command there is.
 * This means youtube, playing local files etc etc!
 * Using https://github.com/sedmelluq/LavaPlayer
 */
public class VoiceCommands extends AbstractCommand {

    private static AudioPlayerManager apm = null;
    private static AudioPlayer ap;
    private static AudioPlayerSendHandler apsh;
    private static boolean isConnected;
    private static String connectedGuildID;
    private static TrackScheduler trackScheduler;


    public VoiceCommands() {
        apm = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(apm);
        ap = apm.createPlayer();
        apsh = new AudioPlayerSendHandler(ap);
        trackScheduler = new TrackScheduler(ap);
        isConnected = false;
        connectedGuildID = null;

        Bot.cmdMng.addCommand(new Command("JoinChannel", Permissions.MEMBER, "join") {
            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                //Convert param to first VoiceChannel possible
                //No check if it's actually a text channel, as we already did that in called()
                VoiceChannel channel = e.getGuild().getVoiceChannelsByName(param, true).get(0);
                if (channel == null) {
                    e.getChannel().sendMessage("Couldn't find that VoiceChannel").complete();
                }
                else if(isConnected && connectedGuildID.equals(e.getGuild().getId())) {
                    e.getChannel().sendMessage("I'm already connected somewhere else.").complete();
                }
                else {
                    AudioManager am = e.getGuild().getAudioManager();
                    am.openAudioConnection(channel);
                    am.setSendingHandler(apsh);
                    isConnected = true;
                    connectedGuildID = e.getGuild().getId();
                }

            }

            @Override
            public boolean called(String param, String[] args, MessageReceivedEvent e) {
                return e.isFromType(ChannelType.TEXT) && param.length() > 0;
            }

            @Override
            public String help() {
                return null;
            }

            @Override
            public String moreHelp() {
                return null;
            }
        })
        .addCommand(new Command("LeaveChannel", Permissions.MEMBER, "leave") {
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
                return null;
            }

            @Override
            public String moreHelp() {
                return null;
            }
        })
        .addCommand(new Command("PlaySound", Permissions.MEMBER, "play") {
            @Override
            public void action(String param, String[] args, MessageReceivedEvent e) {
                if (connectedGuildID.equals(e.getGuild().getId())) {
                    apm.loadItem(param, new AudioLoadResultHandler() {
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
                            // Notify the user that we've got nothing
                        }

                        @Override
                        public void loadFailed(FriendlyException throwable) {
                            // Notify the user that everything exploded
                        }
                    });
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
                return null;
            }

            @Override
            public String moreHelp() {
                return null;
            }
        });

    }

}

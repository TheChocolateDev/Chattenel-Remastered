package me.callmefilms.CNLR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CNLRFront extends JavaPlugin{
	
//	Channel creations
	
	Channel global = new Channel("global");
	Channel local = new Channel("local");
	Channel teamOne = new Channel("team1");
	Channel teamTwo = new Channel("teamTwo");
	
//	Channel list
	
	List<Channel> channels = new ArrayList<Channel>();
	
//	onEnable()
//		Channels added to channel list
//		PlayerJoinEvent and AsyncPlayerChatEvent registered
//		/channel command executor set to Commands class
	
	public void onEnable() {
		
		this.channels.add(global);
		this.channels.add(local);
		this.channels.add(teamOne);
		this.channels.add(teamTwo);
		
		Bukkit.getServer().getPluginManager().registerEvents(new JoinListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ChatRunner(), this);
		
		Bukkit.getServer().getPluginCommand("channel").setExecutor(new Commands());
		
	}
	
//	getPlayerChannel(player) method
//		Runs a loop through the channels and returns the first channel holding the "player" parameter
	
	public Channel getPlayerChannel(Player player) {
		for(int i = 0; i < channels.size(); i++) {
			for(int j = 0; j < channels.get(i).getPlayers().size(); j++) {
				if(channels.get(i).getPlayers().get(j).getUniqueId().toString() == player.getUniqueId().toString()) {
					return channels.get(i);
				}
			}
		}
		return global;
	}
	
//	getPlayerChannes(playerUUID) method
//		Creates a channel list
//		Runs through the plugins' channel list and adds any channel containing a player with the
//	"playerUUID" Unique ID to the channels variable
//		Returns the channels variable
	
	public List<Channel> getPlayerChannels(String playerUUID) {
		List<Channel> channels = new ArrayList<Channel>();
		
		for(int i = 0; i < this.channels.size(); i++) {
			for(int j = 0; j < this.channels.get(i).getPlayers().size(); j++) {
				if(this.channels.get(i).getPlayers().get(j).getUniqueId().toString().equalsIgnoreCase(playerUUID)) {
					channels.add(this.channels.get(i));
				}
			}
		}
		
		return channels;
	}
	
//	playerIsInChannel(playerUUID, channel) method
//		Creates a player list called playerChans out of the getPlayerChannels(playerUUID) method
//		Runs through playerChans checking to see if any match the specified channel in the "channel" parameter
//		Returns true if the player is in the specified channel
	
	public boolean playerIsInChannel(String playerUUID, Channel channel) {
		List<Channel> playerChans = getPlayerChannels(playerUUID);
		for(int i = 0; i < playerChans.size(); i++) {
			if(playerChans.get(i) == channel) {
				return true;
			}
		}
		return false;
	}
	
//	switchChan(player, channel) method
//		Checks to see if player is in the specified channel in the "channel" parameter
//		Returns a message if the if statement is true, but otherwise removes the player
//		from all of the channels it is currently in, and adds the player to the specified
//	channel in the "channel" parameter
	
	public void switchChan(Player player, Channel channel) {
		String playerUUID = player.getUniqueId().toString();
		List<Channel> playerChans = getPlayerChannels(playerUUID);
		String cnlPrefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "Chattenel" + ChatColor.GRAY + "] ";
		if(playerIsInChannel(playerUUID, channel)) {
			player.sendMessage(cnlPrefix + ChatColor.YELLOW + "You are already in the " + ChatColor.GOLD + channel.getName() + ChatColor.YELLOW + " channel.");
		} else {
			for(int i = 0; i < playerChans.size(); i++) {
				playerChans.get(i).removePlayer(player);
			}
			channel.addPlayer(player);
			player.sendMessage(cnlPrefix + ChatColor.YELLOW + "Switched to " + ChatColor.GOLD + channel.getName() + ChatColor.YELLOW + " channel successfully.");
		}
	}
	
//	getChannel(String name)
//		Runs a loop through the channels list
//		An if statement checks in each loop to see if the current channel's name is the same
//	as the string specified as the "name" parameter
//		If the if statement is true, the channel is returned, but otherwise the method returns as null
	
	public Channel getChannel(String name) {
		for(int i = 0; i < channels.size(); i++) {
			if(channels.get(i).getName().equalsIgnoreCase(name)) {
				return channels.get(i);
			}
		}
		return null;
	}
	
	public Collection<? extends Player> getPlayersInChannel(Collection<? extends Player> playersToChange, Channel channel) {
		Iterator playerIt = Bukkit.getServer().getOnlinePlayers().iterator();
		List<Player> allPlayers = new ArrayList<Player>();
		do {
			Player currentPlayer = (Player) playerIt.next();
			allPlayers.add(currentPlayer);
		} while(playerIt.hasNext());
		for(int i = 0; i < allPlayers.size(); i++) {
			if(!(playerIsInChannel(allPlayers.get(i).getUniqueId().toString(), global))) {
				playersToChange.remove(allPlayers.get(i));
			}
		}
		return playersToChange;
	}
	
//	JoinListener class inside the CNLFront class, which implements the Listener interface
	
	public class JoinListener implements Listener {
		
//	onPlayerJoinEvent(event) method with @EventHandler
//		Runs a for loop looking for a channel containing the player and removes
//	the player from that channel
//		Adds the player to the global channel
		
		@EventHandler
		public void onPlayerJoinEvent(PlayerJoinEvent event) {
			Player player = event.getPlayer();
			for(int i = 0; i < channels.size(); i++) {
				for(int j = 0; j < channels.get(i).getPlayers().size(); j++) {
					if(channels.get(i).getPlayers().get(j).getUniqueId().toString() == player.getUniqueId().toString()) {
						channels.get(i).removePlayer(player);
					}
				}
			}
			global.addPlayer(player);
		}
		
	}
	
//	ChatRunner class inside the CNLFront class, which implements the Listener interface
	
	public class ChatRunner implements Listener {
		
//	onAsyncPlayerChatEvent(event) method with @EventHandler
//		Runs a loop to send a message to every player in each class a player
//	is in that contains a special prefix and the message given by the event
		
		@EventHandler
		public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
			Player player = event.getPlayer();
			String playerUUID = player.getUniqueId().toString();
			String message = event.getMessage();
			List<Channel> playerChans = getPlayerChannels(playerUUID);
			switch(playerChans.get(0).getName().toLowerCase()) {
			case "global":
				event.setFormat(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "Global" + ChatColor.GRAY + "] " + ChatColor.RESET + player.getDisplayName() + ChatColor.RESET + ChatColor.WHITE + ChatColor.BOLD + " >> " + ChatColor.RESET + event.getMessage());
				Player[] allOnlinePlayers = (Player[]) Bukkit.getServer().getOnlinePlayers().toArray();
				for(int i = 0; i < allOnlinePlayers.length; i++) {
					if(!(playerIsInChannel(allOnlinePlayers[i].getUniqueId().toString(), global))) {
						event.getRecipients().remove(allOnlinePlayers[i]);
					}
				}
				break;
			case "local":
				Player targetPlayer = local.getPlayers().get(1);
				Location targLoc = targetPlayer.getLocation();
				Location chatterLoc = player.getLocation();
				double xDif = targLoc.getX() - chatterLoc.getX();
				double yDif = targLoc.getY() - chatterLoc.getY();
				double zDif = targLoc.getZ() - chatterLoc.getZ();
				allOnlinePlayers = (Player[]) Bukkit.getServer().getOnlinePlayers().toArray();
				for(int i = 0; i < allOnlinePlayers.length; i++) {
					if((!((xDif <= 50) && (yDif <= 50) && (zDif <= 50))) || (!(playerIsInChannel(allOnlinePlayers[i].getUniqueId().toString(), global)))) {
						event.getRecipients().remove(allOnlinePlayers[i]);
					}
				}
				break;
			case "teamone":
				event.setFormat(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "T1" + ChatColor.GRAY + "] " + ChatColor.RESET + player.getDisplayName() + ChatColor.RESET + ChatColor.WHITE + ChatColor.BOLD + " >> " + ChatColor.RESET + event.getMessage());
				allOnlinePlayers = (Player[]) Bukkit.getServer().getOnlinePlayers().toArray();
				for(int i = 0; i < allOnlinePlayers.length; i++) {
					if(!(playerIsInChannel(allOnlinePlayers[i].getUniqueId().toString(), teamOne))) {
						event.getRecipients().remove(allOnlinePlayers[i]);
					}
				}
				break;
			case "teamtwo":
				event.setFormat(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "T2" + ChatColor.GRAY + "] " + ChatColor.RESET + player.getDisplayName() + ChatColor.RESET + ChatColor.WHITE + ChatColor.BOLD + " >> " + ChatColor.RESET + event.getMessage());
				allOnlinePlayers = (Player[]) Bukkit.getServer().getOnlinePlayers().toArray();
				for(int i = 0; i < allOnlinePlayers.length; i++) {
					if(!(playerIsInChannel(allOnlinePlayers[i].getUniqueId().toString(), teamTwo))) {
						event.getRecipients().remove(allOnlinePlayers[i]);
					}
				}				
				break;
			}
		}
		
	}
	
//	Commands class inside the CNLFront class, which implements the Listener interface
	
	public class Commands implements CommandExecutor {
		
//	onCommand(sndr, cmd, label, args) method
//		Checks to see if the CommandSender, specified by the "sndr" parameter, is a player; If it's not
//	an error message is sent, but otherwise a check is given to the command and it's arguments, with
//	specified instructions on what to do for each type of argument from the /channel command
		
		@Override
		public boolean onCommand(CommandSender sndr, Command cmd, String label, String[] args) {
			if(!(sndr instanceof Player)) {
				sndr.sendMessage("This command can not be executed by console personnel. Please try again in-game.");
			} else {
				Player player = (Player) sndr;
				String playerUUID = player.getUniqueId().toString();
				switch(cmd.getName()) {
				case "channel":
					if(args.length < 1) {
						player.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "------------" + ChatColor.GRAY + "]");
						player.sendMessage(ChatColor.GOLD + "/channel status:" + ChatColor.YELLOW + " Shows your current channel");
						player.sendMessage(ChatColor.GOLD + "/channel switch <channel>: " + ChatColor.YELLOW + "Switches your current channel");
						player.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "------------" + ChatColor.GRAY + "]");
					} else {
						switch(args[0].toLowerCase()) {
						case "status":
							List<Channel> playerChans = getPlayerChannels(playerUUID);
							Channel firstChan = playerChans.get(0);
							player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Chattenel" + ChatColor.GRAY + "] " + ChatColor.YELLOW + "You are in the " + ChatColor.GOLD + firstChan.getName() + ChatColor.YELLOW + " channel.");
							break;
						case "switch":
							if(args.length < 2) {
								player.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "------------" + ChatColor.GRAY + "]");
								player.sendMessage(ChatColor.GOLD + "Correct usage: " + ChatColor.YELLOW + "/channel switch <channel>");
								player.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "------------" + ChatColor.GRAY + "]");
							} else {
								switch(args[1].toLowerCase()) {
								case "global":
									switchChan(player, global);
									break;
								case "local":
									switchChan(player, local);
									break;
								case "team1":
									switchChan(player, teamOne);
									break;
								case "teamtwo":
									switchChan(player, teamTwo);
									break;
								default:
									player.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "------------" + ChatColor.GRAY + "]");
									player.sendMessage(ChatColor.GOLD + "Channels:");
									player.sendMessage(ChatColor.GOLD + "- " + ChatColor.YELLOW + "global");
									player.sendMessage(ChatColor.GOLD + "- " + ChatColor.YELLOW + "local");
									player.sendMessage(ChatColor.GOLD + "- " + ChatColor.YELLOW + "team1");
									player.sendMessage(ChatColor.GOLD + "- " + ChatColor.YELLOW + "teamTwo");
									player.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "------------" + ChatColor.GRAY + "]");
									break;
								}
							}
							break;
						default:
							player.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "------------" + ChatColor.GRAY + "]");
							player.sendMessage(ChatColor.GOLD + "/channel status:" + ChatColor.YELLOW + "Shows your current channel");
							player.sendMessage(ChatColor.GOLD + "/channel switch <channel>:" + ChatColor.YELLOW + "Switches your current channel");
							player.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "------------" + ChatColor.GRAY + "]");
						}
					}
				}
			}
			return true;
		}
		
	}
	
}
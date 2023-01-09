package net.luis;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.luis.common.data.json.JsonHelper;
import net.luis.language.Language;
import net.luis.language.LanguageFile;
import net.luis.language.Languages;
import net.luis.language.Translation;
import net.luis.utils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 *
 * @author Luis-st
 *
 */

public class Test {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) {
		LOGGER.info("");
		makeLanguageFiles();
	}
	
	private static void makeLanguageFiles() {
		List<Translation> english = Utils.make(Lists.newArrayList(), (list) -> {
			list.add(new Translation("client.constans.name", "Virtual Game Collection"));
			list.add(new Translation("window.error.continue", "Continue"));
			list.add(new Translation("screen.loading.title", "Loading Virtual Game Collection"));
			list.add(new Translation("screen.loading.loading.text", "Loading %1%%"));
			list.add(new Translation("screen.menu.multiplayer", "Multiplayer"));
			list.add(new Translation("screen.menu.settings", "Settings"));
			list.add(new Translation("screen.menu.login", "Login"));
			list.add(new Translation("screen.menu.profile", "Profile"));
			list.add(new Translation("window.login.register", "Register"));
			list.add(new Translation("window.login.username", "Username"));
			list.add(new Translation("window.login.password", "Password"));
			list.add(new Translation("window.login.confirm_password", "Confirm Password"));
			list.add(new Translation("window.login.login", "Login"));
			list.add(new Translation("window.login.guest", "Guest"));
			list.add(new Translation("window.login.name", "Name"));
			list.add(new Translation("window.login.user", "User"));
			list.add(new Translation("window.login.back", "Back"));
			list.add(new Translation("window.login.next", "Next"));
			list.add(new Translation("window.logout.logout", "Logout"));
			list.add(new Translation("account.constans.name", "Account Server"));
			list.add(new Translation("account.window.accounts", "Accounts"));
			list.add(new Translation("account.window.account", "%1%"));
			list.add(new Translation("account.window.user_data", "User data"));
			list.add(new Translation("account.window.name", "Display name: %1%"));
			list.add(new Translation("account.window.first_name", "First name: %1%"));
			list.add(new Translation("account.window.last_name", "Last name: %1%"));
			list.add(new Translation("account.window.mail", "Mail: %1%"));
			list.add(new Translation("account.window.birthday", "Birthday: %1%"));
			list.add(new Translation("account.window.system_data", "System data"));
			list.add(new Translation("account.window.id", "Id: %1%"));
			list.add(new Translation("account.window.uuid", "Universally unique id: %1%"));
			list.add(new Translation("account.window.type", "Account type: %1%"));
			list.add(new Translation("account.type.user", "User"));
			list.add(new Translation("account.type.guest", "Guest"));
			list.add(new Translation("account.type.test", "Test"));
			list.add(new Translation("account.type.unknown", "Unknown"));
			list.add(new Translation("account.window.create", "Create account"));
			list.add(new Translation("account.window.remove", "Remove account"));
			list.add(new Translation("account.window.refresh", "Refresh"));
			list.add(new Translation("account.window.close", "Close"));
			list.add(new Translation("window.create_account.name", "Display Name"));
			list.add(new Translation("window.create_account.password", "Password"));
			list.add(new Translation("window.create_account.confirm_password", "Confirm Password"));
			list.add(new Translation("window.create_account.first_name", "First Name"));
			list.add(new Translation("window.create_account.last_name", "Last Name"));
			list.add(new Translation("window.create_account.mail", "Mail"));
			list.add(new Translation("window.create_account.birthday", "Birthday"));
			list.add(new Translation("window.create_account.back", "Back"));
			list.add(new Translation("window.create_account.create", "Create"));
			list.add(new Translation("server.constans.name", "Virtual Game Collection Server"));
			list.add(new Translation("screen.multiplayer.server_host", "Server host"));
			list.add(new Translation("screen.multiplayer.server_port", "Server port"));
			list.add(new Translation("screen.multiplayer.connect", "Connect"));
			list.add(new Translation("screen.multiplayer.connect_local", "Connect local"));
			list.add(new Translation("settings.language.name", "Language"));
			list.add(new Translation("settings.language.description", "Language for the user interface"));
			list.add(new Translation("server.window.server", "Server"));
			list.add(new Translation("server.window.server_host", "Server host: %1%"));
			list.add(new Translation("server.window.server_port", "Server port: %1%"));
			list.add(new Translation("server.window.server_admin", "Server admin: %1%"));
			list.add(new Translation("server.window.players", "Players"));
			list.add(new Translation("server.window.player", "%1%'s player"));
			list.add(new Translation("server.window.player_name", "Name: %1%"));
			list.add(new Translation("server.window.player_uuid", "Id: %1%"));
			list.add(new Translation("server.window.player_admin", "Admin: %1%"));
			list.add(new Translation("server.window.player_playing", "Playing: %1%"));
			list.add(new Translation("screen.game.local_player", "%1% (You)"));
			list.add(new Translation("screen.game.local_player_admin", "%1% (You) (Admin)"));
			list.add(new Translation("screen.game.remote_player", "%1%"));
			list.add(new Translation("screen.game.remote_player_admin", "%1% (Admin)"));
			list.add(new Translation("screen.lobby.game", "Game"));
			list.add(new Translation("screen.lobby.leave", "Leave"));
			list.add(new Translation("screen.lobby.ttt", "Tic Tac Toe"));
			list.add(new Translation("screen.player_select.play", "Play"));
			list.add(new Translation("screen.tic_tac_toe.confirm_action", "Confirm action"));
			list.add(new Translation("screen.tic_tac_toe.play_again", "Play again"));
			list.add(new Translation("screen.tic_tac_toe.cross_player", "Player (X): %1%"));
			list.add(new Translation("screen.tic_tac_toe.circle_player", "Player (O): %1%"));
			list.add(new Translation("screen.tic_tac_toe.no_player", "Player: %1%"));
			list.add(new Translation("screen.tic_tac_toe.player_info", "Player info"));
			list.add(new Translation("screen.tic_tac_toe.player_score", "%1%: %2%"));
			list.add(new Translation("screen.tic_tac_toe.no_data", "No data available yet"));
			list.add(new Translation("screen.tic_tac_toe.current_player", "Current player: %1%"));
			list.add(new Translation("screen.tic_tac_toe.no_current_player", "Current player: no"));
			list.add(new Translation("screen.lobby.ludo", "Ludo"));
			list.add(new Translation("screen.ludo.players", "Players"));
			list.add(new Translation("screen.ludo.green_player", "Green player: %1%"));
			list.add(new Translation("screen.ludo.yellow_player", "Yellow player: %1%"));
			list.add(new Translation("screen.ludo.blue_player", "Blue player: %1%"));
			list.add(new Translation("screen.ludo.red_player", "Red player: %1%"));
			list.add(new Translation("screen.ludo.no_player", "Player: %1%"));
			list.add(new Translation("screen.lobby.4wins", "4 Wins"));
			list.add(new Translation("screen.win4.yellow_player", "Yellow player: %1%"));
			list.add(new Translation("screen.win4.red_player", "Red player: %1%"));
		});
		List<Translation> german = Utils.make(Lists.newArrayList(), (list) -> {
			list.add(new Translation("client.constans.name", "Virtuelle Spielesammlung"));
			list.add(new Translation("window.error.continue", "Weiter"));
			list.add(new Translation("screen.loading.title", "Virtuelle Spielesammlung wird geladen"));
			list.add(new Translation("screen.loading.loading.text", "Wird geladen %1%%"));
			list.add(new Translation("screen.menu.multiplayer", "Mehrspieler"));
			list.add(new Translation("screen.menu.settings", "Einstellungen"));
			list.add(new Translation("screen.menu.login", "Anmeldung"));
			list.add(new Translation("screen.menu.profile", "Profil"));
			list.add(new Translation("window.login.register", "Registrieren"));
			list.add(new Translation("window.login.username", "Nutzername"));
			list.add(new Translation("window.login.password", "Passwort"));
			list.add(new Translation("window.login.confirm_password", "Passwort best�tigen"));
			list.add(new Translation("window.login.login", "Anmelden"));
			list.add(new Translation("window.login.guest", "Gast"));
			list.add(new Translation("window.login.name", "Name"));
			list.add(new Translation("window.login.user", "Benutzer"));
			list.add(new Translation("window.login.back", "Zurück"));
			list.add(new Translation("window.login.next", "Weiter"));
			list.add(new Translation("window.logout.logout", "Abmelden"));
			list.add(new Translation("account.constans.name", "Account Server"));
			list.add(new Translation("account.window.accounts", "Konten"));
			list.add(new Translation("account.window.account", "%1%"));
			list.add(new Translation("account.window.user_data", "Benutzerdaten"));
			list.add(new Translation("account.window.name", "Anzeige name: %1%"));
			list.add(new Translation("account.window.first_name", "Vorname: %1%"));
			list.add(new Translation("account.window.last_name", "Nachname: %1%"));
			list.add(new Translation("account.window.mail", "Mail: %1%"));
			list.add(new Translation("account.window.birthday", "Geburtstag: %1%"));
			list.add(new Translation("account.window.system_data", "Systemdaten"));
			list.add(new Translation("account.window.id", "Id: %1%"));
			list.add(new Translation("account.window.uuid", "Universell eindeutige id: %1%"));
			list.add(new Translation("account.window.type", "Konto typ: %1%"));
			list.add(new Translation("account.type.user", "Benutzer"));
			list.add(new Translation("account.type.guest", "Gast"));
			list.add(new Translation("account.type.test", "Test"));
			list.add(new Translation("account.type.unknown", "Unbekannt"));
			list.add(new Translation("account.window.create", "Konto erstellen"));
			list.add(new Translation("account.window.remove", "Konto löschen"));
			list.add(new Translation("account.window.refresh", "Aktualisieren"));
			list.add(new Translation("account.window.close", "Schlie�en"));
			list.add(new Translation("window.create_account.name", "Anzeige Name"));
			list.add(new Translation("window.create_account.password", "Passwort"));
			list.add(new Translation("window.create_account.confirm_password", "Passwort Bestätigen"));
			list.add(new Translation("window.create_account.first_name", "Vorname"));
			list.add(new Translation("window.create_account.last_name", "Nachname"));
			list.add(new Translation("window.create_account.mail", "Mail"));
			list.add(new Translation("window.create_account.birthday", "Geburtstag"));
			list.add(new Translation("window.create_account.back", "Zurück"));
			list.add(new Translation("window.create_account.create", "Erstellen"));
			list.add(new Translation("server.constans.name", "Virtual Game Collection Server"));
			list.add(new Translation("screen.multiplayer.server_host", "Server host"));
			list.add(new Translation("screen.multiplayer.server_port", "Server port"));
			list.add(new Translation("screen.multiplayer.connect", "Verbinden"));
			list.add(new Translation("screen.multiplayer.connect_local", "Lokal verbinden"));
			list.add(new Translation("settings.language.name", "Sprache"));
			list.add(new Translation("settings.language.description", "Sprache für die Benutzeroberfläche"));
			list.add(new Translation("server.window.server", "Server"));
			list.add(new Translation("server.window.server_host", "Server host: %1%"));
			list.add(new Translation("server.window.server_port", "Server port: %1%"));
			list.add(new Translation("server.window.server_admin", "Server Administrator: %1%"));
			list.add(new Translation("server.window.players", "Spieler"));
			list.add(new Translation("server.window.player", "%1%'s spieler"));
			list.add(new Translation("server.window.player_name", "Name: %1%"));
			list.add(new Translation("server.window.player_uuid", "Id: %1%"));
			list.add(new Translation("server.window.player_admin", "Administrator: %1%"));
			list.add(new Translation("server.window.player_playing", "Spielen: %1%"));
			list.add(new Translation("screen.game.local_player", "%1% (Du)"));
			list.add(new Translation("screen.game.local_player_admin", "%1% (Du) (Administrator)"));
			list.add(new Translation("screen.game.remote_player", "%1%"));
			list.add(new Translation("screen.game.remote_player_admin", "%1% (Administrator)"));
			list.add(new Translation("screen.lobby.game", "Spiel"));
			list.add(new Translation("screen.lobby.leave", "Verlassen"));
			list.add(new Translation("screen.lobby.ttt", "Tic Tac Toe"));
			list.add(new Translation("screen.player_select.play", "Play"));
			list.add(new Translation("screen.tic_tac_toe.confirm_action", "Aktion bestätigen"));
			list.add(new Translation("screen.tic_tac_toe.play_again", "Nochmal abspielen"));
			list.add(new Translation("screen.tic_tac_toe.cross_player", "Spieler (X): %1%"));
			list.add(new Translation("screen.tic_tac_toe.circle_player", "Spieler (O): %1%"));
			list.add(new Translation("screen.tic_tac_toe.no_player", "Spieler: %1%"));
			list.add(new Translation("screen.tic_tac_toe.player_info", "Spielerinfo"));
			list.add(new Translation("screen.tic_tac_toe.player_score", "%1%: %2%"));
			list.add(new Translation("screen.tic_tac_toe.no_data", "Noch keine Daten verfügbar"));
			list.add(new Translation("screen.tic_tac_toe.fail_data", "Daten konnten nicht geladen werden"));
			list.add(new Translation("screen.tic_tac_toe.current_player", "Aktueller Spieler: %1%"));
			list.add(new Translation("screen.tic_tac_toe.no_current_player", "Aktueller Spieler: keiner"));
			list.add(new Translation("screen.lobby.ludo", "Mensch ärgere dich nicht"));
			list.add(new Translation("screen.ludo.players", "Spieler"));
			list.add(new Translation("screen.ludo.green_player", "Grüner Spieler: %1%"));
			list.add(new Translation("screen.ludo.yellow_player", "Gelber Spieler: %1%"));
			list.add(new Translation("screen.ludo.blue_player", "Blauer Spieler: %1%"));
			list.add(new Translation("screen.ludo.red_player", "Roter Spieler: %1%"));
			list.add(new Translation("screen.ludo.no_player", "Spieler: %1%"));
			list.add(new Translation("screen.lobby.4wins", "4 Gewinnt"));
			list.add(new Translation("screen.win4.yellow_player", "Gelber Spieler: %1%"));
			list.add(new Translation("screen.win4.red_player", "Roter Spieler: %1%"));
		});
		saveLanguageFile(Languages.EN_US, english);
		saveLanguageFile(Languages.DE_DE, german);
	}
	
	private static void saveLanguageFile(Language language, List<Translation> translations) {
		LanguageFile languageFile = new LanguageFile(translations, language);
		Function<LanguageFile, DataResult<JsonElement>> function = JsonOps.INSTANCE.withEncoder(LanguageFile.CODEC);
		Optional<JsonElement> optional = function.apply(languageFile).result();
		optional.ifPresent(jsonElement -> JsonHelper.save(new GsonBuilder().setPrettyPrinting().create(), jsonElement, new File(System.getProperty("user.home")).toPath().resolve("Desktop/" + language.fileName() + ".json")));
	}
	
}

package net.vgc;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.vgc.data.json.JsonHelper;
import net.vgc.language.Language;
import net.vgc.language.LanguageFile;
import net.vgc.language.Languages;
import net.vgc.language.Translation;
import net.vgc.util.Util;

public class Test {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) {
		makeLanguageFiles();
	}
	
	protected static void makeLanguageFiles() {
		List<Translation> english = Util.make(Lists.newArrayList(), (list) -> {
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
			list.add(new Translation("window.logout.logout", "Logout"));
			list.add(new Translation("account.constans.name", "Account Server"));
			list.add(new Translation("account.window.accounts", "Accounts"));
			list.add(new Translation("account.window.account", "%1%'s Account"));
			list.add(new Translation("account.window.account_name", "Name: %1%"));
			list.add(new Translation("account.window.account_password", "Password: %1%"));
			list.add(new Translation("account.window.account_uuid", "Id: %1%"));
			list.add(new Translation("account.window.account_guest", "Guest: %1%"));
			list.add(new Translation("account.window.account_taken", "Taken: %1%"));
			list.add(new Translation("account.window.create", "Create account"));
			list.add(new Translation("account.window.remove", "Remove account"));
			list.add(new Translation("account.window.refresh", "Refresh"));
			list.add(new Translation("account.window.close", "Close"));
			list.add(new Translation("window.create_account.true", "Yes"));
			list.add(new Translation("window.create_account.false", "No"));
			list.add(new Translation("server.constans.name", "Virtual Game Collection Server"));
			list.add(new Translation("screen.multiplayer.server_host", "Server host"));
			list.add(new Translation("screen.multiplayer.server_port", "Server port"));
			list.add(new Translation("screen.multiplayer.connect", "Connect"));
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
			list.add(new Translation("screen.game.local_player", "%1% (You)"));
			list.add(new Translation("screen.game.local_player_admin", "%1% (You) (Admin)"));
			list.add(new Translation("screen.game.remote_player", "%1%"));
			list.add(new Translation("screen.game.remote_player_admin", "%1% (Admin)"));
			list.add(new Translation("screen.lobby.game", "Game"));
			list.add(new Translation("screen.lobby.leave", "Leave"));
			list.add(new Translation("screen.lobby.ttt", "Tic Tac Toe"));
			list.add(new Translation("screen.player_select.play", "Play"));
		});
		List<Translation> german = Util.make(Lists.newArrayList(), (list) -> {
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
			list.add(new Translation("window.login.confirm_password", "Passwort bestätigen"));
			list.add(new Translation("window.login.login", "Anmelden"));
			list.add(new Translation("window.login.guest", "Gast"));
			list.add(new Translation("window.login.name", "Name"));
			list.add(new Translation("window.login.user", "Benutzer"));
			list.add(new Translation("window.login.back", "Zurück"));
			list.add(new Translation("window.logout.logout", "Abmelden"));
			list.add(new Translation("account.constans.name", "Account Server"));
			list.add(new Translation("account.window.accounts", "Konten"));
			list.add(new Translation("account.window.account", "%1%'s Konto"));
			list.add(new Translation("account.window.account_name", "Name: %1%"));
			list.add(new Translation("account.window.account_password", "Passwort: %1%"));
			list.add(new Translation("account.window.account_uuid", "Id: %1%"));
			list.add(new Translation("account.window.account_guest", "Gast: %1%"));
			list.add(new Translation("account.window.account_taken", "Verwendet: %1%"));
			list.add(new Translation("account.window.create", "Konto erstellen"));
			list.add(new Translation("account.window.remove", "Konto löschen"));
			list.add(new Translation("account.window.refresh", "Aktualisieren"));
			list.add(new Translation("account.window.close", "Schließen"));
			list.add(new Translation("window.create_account.true", "Ja"));
			list.add(new Translation("window.create_account.false", "Nein"));
			list.add(new Translation("server.constans.name", "Virtual Game Collection Server"));
			list.add(new Translation("screen.multiplayer.server_host", "Server host"));
			list.add(new Translation("screen.multiplayer.server_port", "Server port"));
			list.add(new Translation("screen.multiplayer.connect", "Verbinden"));
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
			list.add(new Translation("screen.game.local_player", "%1% (Du)"));
			list.add(new Translation("screen.game.local_player_admin", "%1% (Du) (Administrator)"));
			list.add(new Translation("screen.game.remote_player", "%1%"));
			list.add(new Translation("screen.game.remote_player_admin", "%1% (Administrator)"));
			list.add(new Translation("screen.lobby.game", "Spiel"));
			list.add(new Translation("screen.lobby.leave", "Verlassen"));
			list.add(new Translation("screen.lobby.ttt", "Tic Tac Toe"));
			list.add(new Translation("screen.player_select.play", "Play"));
		});
		saveLanguageFile(Languages.EN_US, english);
		saveLanguageFile(Languages.DE_DE, german);
	}
	
	protected static void saveLanguageFile(Language language, List<Translation> translations) {
		LanguageFile languageFile = new LanguageFile(language, translations);
		Function<LanguageFile, DataResult<JsonElement>> function = JsonOps.INSTANCE.withEncoder(LanguageFile.CODEC);
		Optional<JsonElement> optional = function.apply(languageFile).result();
		if (optional.isPresent()) {
			JsonHelper.save(new GsonBuilder().setPrettyPrinting().create(), optional.get(), new File(System.getProperty("user.home")).toPath().resolve("Desktop/" + language.getFileName() + ".json"));
		}
	}

}
